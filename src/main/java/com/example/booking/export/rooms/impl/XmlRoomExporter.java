package com.example.booking.export.rooms.impl;

import com.example.booking.domains.Room;
import com.example.booking.export.rooms.RoomExporter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class XmlRoomExporter implements RoomExporter {
    @Override
    public void export(List<Room> rooms, Writer writer) throws IOException {
        writer.write("<rooms>");
        for (Room room : rooms) {
            writer.write("<room>");
            writer.write("<id>" + room.getId() + "</id>");
            writer.write("<name>" + room.getName() + "</name>");
            writer.write("<capacity>" + room.getCapacity() + "</capacity>");
            writer.write("<type>" + room.getType() + "</type>");
            writer.write("<available>" + room.isAvailable() + "</available>");
            writer.write("</room>");
        }
        writer.write("</rooms>");
    }
}
