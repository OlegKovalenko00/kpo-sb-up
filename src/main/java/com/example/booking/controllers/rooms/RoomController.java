package com.example.booking.controllers.rooms;

import com.example.booking.dto.request.RoomRequest;
import com.example.booking.dto.response.RoomResponse;
import com.example.booking.enums.ReportFormat;
import com.example.booking.enums.RoomType;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final CampusFacade campusFacade;

    @PostMapping
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(campusFacade.addRoom(request));
    }

    @PostMapping("/external")
    public ResponseEntity<RoomResponse> createFromExternal(@RequestBody com.example.booking.domains.ExternalRoom externalRoom) {
        return ResponseEntity.ok(campusFacade.bookExternalRoom(externalRoom));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> update(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(campusFacade.updateRoom(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        campusFacade.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> list() {
        return ResponseEntity.ok(campusFacade.listRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(campusFacade.getRoom(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<RoomResponse>> filterByCapacity(@RequestParam int capacity) {
        return ResponseEntity.ok(campusFacade.filterRoomsByCapacity(capacity));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<RoomResponse>> filterByType(@PathVariable RoomType type) {
        return ResponseEntity.ok(campusFacade.filterRoomsByType(type));
    }

    @GetMapping(value = "/export/{format}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> export(@PathVariable String format) throws Exception {
        StringWriter writer = new StringWriter();
        campusFacade.exportRooms(ReportFormat.valueOf(format.toUpperCase()), writer);
        return ResponseEntity.ok(writer.toString());
    }
}
