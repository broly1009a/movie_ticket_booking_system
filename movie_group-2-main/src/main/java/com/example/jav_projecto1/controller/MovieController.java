package com.example.jav_projecto1.controller;

import com.example.jav_projecto1.entities.Movie;
import com.example.jav_projecto1.respiratory.MovieRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.*;

import com.example.jav_projecto1.dto.MovieDTO;
import com.example.jav_projecto1.entities.CinemaRoom;
import com.example.jav_projecto1.respiratory.CinemaRoomRepository;
import com.example.jav_projecto1.entities.Type;
import com.example.jav_projecto1.respiratory.TypeRepository;
import com.example.jav_projecto1.entities.MovieType;
import com.example.jav_projecto1.respiratory.MovieTypeRepository;
import com.example.jav_projecto1.dto.MovieUpdateRequest;
import org.springframework.transaction.annotation.Transactional;
import com.example.jav_projecto1.entities.MovieTypeId;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieRepository movieRepository;
    private final CinemaRoomRepository cinemaRoomRepository;
    private final TypeRepository typeRepository;
    private final MovieTypeRepository movieTypeRepository;

    public MovieController(MovieRepository movieRepository, CinemaRoomRepository cinemaRoomRepository, TypeRepository typeRepository, MovieTypeRepository movieTypeRepository) {
        this.typeRepository = typeRepository;
        this.movieTypeRepository = movieTypeRepository;
        this.movieRepository = movieRepository;
        this.cinemaRoomRepository = cinemaRoomRepository;
    }

    // 3.1.9.1 View movie list
    @GetMapping
    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll().stream().map(movie ->
                MovieDTO.builder()
                        .movieId(movie.getMovieId())
                        .movieNameEnglish(movie.getMovieNameEnglish())
                        .movieNameVn(movie.getMovieNameVn())
                        .director(movie.getDirector())
                        .actor(movie.getActor())
                        .duration(movie.getDuration())
                        .fromDate(movie.getFromDate())
                        .toDate(movie.getToDate())
                        .content(movie.getContent())
                        .largeImage(movie.getLargeImage())
                        .smallImage(movie.getSmallImage())
                        .types(
                                movie.getMovieTypes() == null ? List.of() :
                                        movie.getMovieTypes().stream()
                                                .map(mt -> mt.getType().getTypeName())
                                                .toList()
                        )
                        .cinemaRoomId(
                                movie.getCinemaRoom() != null ? movie.getCinemaRoom().getCinemaRoomId() : null
                        )
                        .cinemaRoomName(
                                movie.getCinemaRoom() != null ? movie.getCinemaRoom().getRoomName() : null
                        )
                        .version(movie.getVersion())
                        .movieProductionCompany(movie.getMovieProductionCompany())
                        .build()
        ).toList();
    }

    // 3.1.9.2 Add movie
    @PostMapping
    public Movie addMovie(@RequestBody Movie movie) {
        return movieRepository.save(movie);
    }

    // 3.1.9.3 Edit movie information
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable String id, @RequestBody MovieUpdateRequest movieDetails) {
        Optional<Movie> movieOpt = movieRepository.findById(id);
        if (movieOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Movie movie = movieOpt.get();
        movie.setActor(movieDetails.getActor());

        // Set CinemaRoom nếu có cinemaRoomId từ request
        if (movieDetails.getCinemaRoomId() != null) {
            Optional<CinemaRoom> cinemaRoomOpt = cinemaRoomRepository.findById(movieDetails.getCinemaRoomId());
            cinemaRoomOpt.ifPresent(movie::setCinemaRoom);
        }

        movie.setContent(movieDetails.getContent());
        movie.setDirector(movieDetails.getDirector());
        movie.setDuration(movieDetails.getDuration());
        movie.setFromDate(movieDetails.getFromDate());
        movie.setMovieProductionCompany(movieDetails.getMovieProductionCompany());
        movie.setToDate(movieDetails.getToDate());
        movie.setVersion(movieDetails.getVersion());
        movie.setMovieNameEnglish(movieDetails.getMovieNameEnglish());
        movie.setMovieNameVn(movieDetails.getMovieNameVn());
        movie.setLargeImage(movieDetails.getLargeImage());
        movie.setSmallImage(movieDetails.getSmallImage());

        // Cập nhật MovieType
        movieTypeRepository.deleteAllByMovie_MovieId(movie.getMovieId());
        if (movieDetails.getTypes() != null) {
            for (Integer typeId : movieDetails.getTypes()) {
                Optional<Type> typeOpt = typeRepository.findById(typeId);
                if (typeOpt.isPresent()) {
                    MovieType mt = new MovieType();
                    mt.setId(new MovieTypeId(movie.getMovieId(), typeId));
                    mt.setMovie(movie);
                    mt.setType(typeOpt.get());
                    movieTypeRepository.save(mt);
                }
            }
        }
        Movie saved = movieRepository.save(movie);

        // Trả về MovieDTO
        MovieDTO dto = MovieDTO.builder()
                .movieId(saved.getMovieId())
                .movieNameEnglish(saved.getMovieNameEnglish())
                .movieNameVn(saved.getMovieNameVn())
                .director(saved.getDirector())
                .actor(saved.getActor())
                .duration(saved.getDuration())
                .fromDate(saved.getFromDate())
                .toDate(saved.getToDate())
                .content(saved.getContent())
                .largeImage(saved.getLargeImage())
                .smallImage(saved.getSmallImage())
                .types(
                        movie.getMovieTypes() == null ? List.of() :
                                movie.getMovieTypes().stream()
                                        .map(mt -> mt.getType().getTypeName())
                                        .toList()
                )
                .cinemaRoomId(
                        saved.getCinemaRoom() != null ? saved.getCinemaRoom().getCinemaRoomId() : null
                )
                .cinemaRoomName(
                        saved.getCinemaRoom() != null ? saved.getCinemaRoom().getRoomName() : null
                )
                .version(saved.getVersion())
                .movieProductionCompany(saved.getMovieProductionCompany())
                .build();

        return ResponseEntity.ok(dto);
    }

    // 3.1.9.4 Delete movie
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String id) {
        if (!movieRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        movieRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMoviesByType(@RequestParam(required = false) String type) {
        List<Movie> movies = movieRepository.findAll();

        List<Movie> filtered;
        if (type == null || type.isBlank()) {
            filtered = movies;
        } else {
            filtered = movies.stream()
                    .filter(m -> m.getMovieTypes() != null && m.getMovieTypes().stream()
                            .anyMatch(mt -> mt.getType().getTypeName().equalsIgnoreCase(type)))
                    .toList();
        }

        List<Map<String, Object>> searchResults = filtered.stream().map(m -> {
            List<String> types = m.getMovieTypes() == null ? List.of() :
                    m.getMovieTypes().stream()
                            .map(mt -> mt.getType().getTypeName())
                            .toList();
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("Title", m.getMovieNameEnglish());
            map.put("Year", m.getFromDate() != null ? String.valueOf(
                    m.getFromDate().toInstant().atZone(ZoneId.systemDefault()).getYear()
            ) : "");
            map.put("imdbID", m.getMovieId());
            map.put("Type", types);
            map.put("Poster", m.getLargeImage());
            return map;
        }).toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("Search", searchResults);
        response.put("totalResults", String.valueOf(searchResults.size()));
        response.put("Response", "True");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieDetail(@PathVariable String id) {
        Optional<Movie> movieOpt = movieRepository.findById(id);
        if (movieOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Movie movie = movieOpt.get();
        MovieDTO dto = MovieDTO.builder()
                .movieId(movie.getMovieId())
                .movieNameEnglish(movie.getMovieNameEnglish())
                .movieNameVn(movie.getMovieNameVn())
                .director(movie.getDirector())
                .actor(movie.getActor())
                .duration(movie.getDuration())
                .fromDate(movie.getFromDate())
                .toDate(movie.getToDate())
                .content(movie.getContent())
                .largeImage(movie.getLargeImage())
                .smallImage(movie.getSmallImage())
                .types(
                        movie.getMovieTypes() == null ? List.of() :
                                movie.getMovieTypes().stream()
                                        .map(mt -> mt.getType().getTypeName())
                                        .toList()
                )
                .cinemaRoomId(
                        movie.getCinemaRoom() != null ? movie.getCinemaRoom().getCinemaRoomId() : null
                )
                .cinemaRoomName(
                        movie.getCinemaRoom() != null ? movie.getCinemaRoom().getRoomName() : null
                )
                .version(movie.getVersion())
                .movieProductionCompany(movie.getMovieProductionCompany())
                .build();
        return ResponseEntity.ok(dto);
    }
}