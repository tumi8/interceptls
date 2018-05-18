/**
 * Copyright © 2018 Johannes Schleger (johannes.schleger@tum.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tum.in.net.analysis;

import java.util.Arrays;
import java.util.List;

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

  public Diff(List<?> expected, List<?> actual) {
    this.expected = expected == null ? null : expected.toString();
    this.actual = actual == null ? null : actual.toString();
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
