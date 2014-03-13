package cz.cvut.fel.nalida.webapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.sun.jersey.spi.resource.Singleton;

import cz.cvut.fel.nalida.QueryGenerator;
import cz.cvut.fel.nalida.SemanticAnalysis;
import cz.cvut.fel.nalida.SyntacticAnalysis;
import cz.cvut.fel.nalida.Tokenization;
import cz.cvut.fel.nalida.db.Lexicon;
import edu.stanford.nlp.pipeline.Annotation;

@Singleton
@Path("/test")
public class TestResource {

	private static SyntacticAnalysis syntacticAnalysis;
	private static SemanticAnalysis semanticAnalysis;

	private static QueryGenerator queryGenerator;
	private static Lexicon lexicon;

	public TestResource() throws Exception {

		Properties properties = new Properties();
		properties.load(this.getClass().getClassLoader().getResourceAsStream("nlpcore.properties"));

		lexicon = new Lexicon("data/schema/");

		syntacticAnalysis = new SyntacticAnalysis(properties, lexicon);
		semanticAnalysis = new SemanticAnalysis(lexicon);

		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("db.properties"));

		queryGenerator = new QueryGenerator(null, new URL(props.getProperty("baseUrl")));

	}

	@GET
	@Produces("text/plain")
	public String process(@QueryParam("q") String query) {
		if (query == null) {
			return "No query submitted.";
		} else {
			Annotation annotatedQuery = syntacticAnalysis.process(query);
			Set<Tokenization> tokenizations = semanticAnalysis.getTokenizations(annotatedQuery);
			Tokenization tokenization = pickTokenization(tokenizations);

			return queryGenerator.generateQuery(tokenization);
		}
	}

	private Tokenization pickTokenization(Set<Tokenization> tokenizations) {
		ArrayList<Tokenization> list = new ArrayList<>(tokenizations);
		return list.get(new Random().nextInt(list.size()));
	}
}
