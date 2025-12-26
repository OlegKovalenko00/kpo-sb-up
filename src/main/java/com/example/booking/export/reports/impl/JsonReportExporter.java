package com.example.booking.export.reports.impl;

import com.example.booking.domains.Report;
import com.example.booking.export.reports.ReportExporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Writer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonReportExporter implements ReportExporter {
    private final ObjectMapper objectMapper;

    @Override
    public void export(Report report, Writer writer) throws IOException {
        writer.write(objectMapper.writeValueAsString(report));
    }
}
