package de.tum.in.net.model;

import java.util.Arrays;

public class Diff {

  protected final String expected;
  protected final String actual;

  public Diff(String expected, String actual) {
    this.expected = expected;
    this.actual = actual;
  }

  public Diff(Integer expected, Integer actual) {
    this.expected = expected == null ? null : expected.toString();
    this.actual = actual == null ? null : actual.toString();
  }

  public Diff(int[] expected, int[] actual) {
    this.expected = Arrays.toString(expected);
    this.actual = Arrays.toString(actual);
  }

  public Diff(String[] expected, String[] actual) {
    this.expected = Arrays.toString(expected);
    this.actual = Arrays.toString(actual);
  }

  public String getExpected() {
    return expected;
  }

  public String getActual() {
    return actual;
  }

  public boolean differs() {
    return this.actual == null && expected == null ? false : !this.actual.equals(expected);
  }

  @Override
  public String toString() {
    if (differs()) {
      return "expected: " + expected + ", but was: " + actual + "";
    }
    return "NoDiff(" + actual + ")";
  }

}
