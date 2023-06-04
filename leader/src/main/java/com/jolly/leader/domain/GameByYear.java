package com.jolly.leader.domain;

/**
 * @author jolly
 */
public record GameByYear(int rank,
                  String name,
                  String platform,
                  int year,
                  String genre,
                  String publisher,
                  float na,
                  float eu,
                  float jp,
                  float other,
                  float global) {
}
