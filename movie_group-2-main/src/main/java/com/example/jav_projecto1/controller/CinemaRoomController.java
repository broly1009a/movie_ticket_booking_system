package com.example.jav_projecto1.controller;

import com.example.jav_projecto1.entities.CinemaRoom;
import com.example.jav_projecto1.respiratory.CinemaRoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cinema-rooms")
public class CinemaRoomController {

    private final CinemaRoomRepository cinemaRoomRepository;

    public CinemaRoomController(CinemaRoomRepository cinemaRoomRepository) {
        this.cinemaRoomRepository = cinemaRoomRepository;
    }

    // Get all cinema rooms
    @GetMapping
    public List<CinemaRoom> getAllCinemaRooms() {
        return cinemaRoomRepository.findAll();
    }

    // Get cinema room by id
    @GetMapping("/{id}")
    public ResponseEntity<CinemaRoom> getCinemaRoomById(@PathVariable Integer id) {
        Optional<CinemaRoom> roomOpt = cinemaRoomRepository.findById(id);
        return roomOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create new cinema room
    @PostMapping
    public CinemaRoom createCinemaRoom(@RequestBody CinemaRoom cinemaRoom) {
        return cinemaRoomRepository.save(cinemaRoom);
    }

    // Update cinema room
    @PutMapping("/{id}")
    public ResponseEntity<CinemaRoom> updateCinemaRoom(@PathVariable Integer id, @RequestBody CinemaRoom roomDetails) {
        Optional<CinemaRoom> roomOpt = cinemaRoomRepository.findById(id);
        if (roomOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        CinemaRoom room = roomOpt.get();
        room.setRoomName(roomDetails.getRoomName());
        room.setCapacity(roomDetails.getCapacity());
        room.setDescription(roomDetails.getDescription());
        return ResponseEntity.ok(cinemaRoomRepository.save(room));
    }

    // Delete cinema room
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCinemaRoom(@PathVariable Integer id) {
        if (!cinemaRoomRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        cinemaRoomRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}