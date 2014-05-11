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
import cz.cvut.fel.nalida.schema.Schema;

@Singleton
@Path("kos")
public class ResponseResource extends AbstractResource {

	public ResponseResource() throws Exception {
		super();
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getResponse(@Context HttpContext context, @QueryParam(QUERY_PARAM) String query,
			@QueryParam(INTERPRETATION_PARAM) Integer interpretationIndex) {
		return evaluateQuery(context, query, interpretationIndex);
	}

	@Override
	protected String evaluateInterpretation(String query, Interpretation interpretation) {
		QueryPlan restQueryPlan = this.core.getRestQuery(interpretation);
		String xmlResponse;
		try {
			xmlResponse = restQueryPlan.execute();
		} catch (Exception e) {
			throw new NalidaException("Communication with KOSapi failed.", Status.INTERNAL_SERVER_ERROR);
		}
		return xmlResponse;
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
	@Path("/schema")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Schema getSchema() {
		return this.core.getSchema();
	}
}
