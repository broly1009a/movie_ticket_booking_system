package com.example.jav_projecto1.controller;

import com.example.jav_projecto1.entities.Invoice;
import com.example.jav_projecto1.entities.Movie;
import com.example.jav_projecto1.entities.Schedule;
import com.example.jav_projecto1.respiratory.*;
import com.example.jav_projecto1.entities.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.jav_projecto1.dto.MovieDTO;
import com.example.jav_projecto1.dto.InvoiceDTO;
import com.example.jav_projecto1.entities.MovieSchedule;
import com.example.jav_projecto1.entities.MovieDate;
import com.example.jav_projecto1.entities.ShowDates;
import com.example.jav_projecto1.dto.ShowtimeDTO;
import com.example.jav_projecto1.entities.Seat;
import com.example.jav_projecto1.entities.CinemaRoom;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private final MovieRepository movieRepository;
    private final ScheduleRepository scheduleRepository;
    private final InvoiceRepository invoiceRepository;
    private final AccountRespiratory accountRespiratory;
    private final SeatRepository seatRepository;
    private final CinemaRoomRepository cinemaRoomRepository;

    public BookingController(
            MovieRepository movieRepository,
            ScheduleRepository scheduleRepository,
            InvoiceRepository invoiceRepository,
            AccountRespiratory accountRespiratory,
            SeatRepository seatRepository,
            CinemaRoomRepository cinemaRoomRepository
    ) {
        this.movieRepository = movieRepository;
        this.scheduleRepository = scheduleRepository;
        this.invoiceRepository = invoiceRepository;
        this.accountRespiratory = accountRespiratory;
        this.seatRepository = seatRepository;
        this.cinemaRoomRepository = cinemaRoomRepository;
    }

    // 3.1.4.1 Book ticket – Select movie and showtime
    @GetMapping("/movies")
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
                        .build()
        ).toList();
    }

    @GetMapping("/schedules")
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    // 3.1.4.2 Booking ticket – Selecting seat
    // (Giả sử bạn truyền movieId, scheduleId để lấy danh sách ghế đã đặt)
    @GetMapping("/seats")
    public List<String> getBookedSeats(@RequestParam String movieName, @RequestParam String scheduleShow) {
        return invoiceRepository.findBookedSeats(movieName, scheduleShow);
    }

    // 3.1.4.3 Booking ticket – Confirm booking ticket
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(@RequestBody Invoice invoice, @RequestParam Long accountId) {
        Optional<Account> accOpt = accountRespiratory.findById(accountId);
        if (accOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản không tồn tại"));
        }
        invoice.setAccount(accOpt.get());
        invoiceRepository.save(invoice);
        return ResponseEntity.ok(Map.of("message", "Đặt vé thành công"));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getBookingHistory(@RequestParam Long accountId) {
        Optional<Account> accOpt = accountRespiratory.findById(accountId);
        if (accOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản không tồn tại"));
        }
        List<InvoiceDTO> history = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getAccount() != null && inv.getAccount().getAccountId().equals(accountId))
                .map(inv -> InvoiceDTO.builder()
                        .invoiceId(inv.getInvoiceId())
                        .movieName(inv.getMovieName())
                        .scheduleShow(inv.getScheduleShow())
                        .scheduleShowTime(inv.getScheduleShowTime())
                        .seat(inv.getSeat())
                        .totalMoney(inv.getTotalMoney())
                        .bookingDate(inv.getBookingDate())
                        .status(inv.getStatus())
                        .build())
                .toList();
        return ResponseEntity.ok(history);
    }

    // 3.1.4.4 Booking ticket – Ticket information
    @GetMapping("/ticket/{invoiceId}")
