package de.tum.in.net.resource;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import de.tum.in.net.analysis.GeneralStatistic;
import de.tum.in.net.model.DatabaseService;

@Path("statistic")
public class StatisticResource {

  private static final Logger log = LogManager.getLogger();

  @Inject
  private DatabaseService db;


  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getStats() {

    try {
      GeneralStatistic stats = db.getGeneralStatistic();
      return new Gson().toJson(stats);
    } catch (SQLException e) {
      log.error("Could not determine general stats.", e);
      throw new InternalServerErrorException();
    }
  }
}
