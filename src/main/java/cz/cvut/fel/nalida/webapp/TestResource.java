package cz.cvut.fel.nalida.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
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

import cz.cvut.fel.nalida.QueryGenerator;
import cz.cvut.fel.nalida.RestQueryGenerator;
import cz.cvut.fel.nalida.SemanticAnalysis;
import cz.cvut.fel.nalida.SqlQueryGenerator;
import cz.cvut.fel.nalida.SyntacticAnalysis;
import cz.cvut.fel.nalida.Tokenization;
import cz.cvut.fel.nalida.db.Lexicon;
import cz.cvut.fel.nalida.db.QueryPlan;
import cz.cvut.fel.nalida.db.Schema;
import edu.stanford.nlp.pipeline.Annotation;

@Singleton
@Path("/kos")
public class TestResource {

	private static SyntacticAnalysis syntacticAnalysis;
	private static SemanticAnalysis semanticAnalysis;

	private static QueryGenerator restQueryGenerator;
	private static QueryGenerator sqlQueryGenerator;
	private static Schema schema;

	public TestResource() throws Exception {

		Properties properties = new Properties();
		properties.load(this.getClass().getClassLoader().getResourceAsStream("nlpcore.properties"));

		InputStream input = new FileInputStream(new File("data/schema/schema.desc"));
		schema = Schema.load(input);
		Lexicon lexicon = new Lexicon(schema, "data/schema/");

		syntacticAnalysis = new SyntacticAnalysis(properties, lexicon);
		semanticAnalysis = new SemanticAnalysis(lexicon);

		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("db.properties"));

		restQueryGenerator = new RestQueryGenerator(schema, props);
		sqlQueryGenerator = new SqlQueryGenerator(schema, props);
	}

	@GET
	@Produces("text/plain")
	public String getResponse(@QueryParam("q") String query) {
		if (query == null || query.trim().isEmpty()) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("No query submitted.").build());
		} else {
			Annotation annotatedQuery = syntacticAnalysis.process(query);
			Set<Tokenization> tokenizations = semanticAnalysis.getTokenizations(annotatedQuery);
			Tokenization tokenization = pickTokenization(tokenizations);
			QueryPlan restQueryPlan = restQueryGenerator.generateQuery(tokenization);

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
			Annotation annotatedQuery = syntacticAnalysis.process(query);
			Set<Tokenization> tokenizations = semanticAnalysis.getTokenizations(annotatedQuery);
			Tokenization tokenization = pickTokenization(tokenizations);
			QueryPlan sqlQueryPlan = sqlQueryGenerator.generateQuery(tokenization);

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
			Annotation annotatedQuery = syntacticAnalysis.process(query);
			Set<Tokenization> tokenizations = semanticAnalysis.getTokenizations(annotatedQuery);
			Tokenization tokenization = pickTokenization(tokenizations);
			QueryPlan restQueryPlan = restQueryGenerator.generateQuery(tokenization);
			QueryPlan sqlQueryPlan = sqlQueryGenerator.generateQuery(tokenization);
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
		return schema;
	}

	private Tokenization pickTokenization(Set<Tokenization> tokenizations) {
		ArrayList<Tokenization> list = new ArrayList<>(tokenizations);
		return list.get(new Random().nextInt(list.size()));
	}
}
