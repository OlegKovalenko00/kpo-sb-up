package com.example.booking.export.reports.impl;

import com.example.booking.domains.Report;
import com.example.booking.export.reports.ReportExporter;
import java.io.IOException;
import java.io.Writer;
import org.springframework.stereotype.Component;

@Component
public class MarkdownReportExporter implements ReportExporter {
    @Override
    public void export(Report report, Writer writer) throws IOException {
        writer.write("# " + report.getTitle() + System.lineSeparator());
        writer.write(System.lineSeparator());
        writer.write(report.getContent());
    }
}
