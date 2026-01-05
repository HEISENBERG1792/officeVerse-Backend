# 🏢 OfficeVerse Backend

<div align="center">

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Java](https://img.shields.io/badge/Java-21+-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

**A multiplayer office-themed game backend built with Spring Boot**

[Features](#-features) • [Getting Started](#-getting-started) • [API Documentation](#-api-documentation) • [WebSocket](#-websocket-endpoints) • [Integration](#-frontend-integration)

</div>

---

## 📋 Overview

OfficeVerse is a **real-time multiplayer game backend** that powers immersive office environments. Built with **Spring Boot**, it features **WebSocket-based real-time communication**, **H2 database persistence**, and **intelligent room-based API integrations** for Discord and OpenAI.

### ✨ Key Highlights

- 🎮 **Real-time gameplay** with WebSocket support for movement and chat
- 💬 **Persistent chat system** with full message history
- 🤖 **AI-powered NPCs** using OpenAI integration
- 🔗 **Discord API** integration for meeting rooms
- 📊 **H2 database** for lightweight data persistence
- 🔐 **Player authentication** and session management
- 📝 **Comprehensive logging** system for game events

---

## 🎯 Features

| Feature | Description |
|---------|-------------|
| **Player Management** | Registration, authentication, and profile handling |
| **Room System** | Dynamic room creation and join functionality |
| **Real-time Chat** | WebSocket-powered instant messaging |
| **Movement Sync** | Live player position synchronization |
| **AI Service** | Intelligent NPC behavior and responses |
| **API Integrations** | Context-aware Discord and OpenAI connections |
| **Event Logging** | Full audit trail of game activities |

---

## 📁 Project Structure

```
com.officeverse
├── 🚀 OfficeVerseApplication.java
├── ⚙️  config
│   ├── WebSocketConfig.java
│   └── AsyncConfig.java
├── 🎮 controller
│   ├── AIController.java
│   ├── AuthController.java
│   └── GameController.java
├── 🔌 websocket
│   ├── MovementSocket.java
│   └── ChatSocket.java
├── 🛠️  service
│   ├── PlayerService.java
│   ├── RoomService.java
│   ├── PositionService.java
│   ├── ChatService.java
│   ├── AIService.java
│   └── LoggingService.java
├── 💾 repository
│   ├── PlayerRepository.java
│   ├── RoomRepository.java
│   ├── PositionRepository.java
│   └── ChatMessageRepository.java
├── 📦 model
│   ├── Player.java
│   ├── Room.java
│   ├── Position.java
│   └── ChatMessage.java
└── 🔧 util
    ├── FileLogger.java
    └── SessionManager.java
```

---

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- ☕ **Java 21+**
- 🐘 **Gradle 8+**
- 🍃 **Spring Boot 4.0.1**
- 💻 **IDE** (IntelliJ IDEA or VS Code recommended)

### Installation

1️⃣ **Clone the repository**

```bash
git clone https://github.com/<username>/officeverse-backend.git
cd officeverse-backend
```

2️⃣ **Build the project**

```bash
./gradlew build
```

3️⃣ **Run the application**

```bash
./gradlew bootRun
```

4️⃣ **Access the server**

The server will be running at `http://localhost:8080`

---

## 💾 Database

OfficeVerse uses **H2 in-memory database** for lightweight data persistence.

### H2 Console Access

- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:officeverse`
- **Username:** `sa`
- **Password:** _(leave empty)_

### Database Schema

The following tables are auto-generated from JPA entities:

| Table | Purpose |
|-------|---------|
| `Player` | User accounts and profiles |
| `Room` | Game rooms and their configurations |
| `Position` | Player coordinates and locations |
| `ChatMessage` | Persistent chat history |

---

## 📡 API Documentation

### 🔐 Authentication Endpoints

#### Register Player
```http
POST /auth/register?name=<name>&roomId=<id>
```
Creates a new player account and assigns them to a room.

#### Get Player Info
```http
GET /auth/player/{id}
```
Retrieves player profile and current status.

---

### 🎮 Game Endpoints

#### Update Player Position
```http
POST /game/update-position
Content-Type: application/json

{
  "playerId": "123",
  "x": 100,
  "y": 200
}
```

#### Get Player Position
```http
GET /game/player-position/{playerId}
```

---

### 🤖 AI Endpoints

#### Get Next AI Move
```http
GET /ai/next-move?npcId=<id>
```
Returns intelligent movement suggestions for NPCs.

---

### 💬 Chat Endpoints

#### Get Chat History
```http
GET /chat/room/{roomId}/history
```
Retrieves all chat messages for a specific room.

---

## 🔌 WebSocket Endpoints

### Real-time Communication

| Endpoint | Purpose | Message Format |
|----------|---------|----------------|
| `/ws/movement` | Player movement sync | `playerId:x:y` |
| `/ws/chat` | Instant messaging | `playerId:roomId:message` |

### Example WebSocket Connection

```javascript
// Movement WebSocket
const movementSocket = new WebSocket('ws://localhost:8080/ws/movement');

movementSocket.onmessage = (event) => {
  const [playerId, x, y] = event.data.split(':');
  updatePlayerPosition(playerId, x, y);
};

// Chat WebSocket
const chatSocket = new WebSocket('ws://localhost:8080/ws/chat');

chatSocket.onmessage = (event) => {
  const [playerId, roomId, message] = event.data.split(':');
  displayChatMessage(playerId, message);
};
```

---

## 🎨 Frontend Integration

OfficeVerse backend is designed to work seamlessly with game engines like **Phaser.js**, **Unity**, or **Three.js**.

### Integration Flow

1. **Authentication**: Use REST API for player login/registration
2. **Room Management**: Fetch available rooms via REST endpoints
3. **Real-time Updates**: Connect to WebSocket endpoints for movement and chat
4. **Room-Specific Features**: Handle special room types dynamically

### Room Types

The `Room` entity includes a `roomType` field that triggers specific integrations:

| Room Type | Integration | Use Case |
|-----------|-------------|----------|
| `MEETING` | Discord API | Voice channels, meeting coordination |
| `AI_LAB` | OpenAI API | AI assistants, NPC conversations |
| `STANDARD` | None | Regular gameplay area |

### Example Frontend Code

```javascript
// Initialize game connection
async function joinGame(playerName, roomId) {
  // Register player
  const response = await fetch(
    `http://localhost:8080/auth/register?name=${playerName}&roomId=${roomId}`,
    { method: 'POST' }
  );
  const player = await response.json();
  
  // Connect WebSockets
  connectMovementSocket(player.id);
  connectChatSocket(player.id, roomId);
  
  return player;
}

// Send movement update
function movePlayer(playerId, x, y) {
  movementSocket.send(`${playerId}:${x}:${y}`);
}

// Send chat message
function sendMessage(playerId, roomId, message) {
  chatSocket.send(`${playerId}:${roomId}:${message}`);
}
```

---

## 🔗 Room-Based API Integration

The backend intelligently routes API calls based on room context:

### Discord Integration (Meeting Rooms)
When players enter a `MEETING` room, the backend:
- Creates temporary Discord channels
- Manages voice connections
- Syncs presence status

### OpenAI Integration (AI Labs)
When players enter an `AI_LAB` room, the backend:
- Initializes AI conversation context
- Provides intelligent NPC responses
- Offers code assistance and tutorials

Frontend clients can detect room types and adjust UI accordingly:
- Show voice controls in meeting rooms
- Display AI chat interface in AI labs
- Add room-specific overlays and features

---

## 🛠️ Configuration

### Application Properties

```properties
# Server Configuration
server.port=8080

# H2 Database
spring.datasource.url=jdbc:h2:mem:officeverse
spring.h2.console.enabled=true

# WebSocket
spring.websocket.allowed-origins=*

# Logging
logging.level.com.officeverse=DEBUG
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## 📞 Support

If you encounter any issues or have questions:

- 🐛 [Report a bug](https://github.com/<username>/officeverse-backend/issues)
- 💡 [Request a feature](https://github.com/<username>/officeverse-backend/issues)
- 📧 Contact: your-email@example.com

---

<div align="center">

**Built with ❤️ using Spring Boot**

⭐ Star this repo if you find it useful!

</div>