public ResponseEntity<?> getTicketInfo(@PathVariable Long invoiceId) {
    Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
    if (invoiceOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    Invoice invoice = invoiceOpt.get();

    // Lấy tên phòng chiếu và version từ Movie
    String cinemaRoomName = null;
    String version = null;
    Optional<Movie> movieOpt = movieRepository.findAll().stream()
        .filter(m -> m.getMovieNameEnglish().equals(invoice.getMovieName()))
        .findFirst();
    if (movieOpt.isPresent()) {
        Movie movie = movieOpt.get();
        if (movie.getCinemaRoom() != null) {
            cinemaRoomName = movie.getCinemaRoom().getRoomName();
        }
        version = movie.getVersion();
    }

    Map<String, Object> dto = Map.of(
        "invoiceId", invoice.getInvoiceId(),
        "movieName", invoice.getMovieName(),
        "scheduleShow", invoice.getScheduleShow(),
        "scheduleShowTime", invoice.getScheduleShowTime(),
        "seat", invoice.getSeat(),
        "totalMoney", invoice.getTotalMoney(),
        "bookingDate", invoice.getBookingDate(),
        "status", invoice.getStatus(),
        "cinemaRoomName", cinemaRoomName,
        "version", version
    );
    return ResponseEntity.ok(dto);
}

    @GetMapping("/schedules/{movieId}")
    public ResponseEntity<List<Schedule>> getSchedulesByMovieId(@PathVariable String movieId) {
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        if (movieOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Movie movie = movieOpt.get();
        if (movie.getMovieSchedules() == null) {
            return ResponseEntity.ok(List.of());
        }
        List<Schedule> schedules = movie.getMovieSchedules().stream()
                .map(MovieSchedule::getSchedule)
                .toList();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/showtimes/{movieId}")
    public ResponseEntity<List<Map<String, Object>>> getShowtimesByMovieId(@PathVariable String movieId) {
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        if (movieOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Movie movie = movieOpt.get();

        List<Map<String, Object>> showtimes = new java.util.ArrayList<>();

        // Lấy tất cả ngày chiếu của phim
        List<MovieDate> movieDates = movie.getMovieDates();
        if (movieDates == null) movieDates = List.of();

        // Lấy tất cả giờ chiếu của phim
        List<MovieSchedule> movieSchedules = movie.getMovieSchedules();
        if (movieSchedules == null) movieSchedules = List.of();

        // Lấy phòng chiếu và danh sách ghế
        CinemaRoom room = movie.getCinemaRoom();
        List<Seat> allSeats = room == null ? List.of() :
                seatRepository.findAll().stream()
                        .filter(seat -> seat.getCinemaRoom().getCinemaRoomId().equals(room.getCinemaRoomId()))
                        .toList();

        // Kết hợp từng ngày chiếu với từng giờ chiếu
        for (MovieDate md : movieDates) {
            ShowDates showDate = md.getShowDates();
            for (MovieSchedule ms : movieSchedules) {
                Schedule schedule = ms.getSchedule();

                // Đếm số ghế còn trống cho từng showtime
                String movieName = movie.getMovieNameEnglish();
                String scheduleShow = schedule.getScheduleTime();
                String scheduleShowTime = schedule.getScheduleTime(); // Nếu invoice lưu ngày thì đổi thành showDate.getShowDate().toString()

                List<String> bookedSeats = invoiceRepository.findAll().stream()
                        .filter(inv -> inv.getMovieName().equals(movieName)
                                && inv.getScheduleShow().equals(scheduleShow)
                                && inv.getScheduleShowTime().equals(scheduleShowTime))
                        .map(Invoice::getSeat)
                        .toList();

                long availableSeats = allSeats.stream()
                        .filter(seat -> !bookedSeats.contains(seat.getSeatCode()))
                        .count();

                Map<String, Object> map = new java.util.HashMap<>();
                map.put("scheduleId", schedule.getScheduleId());
                map.put("scheduleTime", schedule.getScheduleTime());
                map.put("showDateId", showDate.getShowDateId());
                map.put("showDate", showDate.getShowDate());
                map.put("dateName", showDate.getDateName());
                map.put("availableSeats", availableSeats);
                showtimes.add(map);
            }
        }
        return ResponseEntity.ok(showtimes);
    }

    @GetMapping("/seat-status")
    public ResponseEntity<?> getSeatStatus(
            @RequestParam String movieId,
            @RequestParam Integer showDateId,
            @RequestParam Integer scheduleId
    ) {
        // Lấy thông tin phim
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        if (movieOpt.isEmpty()) return ResponseEntity.notFound().build();
        Movie movie = movieOpt.get();

        // Lấy phòng chiếu
        CinemaRoom room = movie.getCinemaRoom();
        if (room == null) return ResponseEntity.badRequest().body("Phim chưa gán phòng chiếu");

        // Lấy tất cả ghế của phòng chiếu
        List<Seat> allSeats = seatRepository.findAll().stream()
                .filter(seat -> seat.getCinemaRoom().getCinemaRoomId().equals(room.getCinemaRoomId()))
                .toList();

        // Lấy giờ chiếu từ scheduleId
        Optional<Schedule> scheduleOpt = scheduleRepository.findById(scheduleId);
        if (scheduleOpt.isEmpty()) return ResponseEntity.badRequest().body("Giờ chiếu không tồn tại");
        String scheduleShow = scheduleOpt.get().getScheduleTime();

        // Lấy ngày chiếu từ showDateId (nếu invoice lưu ngày chiếu, bạn cần lấy đúng định dạng)
        // Nếu invoice chỉ lưu giờ chiếu thì có thể bỏ qua phần này
        // Optional<ShowDates> showDateOpt = showDatesRepository.findById(showDateId);
        // String showDateStr = showDateOpt.map(sd -> sd.getShowDate().toString()).orElse("");

        // Lấy tên phim đúng với invoice
        String movieName = movie.getMovieNameEnglish();

        // Lấy danh sách ghế đã đặt (theo movieName, scheduleShow)
        List<String> bookedSeats = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getMovieName().equals(movieName)
                        && inv.getScheduleShow().equals(scheduleShow)
                        && inv.getScheduleShowTime().equals(scheduleShow)) // Nếu invoice lưu ngày thì thay bằng showDateStr
                .map(Invoice::getSeat)
                .toList();

        // Trả về danh sách ghế và trạng thái
        List<Map<String, Object>> result = allSeats.stream().map(seat -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("seatId", seat.getSeatId());
            map.put("seatCode", seat.getSeatCode());
            map.put("rowLabel", seat.getRowLabel());
            map.put("seatNumber", seat.getSeatNumber());
            map.put("seatType", seat.getSeatType());
            map.put("status", bookedSeats.contains(seat.getSeatCode()) ? "BOOKED" : "AVAILABLE");
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

}