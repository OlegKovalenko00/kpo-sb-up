package com.example.booking.export.reports;

import com.example.booking.domains.Report;
import java.io.IOException;
import java.io.Writer;

public interface ReportExporter {
    void export(Report report, Writer writer) throws IOException;
}
