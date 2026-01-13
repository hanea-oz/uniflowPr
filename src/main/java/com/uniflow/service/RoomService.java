package com.uniflow.service;

import com.uniflow.model.Room;
import com.uniflow.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;

    public Room createRoom(String name, Integer capacity, String type) {
        if (roomRepository.existsByName(name)) {
            throw new IllegalArgumentException("Room already exists: " + name);
        }

        Room room = Room.builder()
                .name(name)
                .capacity(capacity)
                .type(type)
                .build();

        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, String name, Integer capacity) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + id));

        if (!room.getName().equals(name) && roomRepository.existsByName(name)) {
            throw new IllegalArgumentException("Room already exists: " + name);
        }

        room.setName(name);
        room.setCapacity(capacity);

        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return roomRepository.findAll();
    }
}
