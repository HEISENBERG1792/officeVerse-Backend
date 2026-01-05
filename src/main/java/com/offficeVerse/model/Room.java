package com.offficeVerse.model;

import jakarta.persistence.*;
import java.util.List;
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String roomType; // eg: meeting, desk

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Player> players;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<ChatMessage> messages;

    protected Room() {}

    public Room(String name) {
        this.name = name;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public List<Player> getPlayers() { return players; }
    public List<ChatMessage> getMessages() { return messages; }

    public void setPlayers(List<Player> players) { this.players = players; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
}
