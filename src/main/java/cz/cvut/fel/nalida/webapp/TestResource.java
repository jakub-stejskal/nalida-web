package cz.cvut.fel.nalida.webapp;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.spi.resource.Singleton;

import cz.cvut.fel.nalida.Nalida;
import cz.cvut.fel.nalida.query.QueryPlan;
import cz.cvut.fel.nalida.schema.Schema;
import cz.cvut.fel.nalida.tokenization.Tokenization;

@Singleton
@Path("/kos")
public class TestResource {

	Nalida core;

	public TestResource() throws Exception {
		this.core = new Nalida();
	}

	@GET
	@Produces("text/plain")
	public String getResponse(@QueryParam("q") String query) {
		if (query == null || query.trim().isEmpty()) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("No query submitted.").build());
		} else {
			Set<Tokenization> tokenizations = this.core.getTokenizations(query);
			Tokenization tokenization = pickTokenization(tokenizations);
			QueryPlan restQueryPlan = this.core.getRestQuery(tokenization);

			String xmlResponse;
			try {
				xmlResponse = restQueryPlan.execute();
			} catch (Exception e) {
				throw new WebApplicationException(e, Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity("Communication with KOSapi failed.").build());
			}
			return xmlResponse;
		}
	}

	@GET
	@Path("/sql")
	@Produces("text/plain")
	public String getSqlQuery(@QueryParam("q") String query) {
		if (query == null || query.trim().isEmpty()) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("No query submitted.").build());
		} else {
			Set<Tokenization> tokenizations = this.core.getTokenizations(query);
			Tokenization tokenization = pickTokenization(tokenizations);
			QueryPlan sqlQueryPlan = this.core.getSqlQuery(tokenization);

			return sqlQueryPlan.toString();
		}
	}

	@GET
	@Path("/debug")
	@Produces("text/plain")
	public String getDebug(@QueryParam("q") String query) {
		if (query == null || query.trim().isEmpty()) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("No query submitted.").build());
		} else {
			Set<Tokenization> tokenizations = this.core.getTokenizations(query);
			Tokenization tokenization = pickTokenization(tokenizations);
			QueryPlan restQueryPlan = this.core.getRestQuery(tokenization);
			QueryPlan sqlQueryPlan = this.core.getSqlQuery(tokenization);
			String xmlResponse;
			try {
				xmlResponse = restQueryPlan.execute();
			} catch (Exception e) {
				throw new WebApplicationException(e, Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity("Communication with KOSapi failed.").build());
			}

			StringBuilder sb = new StringBuilder();
			sb.append("Submitted question:".toUpperCase());
			sb.append("\n");
			sb.append(query);
			sb.append("\n\n");

			sb.append("Tokenization:".toUpperCase());
			sb.append("\n");
			sb.append(tokenization.toString());
			sb.append("\n\n");

			sb.append("SQL Query:".toUpperCase());
			sb.append("\n");
			sb.append(sqlQueryPlan);
			sb.append("\n\n");

			sb.append("REST Query:".toUpperCase());
			sb.append("\n");
			sb.append(restQueryPlan);
			sb.append("\n\n");

			sb.append("Responses:".toUpperCase());
			sb.append("\n");
			sb.append(xmlResponse);

			return sb.toString();
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/schema")
	public Schema getSchema() {
		return this.core.getSchema();
	}

	private Tokenization pickTokenization(Set<Tokenization> tokenizations) {
		ArrayList<Tokenization> list = new ArrayList<>(tokenizations);
		return list.get(new Random().nextInt(list.size()));
	}
}
