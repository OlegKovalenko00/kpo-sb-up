package com.example.booking.factories;

import com.example.booking.enums.ReportFormat;
import com.example.booking.export.rooms.RoomExporter;
import com.example.booking.export.rooms.impl.CsvRoomExporter;
import com.example.booking.export.rooms.impl.XmlRoomExporter;
import com.example.booking.exception.BookingException;
import org.springframework.stereotype.Component;

@Component
public class RoomExporterFactory {
    private final CsvRoomExporter csvRoomExporter;
    private final XmlRoomExporter xmlRoomExporter;

    public RoomExporterFactory(CsvRoomExporter csvRoomExporter, XmlRoomExporter xmlRoomExporter) {
        this.csvRoomExporter = csvRoomExporter;
        this.xmlRoomExporter = xmlRoomExporter;
    }

    public RoomExporter resolve(ReportFormat format) {
        if (format == ReportFormat.CSV) {
            return csvRoomExporter;
        }
        if (format == ReportFormat.XML) {
            return xmlRoomExporter;
        }
        throw new BookingException("Unsupported room export format");
    }
}
