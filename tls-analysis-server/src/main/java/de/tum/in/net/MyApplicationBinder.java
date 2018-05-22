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
package de.tum.in.net;

import javax.inject.Singleton;

import org.glassfish.jersey.internal.inject.AbstractBinder;

import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.HandshakeAnalyser;
import de.tum.in.net.model.HandshakeParser;
import de.tum.in.net.services.RustHandshakeParser;
import de.tum.in.net.services.TlsHandshakeAnalyser;

public class MyApplicationBinder extends AbstractBinder {

  private DatabaseService db;

  public MyApplicationBinder(DatabaseService dbService) {
    this.db = dbService;
  }

  @Override
  protected void configure() {
    bind(db).to(DatabaseService.class).in(Singleton.class);
    bind(RustHandshakeParser.class).to(HandshakeParser.class);
    bind(TlsHandshakeAnalyser.class).to(HandshakeAnalyser.class);
  }

}
