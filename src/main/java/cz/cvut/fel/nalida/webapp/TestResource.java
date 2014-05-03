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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.resource.Singleton;

import cz.cvut.fel.nalida.Nalida;
import cz.cvut.fel.nalida.interpretation.Interpretation;
import cz.cvut.fel.nalida.query.QueryPlan;
import cz.cvut.fel.nalida.schema.Schema;

@Singleton
@Path("/kos")
public class TestResource {

	private static final String INTERPRETATION_PARAM = "t";
	private static final String QUERY_PARAM = "q";
	private static final boolean RANDOM_DISAMBIGUATION = false;
	Nalida core;

	public TestResource() throws Exception {
		this.core = new Nalida();
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getResponse(@Context HttpContext context, @QueryParam(QUERY_PARAM) String query,
			@QueryParam(INTERPRETATION_PARAM) Integer interpretationIndex) {

		if (query == null || query.trim().isEmpty()) {
			throw new NalidaException("No query submitted.");
		} else {
			Set<Interpretation> interpretations = this.core.getInterpretations(query);
			Interpretation interpretation;
			if (interpretations.isEmpty()) {
				throw new NalidaException("Failed to translate query \"" + query + "\". Try to reformulate it.");
			} else if (interpretations.size() == 1) {
				interpretation = interpretations.iterator().next();
			} else {
				if (interpretationIndex != null) {
					interpretation = pickInterpretation(interpretations, interpretationIndex);
				} else {
					String uri = context.getRequest().getRequestUri().toASCIIString();
					return interpretationsToXML(query, uri, interpretations);
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
	@Produces(MediaType.APPLICATION_XML)
	public String getSqlQuery(@Context HttpContext context, @QueryParam(QUERY_PARAM) String query,
			@QueryParam(INTERPRETATION_PARAM) Integer interpretationIndex) {
		if (query == null || query.trim().isEmpty()) {
			throw new NalidaException("No query submitted.");
		} else {
			Set<Interpretation> interpretations = this.core.getInterpretations(query);
			Interpretation interpretation;
			if (interpretations.isEmpty()) {
				throw new NalidaException("Failed to translate query \"" + query + "\". Try to reformulate it.");
			} else if (interpretations.size() == 1) {
				interpretation = interpretations.iterator().next();
			} else {
				if (interpretationIndex != null) {
					interpretation = pickInterpretation(interpretations, interpretationIndex);
				} else {
					String uri = context.getRequest().getRequestUri().toASCIIString();
					return interpretationsToXML(query, uri, interpretations);
				}
			}

			return sqlQueryPlanToXML(this.core.getSqlQuery(interpretation));
		}
	}

	@GET
	@Path("/debug")
	@Produces(MediaType.TEXT_PLAIN)
	public String getDebug(@Context HttpContext context, @QueryParam(QUERY_PARAM) String query,
			@QueryParam(INTERPRETATION_PARAM) Integer interpretationIndex) {
		if (query == null || query.trim().isEmpty()) {
			throw new NalidaException("No query submitted.");
		} else {
			Set<Interpretation> interpretations = this.core.getInterpretations(query);
			Interpretation interpretation;
			if (interpretations.isEmpty()) {
				throw new NalidaException("Failed to translate query \"" + query + "\". Try to reformulate it.");
			} else if (interpretations.size() == 1) {
				interpretation = interpretations.iterator().next();
			} else {
				if (interpretationIndex != null) {
					interpretation = pickInterpretation(interpretations, interpretationIndex);
				} else {
					String uri = context.getRequest().getRequestUri().toASCIIString();
					return interpretationsToXML(query, uri, interpretations);
				}
			}

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

			sb.append("Interpretation:".toUpperCase());
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

			sb.append("Response:".toUpperCase());
			sb.append("\n");
			sb.append(xmlResponse);

			return sb.toString();
		}
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getHTMLResponse(@Context HttpContext context, @QueryParam(QUERY_PARAM) String query,
			@QueryParam(INTERPRETATION_PARAM) Integer interpretationIndex) {
		try {
			return wrapInHTML("KOSapi response", getResponse(context, query, interpretationIndex));
		} catch (NalidaException e) {
			return wrapInHTML("Error", e.getMessage());
		}
	}

	@GET
	@Path("/sql")
	@Produces(MediaType.TEXT_HTML)
	public String getHTMLSqlQuery(@Context HttpContext context, @QueryParam(QUERY_PARAM) String query,
			@QueryParam(INTERPRETATION_PARAM) Integer interpretationIndex) {
		try {
			return wrapInHTML("SQL query", getSqlQuery(context, query, interpretationIndex));
		} catch (NalidaException e) {
			return wrapInHTML("Error", e.getMessage());
		}
	}

	@GET
	@Path("/debug")
	@Produces(MediaType.TEXT_HTML)
	public String getHTMLDebug(@Context HttpContext context, @QueryParam(QUERY_PARAM) String query,
			@QueryParam(INTERPRETATION_PARAM) Integer interpretationIndex) {
		try {
			return wrapInHTML("Detailed intermediate outputs", getDebug(context, query, interpretationIndex));
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

	private Interpretation pickInterpretation(Set<Interpretation> interpretations) {
		ArrayList<Interpretation> list = new ArrayList<>(interpretations);
		int pickedIndex = RANDOM_DISAMBIGUATION ? new Random().nextInt(list.size()) : 0;
		return list.get(pickedIndex);
	}

	private Interpretation pickInterpretation(Set<Interpretation> interpretations, Integer interpretationIndex) {
		if (interpretationIndex.intValue() < 0) {
			return pickInterpretation(interpretations);
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
				+ "</h4></div><div class=\"modal-body\"><textarea style=\"width:100%;height:40em\" disabled>" + body + "</textarea></div>";
	}

	private String sqlQueryPlanToXML(QueryPlan queryPlan) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<sql>" + queryPlan.toString() + "</sql>";
	}

	private String interpretationsToXML(String query, String uri, Set<Interpretation> interpretations) {
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<query>");
		sb.append(query);
		sb.append("</query>\n<interpretations>\n");
		int id = 0;
		for (Interpretation interpretation : interpretationToList(interpretations)) {
			sb.append("<interpretation>\n\t<link href=\"");
			sb.append(uri + '&' + INTERPRETATION_PARAM + '=' + id++);
			sb.append("\" />\n\t<tokens>");
			sb.append(interpretation.getElements().toString());
			sb.append("</tokens>\n</interpretation>\n");
		}
		sb.append("</interpretations>");
		return sb.toString();
	}
}
