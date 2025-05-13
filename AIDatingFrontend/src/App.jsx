import './App.css';
import { User, MessageCircle, X, Heart, Send } from 'lucide-react';
import React, { useState, useEffect } from 'react';

const fetchRandomProfile = async () => {
  const response = await fetch('http://localhost:8080/profiles/random');
  if (!response.ok) throw new Error('Failed to fetch profile');
  return response.json();
};

const saveSwipe = async (profileId) => {
  const response = await fetch('http://localhost:8080/matches', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ profileId }),
  });
  if (!response.ok) throw new Error('Failed to save swipe');
};

const fetchMatches = async () => {
  const response = await fetch('http://localhost:8080/matches');
  if (!response.ok) throw new Error('Failed to fetch matches');
  return response.json();
};

const fetchConversation = async (conversationId) => {
  const response = await fetch(`http://localhost:8080/conversations/${conversationId}`);
  if (!response.ok) throw new Error('Failed to fetch conversation');
  return response.json();
};

const sendMessage = async (conversationId, message) => {
  const response = await fetch(`http://localhost:8080/conversations/${conversationId}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ messageText: message, authorId: "user" }),
  });
  if (!response.ok) throw new Error('Failed to submit message');
  return response.json();
};

const ProfileSelector = ({ profile, onSwipe }) => (
  profile ? (
    <div className="rounded-2xl overflow-hidden bg-white shadow-xl hover:shadow-2xl transition-transform duration-300 hover:scale-105 border border-gray-100">
      <div className="relative">
        <img src={`http://localhost:8080/images/${profile.imageUrl}`} className="w-full object-cover" />
        <div className="absolute bottom-0 left-0 right-0 text-white p-5 bg-gradient-to-t from-black/80 via-black/50 to-transparent">
          <h2 className='text-3xl font-bold'>{profile.firstName} {profile.lastName}, {profile.age}</h2>
        </div>
      </div>
      <div className="p-5">
        <p className="text-gray-700 text-lg">{profile.bio}</p>
      </div>
      <div className="p-4 flex justify-center space-x-6">
        <button className="bg-gradient-to-br from-red-500 to-pink-500 text-white rounded-full p-4 shadow-xl hover:scale-110 transition-transform"
          onClick={() => onSwipe(profile.id, "left")}>
          <X size={24} />
        </button>
        <button className="bg-gradient-to-br from-green-400 to-emerald-500 text-white rounded-full p-4 shadow-xl hover:scale-110 transition-transform"
          onClick={() => onSwipe(profile.id, "right")}>
          <Heart size={24} />
        </button>
      </div>
    </div>
  ) : <div>Loading...</div>
);

const MatchesList = ({ matches, onSelectMatch }) => (
  <div className="rounded-xl shadow-xl p-6 bg-white">
    <h2 className="text-2xl font-bold mb-4 text-gray-800">Matches</h2>
    <ul>
      {matches.map((match, index) => (
        <li key={index} className="mb-3">
          <button className="w-full hover:bg-gray-100 rounded-xl flex items-center p-2 transition-colors"
            onClick={() => onSelectMatch(match.profile, match.conversationId)}>
            <img src={`http://localhost:8080/images/${match.profile.imageUrl}`} className="w-16 h-16 rounded-full mr-3 object-cover" />
            <span>
              <h3 className='font-bold text-lg text-gray-800'>{match.profile.firstName} {match.profile.lastName}</h3>
            </span>
          </button>
        </li>
      ))}
    </ul>
  </div>
);

