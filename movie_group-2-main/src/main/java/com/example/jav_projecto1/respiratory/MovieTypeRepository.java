package com.example.jav_projecto1.respiratory;

import com.example.jav_projecto1.entities.MovieType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieTypeRepository extends JpaRepository<MovieType, Long> {
    void deleteAllByMovie_MovieId(String movieId);
}