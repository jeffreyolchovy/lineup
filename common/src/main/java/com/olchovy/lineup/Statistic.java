package com.olchovy.lineup;

public enum Statistic {
  AT_BATS("AB"),
  HITS("H"),
  SINGLES("1B"),
  DOUBLES("2B"),
  TRIPLES("3B"),
  HOMERUNS("HR"),
  RUNS_BATTED_IN("RBI"),
  RUNS_SCORED("R"),
  WALKS("BB"),
  STRIKEOUTS("SO"),
  SACRIFICE_FLYOUTS("SF"),
  BATTING_AVG("AVG"),
  SLUGGING_PCT("SLG"),
  ON_BASE_AVG("OBA"),
  EXTRA_BASE_AVG("EBA"),
  PUT_IN_PLAY_PCT("PIP"),
  HOMERUNS_PER_HIT("HR/H"),
  WALKS_PER_AT_BAT("BB/AB"),
  STRIKEOUTS_PER_AT_BAT("SO/AB"),
  SINGLES_PER_HIT("1B/H");

  private final String abbreviation;

  private Statistic(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public String getAbbreviation() {
    return abbreviation;
  }

  public static Statistic fromAbbreviation(String abbreviation) {
    for (Statistic e : Statistic.values()) {
      if (e.abbreviation.equalsIgnoreCase(abbreviation)) {
        return e;
      }
    }
    return null;
  }

  public String toString() {
    return this.abbreviation;
  }
}
