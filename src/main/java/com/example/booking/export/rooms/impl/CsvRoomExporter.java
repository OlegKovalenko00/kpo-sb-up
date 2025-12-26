package com.example.booking.export.rooms.impl;

import com.example.booking.domains.Room;
import com.example.booking.export.rooms.RoomExporter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CsvRoomExporter implements RoomExporter {
    @Override
    public void export(List<Room> rooms, Writer writer) throws IOException {
        writer.write("id,name,capacity,type,available" + System.lineSeparator());
        for (Room room : rooms) {
            writer.write(room.getId() + "," + room.getName() + "," + room.getCapacity() + "," + room.getType() + "," + room.isAvailable() + System.lineSeparator());
        }
    }
}
