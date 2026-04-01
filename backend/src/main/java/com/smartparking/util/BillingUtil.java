package com.smartparking.util;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class BillingUtil {

    private static final BigDecimal HOURLY_RATE = new BigDecimal("50.00");

    public BigDecimal estimateCost(int minutes) {
        BigDecimal hours = new BigDecimal(minutes).divide(new BigDecimal("60"), 2, RoundingMode.HALF_UP);
        return hours.multiply(HOURLY_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal computeFinalAmount(int minutes) {
        return estimateCost(minutes);
    }
}
