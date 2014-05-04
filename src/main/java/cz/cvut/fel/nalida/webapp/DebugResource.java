package cz.cvut.fel.nalida.webapp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.resource.Singleton;

import cz.cvut.fel.nalida.interpretation.Interpretation;
import cz.cvut.fel.nalida.query.QueryPlan;

@Singleton
@Path("/kos/debug")
public class DebugResource extends AbstractResource {

	public DebugResource() throws Exception {
		super();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getDebug(@Context HttpContext context, @QueryParam(QUERY_PARAM) String query,
			@QueryParam(INTERPRETATION_PARAM) Integer interpretationIndex) {
		return evaluateQuery(context, query, interpretationIndex);
	}

	@Override
	protected String evaluateInterpretation(String query, Interpretation interpretation) {
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

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getHTMLDebug(@Context HttpContext context, @QueryParam(QUERY_PARAM) String query,
			@QueryParam(INTERPRETATION_PARAM) Integer interpretationIndex) {
		try {
			return wrapInHTML("Detailed debug log", getDebug(context, query, interpretationIndex));
		} catch (NalidaException e) {
			return wrapInHTML("Error", e.getMessage());
		}
	}
}
