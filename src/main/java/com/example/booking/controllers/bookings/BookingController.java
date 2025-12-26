package com.example.booking.controllers.bookings;

import com.example.booking.dto.request.BookingRequest;
import com.example.booking.dto.response.BookingResponse;
import com.example.booking.enums.ReportFormat;
import com.example.booking.facade.CampusFacade;
import jakarta.validation.Valid;
import java.io.StringWriter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final CampusFacade campusFacade;

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(campusFacade.createBooking(request));
    }

    @PostMapping("/book")
    public ResponseEntity<BookingResponse> book(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(campusFacade.createBooking(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> update(@PathVariable Long id, @Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(campusFacade.updateBooking(id, request));
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<BookingResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(campusFacade.cancelBooking(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        campusFacade.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> list() {
        return ResponseEntity.ok(campusFacade.listBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(campusFacade.getBooking(id));
    }

    @GetMapping(value = "/report/{format}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> exportReport(@PathVariable String format) throws Exception {
        StringWriter writer = new StringWriter();
        campusFacade.exportReport(ReportFormat.valueOf(format.toUpperCase()), writer);
        return ResponseEntity.ok(writer.toString());
    }
}
