package com.vn.vodka_server.util;

// Calculate trend percent
public class TrendPercent {
    public static double calculateTrendPercent(long current, long previous) {
        return calculateTrendPercent((double) current, (double) previous);
    }

    public static double calculateTrendPercent(double current, double previous) {
        if (previous == 0.0)
            return current > 0.0 ? 100.0 : 0.0;
        return Math.round((current - previous) * 1000.0 / previous) / 10.0;
    }
}
