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
import cz.cvut.fel.nalida.interpretation.Interpretation;
import cz.cvut.fel.nalida.query.QueryPlan;
import cz.cvut.fel.nalida.schema.Schema;

@Singleton
@Path("/kos")
public class TestResource {

	Nalida core;

	public TestResource() throws Exception {
		this.core = new Nalida();
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getResponse(@QueryParam("q") String query, @QueryParam("t") Integer interpretationIndex) {
		if (query == null || query.trim().isEmpty()) {
			throw new NalidaException("No query submitted.");
		} else {
			Set<Interpretation> interpretations = this.core.getInterpretations(query);
			Interpretation interpretation;
			if (interpretations.isEmpty()) {
				throw new NalidaException("Failed to translate query. Try to reformulate it.");
			} else if (interpretations.size() == 1) {
				interpretation = interpretations.iterator().next();
			} else {
				if (interpretationIndex != null) {
					interpretation = pickInterpretation(interpretations, interpretationIndex);
				} else {
					return interpretationsToXML(interpretations);
				}
			}

			QueryPlan restQueryPlan = this.core.getRestQuery(interpretation);
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
			Set<Interpretation> interpretations = this.core.getInterpretations(query);
			Interpretation interpretation = pickTokenization(interpretations);
			QueryPlan sqlQueryPlan = this.core.getSqlQuery(interpretation);

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
			Set<Interpretation> interpretations = this.core.getInterpretations(query);
			Interpretation interpretation = pickTokenization(interpretations);
			QueryPlan restQueryPlan = this.core.getRestQuery(interpretation);
			QueryPlan sqlQueryPlan = this.core.getSqlQuery(interpretation);
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
			sb.append(interpretation.toString());
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
	public String getHTMLResponse(@QueryParam("q") String query, @QueryParam("t") Integer interpretationIndex) {
		try {
			return wrapInHTML("KOSapi response", getResponse(query, interpretationIndex));
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

	private Interpretation pickTokenization(Set<Interpretation> interpretations) {
		ArrayList<Interpretation> list = new ArrayList<>(interpretations);
		return list.get(new Random().nextInt(list.size()));
	}

	private Interpretation pickInterpretation(Set<Interpretation> interpretations, Integer interpretationIndex) {
		if (interpretationIndex.intValue() < 0) {
			return pickTokenization(interpretations);
		}
		return interpretationToList(interpretations).get(interpretationIndex.intValue());
	}

	private List<Interpretation> interpretationToList(Set<Interpretation> interpretations) {
		ArrayList<Interpretation> list = new ArrayList<>(interpretations);
		Collections.sort(list, new Comparator<Interpretation>() {
			@Override
			public int compare(Interpretation o1, Interpretation o2) {
				return o1.getTokens().toString().compareTo(o2.getTokens().toString());
			}
		});
		return list;
	}

	private String wrapInHTML(String title, String body) {
		return "<div class=\"modal-header\"><h4 class=\"modal-title\">" + title
				+ "</h4></div><div class=\"modal-body\"><textarea disabled>" + body + "</textarea></div>";
	}

	private String interpretationsToXML(Set<Interpretation> interpretations) {
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><interpretations>\n");
		int id = 0;
		for (Interpretation interpretation : interpretationToList(interpretations)) {
			sb.append("<interpretation><id>");
			sb.append(id++);
			sb.append("</id><tokens>");
			sb.append(interpretation.getElements().toString());
			sb.append("</tokens></interpretation>\n");
		}
		sb.append("</interpretations>");
		return sb.toString();
	}
}