const ChatScreen = ({ currentMatch, conversation, refreshState }) => {
  const [input, setInput] = useState('');

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend(conversation, input);
    }
  };

  const handleSend = async (conversation, input) => {
    if (input.trim()) {
      await sendMessage(conversation.id, input);
      setInput('');
    }
    refreshState();
  };

  return currentMatch ? (
    <div className='rounded-xl shadow-xl p-6 bg-white'>
      <h2 className="text-2xl font-bold mb-4 text-gray-800">
        Chat with {currentMatch.firstName} {currentMatch.lastName}
      </h2>
      <div className="h-[50vh] border rounded-lg overflow-y-auto mb-6 p-4 bg-gray-50">
        {conversation.messages.map((message, index) => (
          <div key={index} className={`flex ${message.authorId === 'user' ? 'justify-end' : 'justify-start'} mb-4`}>
            <div className={`flex items-end ${message.authorId === 'user' ? 'flex-row-reverse' : 'flex-row'}`}>
              {message.authorId === 'user' ? (
                <User size={18} className="text-indigo-500" />
              ) : (
                <img
                  src={`http://localhost:8080/images/${currentMatch.imageUrl}`}
                  className="w-11 h-11 rounded-full"
                />
              )}
              <div
                className={`max-w-xs px-5 py-3 rounded-3xl shadow-md ${
                  message.authorId === 'user'
                    ? 'bg-indigo-500 text-white ml-2'
                    : 'bg-gray-200 text-gray-800 mr-2'
                }`}
              >
                {message.messageText}
              </div>
            </div>
          </div>
        ))}
      </div>
      <div className="flex items-center">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyPress={handleKeyPress}
          className="flex-1 border border-gray-300 rounded-full px-4 py-2 shadow-inner focus:ring-2 focus:ring-indigo-400 focus:outline-none mr-2"
          placeholder="Type a message..."
        />
        <button
          className="bg-indigo-500 text-white rounded-full p-2 hover:bg-indigo-600 transition-colors duration-200 shadow-md"
          onClick={() => handleSend(conversation, input)}
        >
          <Send size={24} />
        </button>
      </div>
    </div>
  ) : <div>Loading...</div>;
};

function App() {
  const [currentScreen, setCurrentScreen] = useState('profile');
  const [currentProfile, setCurrentProfile] = useState(null);
  const [matches, setMatches] = useState([]);
  const [currentMatchAndConversation, setCurrentMatchAndConversation] = useState({ match: {}, conversation: [] });

  const loadRandomProfile = async () => {
    try {
      const profile = await fetchRandomProfile();
      setCurrentProfile(profile);
    } catch (error) {
      console.error(error);
    }
  };

  const loadMatches = async () => {
    try {
      const matches = await fetchMatches();
      setMatches(matches);
    } catch (error) {
      console.error(error);
    }
  };

  const onSwipe = async (profileId, direction) => {
    loadRandomProfile();
    if (direction === 'right') {
      await saveSwipe(profileId);
      await loadMatches();
    }
  };

  const onSelectMatch = async (profile, conversationId) => {
    const conversation = await fetchConversation(conversationId);
    setCurrentMatchAndConversation({ match: profile, conversation: conversation });
    setCurrentScreen('chat');
  };

  const refreshChatState = async () => {
    const conversation = await fetchConversation(currentMatchAndConversation.conversation.id);
    setCurrentMatchAndConversation({
      match: currentMatchAndConversation.match,
      conversation: conversation
    });
  };

  const renderScreen = () => {
    switch (currentScreen) {
      case 'profile':
        return <ProfileSelector profile={currentProfile} onSwipe={onSwipe} />;
      case 'matches':
        return <MatchesList matches={matches} onSelectMatch={onSelectMatch} />;
      case 'chat':
        return (
          <ChatScreen
            currentMatch={currentMatchAndConversation.match}
            conversation={currentMatchAndConversation.conversation}
            refreshState={refreshChatState}
          />
        );
      default:
        return null;
    }
  };

  useEffect(() => {
    loadRandomProfile();
    loadMatches();
  }, []);

  return (
    <div className="max-w-md mx-auto p-4 bg-gray-50 min-h-screen">
      <nav className="flex justify-between items-center bg-white border border-gray-200 shadow-md rounded-full px-6 py-3 mb-6 sticky top-4 z-50">
        <User className="cursor-pointer text-indigo-500 hover:text-indigo-700 transition-colors" onClick={() => setCurrentScreen('profile')} />
        <MessageCircle className="cursor-pointer text-indigo-500 hover:text-indigo-700 transition-colors" onClick={() => setCurrentScreen('matches')} />
      </nav>
      {renderScreen()}
    </div>
  );
}

export default App;
