package cz.cvut.fel.nalida.webapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
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
	@Produces(MediaType.APPLICATION_XML)
	public String getResponse(@QueryParam("q") String query, @QueryParam("t") Integer tokenizationIndex) {
		if (query == null || query.trim().isEmpty()) {
			throw new NalidaException("No query submitted.");
		} else {
			Set<Tokenization> tokenizations = this.core.getTokenizations(query);
			Tokenization tokenization;
			if (tokenizations.isEmpty()) {
				throw new NalidaException("Failed to translate query. Try to reformulate it.");
			} else if (tokenizations.size() == 1) {
				tokenization = tokenizations.iterator().next();
			} else {
				if (tokenizationIndex != null) {
					tokenization = pickTokenization(tokenizations, tokenizationIndex);
				} else {
					return tokenizationsToXML(tokenizations);
				}
			}

			QueryPlan restQueryPlan = this.core.getRestQuery(tokenization);
			String xmlResponse;
			try {
				xmlResponse = restQueryPlan.execute();
			} catch (Exception e) {
				throw new NalidaException("Communication with KOSapi failed.", Status.INTERNAL_SERVER_ERROR);
			}
			return xmlResponse;
		}
	}

	@GET
	@Path("/sql")
	@Produces(MediaType.TEXT_PLAIN)
	public String getSqlQuery(@QueryParam("q") String query) {
		if (query == null || query.trim().isEmpty()) {
			throw new NalidaException("No query submitted.");
		} else {
			Set<Tokenization> tokenizations = this.core.getTokenizations(query);
			Tokenization tokenization = pickTokenization(tokenizations);
			QueryPlan sqlQueryPlan = this.core.getSqlQuery(tokenization);

			return sqlQueryPlan.toString();
		}
	}

	@GET
	@Path("/debug")
	@Produces(MediaType.TEXT_PLAIN)
	public String getDebug(@QueryParam("q") String query) {
		if (query == null || query.trim().isEmpty()) {
			throw new NalidaException("No query submitted.");
		} else {
			Set<Tokenization> tokenizations = this.core.getTokenizations(query);
			Tokenization tokenization = pickTokenization(tokenizations);
			QueryPlan restQueryPlan = this.core.getRestQuery(tokenization);
			QueryPlan sqlQueryPlan = this.core.getSqlQuery(tokenization);
			String xmlResponse;
			try {
				xmlResponse = restQueryPlan.execute();
			} catch (Exception e) {
				throw new NalidaException("Communication with KOSapi failed.", Status.INTERNAL_SERVER_ERROR);
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
	@Produces(MediaType.TEXT_HTML)
	public String getHTMLResponse(@QueryParam("q") String query, @QueryParam("t") Integer tokenizationIndex) {
		try {
			return wrapInHTML("KOSapi response", getResponse(query, tokenizationIndex));
		} catch (NalidaException e) {
			return wrapInHTML("Error", e.getMessage());
		}
	}

	@GET
	@Path("/sql")
	@Produces(MediaType.TEXT_HTML)
	public String getHTMLSqlQuery(@QueryParam("q") String query) {
		try {
			return wrapInHTML("SQL query", getSqlQuery(query));
		} catch (NalidaException e) {
			return wrapInHTML("Error", e.getMessage());
		}
	}

	@GET
	@Path("/debug")
	@Produces(MediaType.TEXT_HTML)
	public String getHTMLDebug(@QueryParam("q") String query) {
		try {
			return wrapInHTML("Detailed intermediate outputs", getDebug(query));
		} catch (NalidaException e) {
			return wrapInHTML("Error", e.getMessage());
		}
	}

	@GET
	@Path("/schema")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Schema getSchema() {
		return this.core.getSchema();
	}

	private Tokenization pickTokenization(Set<Tokenization> tokenizations) {
		ArrayList<Tokenization> list = new ArrayList<>(tokenizations);
		return list.get(new Random().nextInt(list.size()));
	}

	private Tokenization pickTokenization(Set<Tokenization> tokenizations, Integer tokenizationIndex) {
		return tokenizationToList(tokenizations).get(tokenizationIndex.intValue());
	}

	private List<Tokenization> tokenizationToList(Set<Tokenization> tokenizations) {
		ArrayList<Tokenization> list = new ArrayList<>(tokenizations);
		Collections.sort(list, new Comparator<Tokenization>() {
			@Override
			public int compare(Tokenization o1, Tokenization o2) {
				return o1.getTokens().toString().compareTo(o2.getTokens().toString());
			}
		});
		return list;
	}

	private String wrapInHTML(String title, String body) {
		return "<div class=\"modal-header\"><h4 class=\"modal-title\">" + title
				+ "</h4></div><div class=\"modal-body\"><textarea disabled>" + body + "</textarea></div>";
	}

	private String tokenizationsToXML(Set<Tokenization> tokenizations) {
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><tokenizations>");
		int id = 0;
		for (Tokenization tokenization : tokenizationToList(tokenizations)) {
			sb.append("<tokenization><id>");
			sb.append(id++);
			sb.append("</id><tokens>");
			sb.append(tokenization.getElements().toString());
			sb.append("</tokens></tokenization>");
		}
		sb.append("</tokenizations>");
		return sb.toString();
	}
}
