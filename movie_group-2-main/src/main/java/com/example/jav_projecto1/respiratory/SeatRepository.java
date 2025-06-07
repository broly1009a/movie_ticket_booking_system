package com.example.jav_projecto1.respiratory;

import com.example.jav_projecto1.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}