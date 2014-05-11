package cz.cvut.fel.nalida.webapp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.resource.Singleton;

import cz.cvut.fel.nalida.interpretation.Interpretation;
import cz.cvut.fel.nalida.query.QueryPlan;

@Singleton
@Path("kos/sql")
public class SqlResource extends AbstractResource {

	public SqlResource() throws Exception {
		super();
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String getSqlQuery(@Context HttpContext context, @QueryParam(QUERY_PARAM) String query,
			@QueryParam(INTERPRETATION_PARAM) Integer interpretationIndex) {
		return evaluateQuery(context, query, interpretationIndex);
	}

	@Override
	protected String evaluateInterpretation(String query, Interpretation interpretation) {
		return sqlQueryPlanToXML(this.core.getSqlQuery(interpretation));
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getHTMLSqlQuery(@Context HttpContext context, @QueryParam(QUERY_PARAM) String query,
			@QueryParam(INTERPRETATION_PARAM) Integer interpretationIndex) {
		try {
			return wrapInHTML("SQL query", getSqlQuery(context, query, interpretationIndex));
		} catch (NalidaException e) {
			return wrapInHTML("Error", e.getMessage());
		}
	}

	private String sqlQueryPlanToXML(QueryPlan queryPlan) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<sql>" + queryPlan.toString() + "</sql>";
	}

}
