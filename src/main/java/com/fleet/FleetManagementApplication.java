package com.fleet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class FleetManagementApplication {
    public static void main(String[] args) {
        // PostgreSQL rejects deprecated timezone aliases such as Asia/Calcutta.
        // Force a canonical timezone so JDBC startup metadata negotiation succeeds.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(FleetManagementApplication.class, args);
    }
}