package br.com.davidds5.manicure_api.dto;

public record PlatformMetricsDTO(
    long totalTenants,
    long activeTenants,
    long trialTenants,
    long suspendedTenants,
    double estimatedMonthlyRecurringRevenue
) {}
