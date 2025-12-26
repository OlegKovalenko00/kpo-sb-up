package com.example.booking.factories;

import com.example.booking.enums.ReportFormat;
import com.example.booking.export.reports.ReportExporter;
import com.example.booking.export.reports.impl.JsonReportExporter;
import com.example.booking.export.reports.impl.MarkdownReportExporter;
import com.example.booking.exception.BookingException;
import org.springframework.stereotype.Component;

@Component
public class ReportExporterFactory {
    private final JsonReportExporter jsonReportExporter;
    private final MarkdownReportExporter markdownReportExporter;

    public ReportExporterFactory(JsonReportExporter jsonReportExporter, MarkdownReportExporter markdownReportExporter) {
        this.jsonReportExporter = jsonReportExporter;
        this.markdownReportExporter = markdownReportExporter;
    }

    public ReportExporter resolve(ReportFormat format) {
        if (format == ReportFormat.JSON) {
            return jsonReportExporter;
        }
        if (format == ReportFormat.MARKDOWN) {
            return markdownReportExporter;
        }
        throw new BookingException("Unsupported report format");
    }
}
