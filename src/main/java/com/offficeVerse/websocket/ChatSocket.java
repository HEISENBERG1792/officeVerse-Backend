package com.offficeVerse.websocket;

import com.offficeVerse.model.ChatMessage;
import com.offficeVerse.model.Player;
import com.offficeVerse.model.Room;
import com.offficeVerse.service.ChatService;
import com.offficeVerse.service.PlayerService;
import com.offficeVerse.service.RoomService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSocket extends TextWebSocketHandler {

    private final ChatService chatService;
    private final RoomService roomService;
    private final PlayerService playerService;

    // Map: roomId -> set of sessions for broadcasting
    private final Map<Long, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    public ChatSocket(ChatService chatService, RoomService roomService, PlayerService playerService) {
        this.chatService = chatService;
        this.roomService = roomService;
        this.playerService = playerService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Chat WebSocket connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String[] parts = message.getPayload().split(":", 3);
        if (parts.length != 3) {
            session.sendMessage(new TextMessage("Invalid format. Use playerId:roomId:messageText"));
            return;
        }

        try {
            Long playerId = Long.parseLong(parts[0]);
            Long roomId = Long.parseLong(parts[1]);
            String text = parts[2];

            Player player = playerService.getPlayer(playerId);
            Room room = roomService.getRoom(roomId);

            if (player == null || room == null) {
                session.sendMessage(new TextMessage("Player or Room not found"));
                return;
            }

            // Save messages
            ChatMessage chatMessage = new ChatMessage(player.getName(), text, room);
            chatService.saveMessage(chatMessage);

            // Add session to room map
            roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);

            // Broadcasts to all players
            for (WebSocketSession s : roomSessions.get(roomId)) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(player.getName() + ": " + text));
                }
            }

        } catch (NumberFormatException e) {
            session.sendMessage(new TextMessage("Invalid numbers for playerId or roomId"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Chat WebSocket disconnected: " + session.getId());
        // close session for all
        for (Set<WebSocketSession> sessions : roomSessions.values()) {
            sessions.remove(session);
        }
    }
}
