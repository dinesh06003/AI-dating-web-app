package com.dinesh.ai_dating_backend.service;


import com.dinesh.ai_dating_backend.repo.ProfileRepo;
import com.dinesh.ai_dating_backend.config.UserProfileProperties;
import com.dinesh.ai_dating_backend.model.Profile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import static com.dinesh.ai_dating_backend.service.Utils.generateMyersBriggsTypes;
import static com.dinesh.ai_dating_backend.service.Utils.selfieTypes;

@Service
public class ProfileCreationService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileCreationService.class);

    private final OpenAiChatModel chatModel;

    private final HttpClient httpClient;

    private final HttpRequest.Builder stableDiffusionRequestBuilder;

    private List<Profile> generatedProfiles = new ArrayList<>();

    private static final String PROFILES_FILE_PATH = "profiles.json";

    @Value("${startup-actions.initializeProfiles}")
    private Boolean initializerProfiles;

    @Value("${lookingForGender}")
    private String lookingForGender;

    private final UserProfileProperties userProfileProperties;


    private final ProfileRepo profileRepo;

    private static final String STABLE_DIFFUSION_URL = "https://fe97a6a77c5448ab52.gradio.live/sdapi/v1/txt2img";

    public ProfileCreationService(OpenAiChatModel chatModel, UserProfileProperties userProfileProperties, ProfileRepo profileRepo) {
        this.chatModel = chatModel;
        this.userProfileProperties = userProfileProperties;
        this.httpClient = HttpClient.newHttpClient();
        this.stableDiffusionRequestBuilder = HttpRequest.newBuilder()
                .setHeader("Content-type", "application/json")
                .uri(URI.create(STABLE_DIFFUSION_URL));
        this.profileRepo = profileRepo;
    }


    public void createProfiles(int numberOfProfiles) {

        if (!this.initializerProfiles) {
            return;
        }

        List<Integer> ages = new ArrayList<>();
        for (int i = 20; i <= 35; i++) {
            ages.add(i);
        }

        List<String> ethnicities = new ArrayList<>(List.of("White", "Black", "Asian", "Indian", "Hispanic"));
        List<String> myersBriggsPersonalityTypes = generateMyersBriggsTypes();

        String gender = this.lookingForGender;

        while (this.generatedProfiles.size() < numberOfProfiles) {
            int age = getRandomElement(ages);
            String ethnicity = getRandomElement(ethnicities);
            String personalityType = getRandomElement(myersBriggsPersonalityTypes);
            String prompt = "Create a Tinder profile persona of an " + personalityType + " " + age + " year old " + ethnicity + " " + gender + " "
                    + " including the first name, last name, Myers Briggs Personality type and a tinder bio. Save the profile using the saveProfile function";

            System.out.println("Creating profile with prompt: " + prompt);

            ChatResponse response = chatModel.call(new Prompt(prompt,
                    OpenAiChatOptions.builder().function("saveProfile").build()));

            System.out.println("Response from chat model: " + response.getResult().getOutput().getText()+" ****The end****");

        }
        saveProfilesToJson(this.generatedProfiles);
    }

    @Bean
    @Description("Save the Tinder Profile information")
    public Function<Profile, Boolean> saveProfile() {
        return (Profile profile) -> {
//            System.out.println("This is the function that we expect to be called by Spring AI by looking at the OpenAI response");
            System.out.println(profile);
            this.generatedProfiles.add(profile);
            return true;
        };
    }

    private void saveProfilesToJson(List<Profile> generatedProfiles) {
        logger.info("SAVE PROFILES TO JSON FUNCTION CALLED");

        try {
            Gson gson = new Gson();
            List<Profile> existingProfiles = gson.fromJson(
                    new FileReader(PROFILES_FILE_PATH), new TypeToken<ArrayList<Profile>>() {}.getType()
            );

            System.out.println("*************************");
            if (existingProfiles != null) {
                generatedProfiles.addAll(existingProfiles);
            }

            List<Profile> profilesWithImages = new ArrayList<>();
            for (Profile profile : generatedProfiles) {
                if (profile.imageUrl() == null || profile.imageUrl().isEmpty()) {
                    profile = generateProfileImage(profile);
                }
                profilesWithImages.add(profile);
            }
            String jsonString = gson.toJson(profilesWithImages);
            FileWriter writer = new FileWriter(PROFILES_FILE_PATH);
            writer.write(jsonString);
            writer.close();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Profile generateProfileImage(Profile profile) {
        String uuid = StringUtils.isAllBlank(profile.id()) ? UUID.randomUUID().toString() : profile.id();
        profile = new Profile(
                uuid,
                profile.firstName(), profile.lastName(),
                profile.age(), profile.ethnicity(),
                profile.gender(), profile.bio(),
                uuid + ".jpg", profile.myersBriggsPersonalityType()
        );
        String randomSelfieType = getRandomElement(selfieTypes());


        String prompt = String.format("Selfie of a %d year old %s %s %s, %s, photorealistic skin texture and details, individual hairs and pores visible, highly detailed, photorealistic, hyperrealistic, subsurface scattering, 4k DSLR, ultrarealistic, best quality, masterpiece. Bio- %s",
                profile.age(),
                profile.myersBriggsPersonalityType(),
                profile.ethnicity(),
                profile.gender(),
                randomSelfieType,
                profile.bio());

        String negativePrompt = "multiple faces, lowres, text, error, cropped, worst quality, low quality, jpeg artifacts, ugly, duplicate, morbid, mutilated, out of frame, extra fingers, mutated hands, poorly drawn hands, poorly drawn face, mutation, deformed, blurry, dehydrated, bad anatomy, bad proportions, extra limbs, cloned face, disfigured, gross proportions, malformed limbs, missing arms, missing legs, extra arms, extra legs, fused fingers, too many fingers, long neck, username, watermark, signature";


        String jsonString = String.format("""
                {
                    "prompt": "%s",
                    "negative_prompt": "%s",
                    "steps": 40
                }
                """, prompt, negativePrompt);
        System.out.printf("Creating image for %s %s %s%n", profile.firstName(), profile.lastName(), profile.ethnicity());

        HttpRequest httpRequest = this.stableDiffusionRequestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonString)).build();

        HttpResponse<String> response;
        try {
            response = this.httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
        } catch (IOException |InterruptedException e) {
            throw new RuntimeException(e);
        }
        record ImageResponse(List<String> images) {}

        Gson gson = new Gson();
        ImageResponse imageResponse = gson.fromJson(response.body(), ImageResponse.class);
        System.out.println("creating image" + imageResponse);
        if(imageResponse.images() !=null && !imageResponse.images().isEmpty()){
            String base64Image = imageResponse.images().getFirst();
            System.out.println("base64Image output: "+base64Image);
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            String directoryPath = "src/main/resource/static/images";
            String filePath = directoryPath + profile.imageUrl();
            Path directory = Paths.get(directoryPath);

            if(!Files.exists(directory)){
                try {
                    Files.createDirectories(directory);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try (FileOutputStream imageOutFile = new FileOutputStream(filePath)){
                imageOutFile.write(imageBytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        return profile;


    }

    private <T> T getRandomElement(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }




    public void saveProfilesToDB() {

        Gson gson = new Gson();
        try {
            List<Profile> existingProfiles = gson.fromJson(
                    new FileReader(PROFILES_FILE_PATH), new TypeToken<ArrayList<Profile>>() {}.getType()
            );
            profileRepo.deleteAll();
//            List<Profile> sorted = existingProfiles.stream()
//                    .sorted(Comparator.comparing(Profile::firstName, Comparator.nullsLast(String::compareTo)))
//                            .collect(Collectors.toList());
//            System.out.println("Existing profiles: " + existingProfiles.size());
            profileRepo.saveAll(existingProfiles);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Profile profile = userProfileProperties.toProfile();
        System.out.println(userProfileProperties);
        profileRepo.save(profile);
    }
}
