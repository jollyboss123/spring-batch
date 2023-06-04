package com.jolly.leader.domain;

import java.util.Collection;

/**
 * @author jolly
 */
public record YearReport(int year,
                  Collection<YearPlatformSales> breakout) {
}
