package de.tum.in.net.analysis;

public class GeneralStatistic {

  private final int totalTestCount;
  private final int totalInterceptionCount;

  public GeneralStatistic(int totalTestCount, int totalInterceptionCount) {
    this.totalTestCount = totalTestCount;
    this.totalInterceptionCount = totalInterceptionCount;
  }

  public int getTotalTestCount() {
    return totalTestCount;
  }

  public int getTotalInterceptionCount() {
    return totalInterceptionCount;
  }

}
