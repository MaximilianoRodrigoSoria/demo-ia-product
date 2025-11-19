package com.ar.laboratory.demoiaproduct.dto;

import java.time.LocalDateTime;

/**
 * DTO interno para información de health usando Java 17 records.
 */
public record HealthInfo(
        String applicationStatus,
        String statusMessage,
        String applicationVersion,
        LocalDateTime checkTime
) {
}
