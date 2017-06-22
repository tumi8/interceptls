package de.tum.in.net;

import javax.inject.Singleton;

import org.glassfish.jersey.internal.inject.AbstractBinder;

import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.HandshakeParser;
import de.tum.in.net.services.RustHandshakeParser;

public class MyApplicationBinder extends AbstractBinder {

  private DatabaseService db;

  public MyApplicationBinder(DatabaseService dbService) {
    this.db = dbService;
  }

  @Override
  protected void configure() {
    bind(db).to(DatabaseService.class).in(Singleton.class);
    bind(RustHandshakeParser.class).to(HandshakeParser.class);
  }

}
