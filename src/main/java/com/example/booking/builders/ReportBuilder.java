package com.example.booking.builders;

import com.example.booking.domains.Report;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ReportBuilder {
    private final List<String> lines = new ArrayList<>();
    private String title = "Booking operations report";

    public synchronized void addOperation(String value) {
        lines.add(value);
    }

    public synchronized Report build() {
        return Report.builder()
                .title(title)
                .content(String.join(System.lineSeparator(), lines))
                .build();
    }

    public synchronized void reset() {
        lines.clear();
    }

    public synchronized void title(String value) {
        this.title = value;
    }
}
