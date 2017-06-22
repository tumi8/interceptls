package de.tum.in.net.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
@Priority(Integer.MIN_VALUE)
public class LoggingFilter implements ContainerRequestFilter {

  private static final Logger log = LogManager.getLogger();

  @Override
  public void filter(ContainerRequestContext context) throws IOException {
    log.info("{} {}", context.getMethod(), context.getUriInfo().getPath(true));
  }

}
