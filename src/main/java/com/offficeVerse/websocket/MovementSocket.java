package com.offficeVerse.websocket;

import com.offficeVerse.model.Player;
import com.offficeVerse.model.Position;
import com.offficeVerse.service.PlayerService;
import com.offficeVerse.service.PositionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Component
public class MovementSocket extends TextWebSocketHandler {

    private final PlayerService playerService;
    private final PositionService positionService;

    // Track sessions for broadcasting (optional)
    private final Map<String, Player> sessions = new HashMap<>();

    public MovementSocket(PlayerService playerService, PositionService positionService) {
        this.playerService = playerService;
        this.positionService = positionService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Optionally associate a session with a Player (login required)
        System.out.println("WebSocket connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String[] parts = message.getPayload().split(":");
        if (parts.length != 3) {
            session.sendMessage(new TextMessage("Invalid format. Use playerId:x:y"));
            return;
        }

        try {
            Long playerId = Long.parseLong(parts[0]);
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);

            Player player = playerService.getPlayer(playerId);
            if (player == null) {
                session.sendMessage(new TextMessage("Player not found"));
                return;
            }

            // Save / update player position
            Position position = new Position(x, y, player);
            positionService.savePosition(position);

            // broadcast to other players in the same room
            session.sendMessage(new TextMessage("Position updated: x=" + x + ", y=" + y));

        } catch (NumberFormatException e) {
            session.sendMessage(new TextMessage("Invalid numbers"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("WebSocket disconnected: " + session.getId());
        sessions.remove(session.getId());
    }
}
