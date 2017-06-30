package de.tum.in.net.model;

import java.util.Arrays;

public class Diff {

  private final String name;
  protected final String expected;
  protected final String actual;

  public Diff(String name, String expected, String actual) {
    this.name = name;
    this.expected = expected;
    this.actual = actual;
  }

  public Diff(String name, Integer expected, Integer actual) {
    this.name = name;
    this.expected = expected == null ? null : expected.toString();
    this.actual = actual == null ? null : actual.toString();
  }

  public Diff(String name, int[] expected, int[] actual) {
    this.name = name;
    this.expected = Arrays.toString(expected);
    this.actual = Arrays.toString(actual);
  }

  public String getName() {
    return name;
  }

  public String getExpected() {
    return expected;
  }

  public String getActual() {
    return actual;
  }

  public boolean differs() {
    return this.actual == null && expected == null ? true : !this.actual.equals(expected);
  }

  @Override
  public String toString() {
    if (differs()) {
      return name + "(expected: " + expected + ", but was: " + actual + ")";
    }
    return name + "(NoDiff)";
  }

}
