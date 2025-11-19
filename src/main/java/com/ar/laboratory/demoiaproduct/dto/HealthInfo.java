package com.ar.laboratory.demoiaproduct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO interno para información de health
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthInfo {
    private String applicationStatus;
    private String statusMessage;
    private String applicationVersion;
    private LocalDateTime checkTime;
}
