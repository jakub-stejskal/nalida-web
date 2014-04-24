package cz.cvut.fel.nalida.webapp;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class NalidaException extends WebApplicationException {

	private static final long serialVersionUID = 1L;

	public NalidaException(String message, Response.Status status) {
		super(Response.status(status).entity(new ErrorBean(message, status.getStatusCode())).build());
	}

	public NalidaException(String message) {
		this(message, Response.Status.BAD_REQUEST);
	}

	@Override
	public String getMessage() {
		return getResponse().getEntity().toString();
	}
}
