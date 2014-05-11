package cz.cvut.fel.nalida.webapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.sun.jersey.api.core.HttpContext;

import cz.cvut.fel.nalida.Nalida;
import cz.cvut.fel.nalida.interpretation.Interpretation;

abstract public class AbstractResource {

	protected static final String INTERPRETATION_PARAM = "t";
	protected static final String QUERY_PARAM = "q";
	private static final boolean RANDOM_DISAMBIGUATION = false;
	Nalida core;

	public AbstractResource() throws Exception {
		this.core = new Nalida();
	}

	protected String evaluateQuery(HttpContext context, String query, Integer interpretationIndex) {
		if (query == null || query.trim().isEmpty()) {
			throw new NalidaException("No query submitted.");
		} else {
			Set<Interpretation> interpretations = this.core.getInterpretations(query);
			Interpretation interpretation;
			if (interpretations.isEmpty()) {
				throw new NalidaException("Failed to translate query '" + query + "'. Try to reformulate it.");
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

			return evaluateInterpretation(query, interpretation);
		}
	}

	abstract protected String evaluateInterpretation(String query, Interpretation interpretation);

	private Interpretation pickInterpretation(Set<Interpretation> interpretations) {
		List<Interpretation> list = interpretationToList(interpretations);
		int pickedIndex = RANDOM_DISAMBIGUATION ? new Random().nextInt(list.size()) : 0;
		return list.get(pickedIndex);
	}

	protected Interpretation pickInterpretation(Set<Interpretation> interpretations, Integer interpretationIndex) {
		if (interpretationIndex.intValue() < 0) {
			return pickInterpretation(interpretations);
		}
		return interpretationToList(interpretations).get(interpretationIndex.intValue());
	}

	protected List<Interpretation> interpretationToList(Set<Interpretation> interpretations) {
		ArrayList<Interpretation> list = new ArrayList<>(interpretations);
		Collections.sort(list, new Comparator<Interpretation>() {
			@Override
			public int compare(Interpretation o1, Interpretation o2) {
				return o1.getTokens().toString().compareTo(o2.getTokens().toString());
			}
		});
		return list;
	}

	protected String wrapInHTML(String title, String body) {
		return "<div class=\"modal-header\"><h4 class=\"modal-title\">" + title
				+ "</h4></div><div class=\"modal-body\"><textarea style=\"width:100%;height:40em\" disabled>" + body + "</textarea></div>";
	}

	protected String interpretationsToXML(String query, String uri, Set<Interpretation> interpretations) {
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
