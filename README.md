# 🧠 Tinder with AI — Smart Matchmaking Web App

This is an AI-powered dating application inspired by Tinder. It generates intelligent personas and enables dynamic conversations using OpenAI and Ollama models. Built using Spring Boot (Java) and React, the project is fully containerized and uses MongoDB for persistent storage.

---

## 🛠 Tech Stack

- **Frontend**: React, Tailwind CSS
- **Backend**: Java 17, Spring Boot, Spring AI
- **Database**: MongoDB (via Docker)
- **AI Models**: OpenAI GPT-4o, Ollama (LLaMA family)
- **Image Generation**: Stable Diffusion (via external API)
- **Containerization**: Docker

---

## 📦 Features

- 🔄 AI-generated dating profiles with personalized bios and images
- 💘 Swipe to match (Tinder-style)
- 💬 Chat interface with persona-driven AI conversations
- 🧠 GPT-4o integration via Spring AI
- 🧾 Data persistence using MongoDB
- 🧑‍🎨 Profile selfies generated using image generation API

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/dinesh06003/tinder-with-ai.git
cd tinder-with-ai
```

### 2. Start services with Docker

- Make sure your docker engine is running

### 3.Start the backend (Spring Boot)
-   use your Open Api key in application.properties

This starts:
- MongoDB
- Ollama AI server


### 4. Start the frontend (React)

```bash
cd tinder-ai-frontend
npm install
npm run dev
```



