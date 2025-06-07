package com.example.jav_projecto1.controller;

import com.example.jav_projecto1.entities.Seat;
import com.example.jav_projecto1.respiratory.SeatRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatRepository seatRepository;

    public SeatController(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @GetMapping
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seat> getSeatById(@PathVariable Long id) {
        Optional<Seat> seatOpt = seatRepository.findById(id);
        return seatOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Seat createSeat(@RequestBody Seat seat) {
        return seatRepository.save(seat);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Seat> updateSeat(@PathVariable Long id, @RequestBody Seat seatDetails) {
        Optional<Seat> seatOpt = seatRepository.findById(id);
        if (seatOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Seat seat = seatOpt.get();
        seat.setRowLabel(seatDetails.getRowLabel());
        seat.setSeatNumber(seatDetails.getSeatNumber());
        seat.setSeatCode(seatDetails.getSeatCode());
        seat.setSeatType(seatDetails.getSeatType());
        seat.setStatus(seatDetails.getStatus());
        seat.setCinemaRoom(seatDetails.getCinemaRoom());
        return ResponseEntity.ok(seatRepository.save(seat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeat(@PathVariable Long id) {
        if (!seatRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        seatRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}