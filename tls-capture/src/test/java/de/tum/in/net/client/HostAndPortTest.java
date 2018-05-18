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
package de.tum.in.net.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HostAndPortTest {

  @Test
  public void defaultPortIs443() {
    HostAndPort t = HostAndPort.parse("junit.org");
    assertEquals("junit.org", t.getHost());
    assertEquals(443, t.getPort());
  }

  @Test
  public void canParse() {
    HostAndPort t1 = HostAndPort.parse("junit.org");
    assertEquals("junit.org", t1.getHost());

    HostAndPort t2 = HostAndPort.parse("junit.org:678");
    assertEquals("junit.org", t2.getHost());
    assertEquals(678, t2.getPort());
  }

  @Test(expected = IllegalArgumentException.class)
  public void portMustBeNumber() {
    HostAndPort.parse("junit.org:abc");
  }

  @Test(expected = IllegalArgumentException.class)
  public void portNumberTooHigh() {
    HostAndPort.parse("junit.org:65536");
  }

  @Test(expected = IllegalArgumentException.class)
  public void portNumberTooLowh() {
    HostAndPort.parse("junit.org:-1");
  }

  @Test(expected = IllegalArgumentException.class)
  public void tooManyColons() {
    HostAndPort.parse("junit.org:443:123");
  }

  @Test
  public void toStringOutput() {
    HostAndPort t1 = HostAndPort.parse("junit.org");
    assertEquals("junit.org", t1.toString());

    HostAndPort t2 = HostAndPort.parse("junit.org:678");
    assertEquals("junit.org:678", t2.toString());
  }
}
