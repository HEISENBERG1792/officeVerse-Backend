package com.offficeVerse.service;

import com.offficeVerse.model.Room;
import com.offficeVerse.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createRoom(String name) {
        Room room = new Room(name);
        return roomRepository.save(room);
    }

    public Room getRoom(Long id) {
        Optional<Room> room = roomRepository.findById(id);
        return room.orElse(null);
    }

    public Room getRoomByName(String name) {
        return roomRepository.findByName(name);
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    public String getRoomType(Long roomId) {
        Room room = getRoom(roomId);
        if (room != null) {
            return room.getRoomType();
        }
        return "NORMAL";
    }
}
