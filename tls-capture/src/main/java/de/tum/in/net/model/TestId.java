package de.tum.in.net.model;

public class TestId {

  private final String testId;

  public TestId(String testId) {
    this.testId = testId;
  }

  public String getID() {
    return this.testId;
  }

  @Override
  public String toString() {
    return testId;
  }

}
