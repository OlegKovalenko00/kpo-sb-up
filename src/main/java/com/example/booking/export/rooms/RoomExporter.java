package com.example.booking.export.rooms;

import com.example.booking.domains.Room;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public interface RoomExporter {
    void export(List<Room> rooms, Writer writer) throws IOException;
}
