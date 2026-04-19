/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.dto.request;

import lombok.Data;

@Data
public class CompleteTripRequest {
    private String endLocation;
    private Long endOdometer;
}
