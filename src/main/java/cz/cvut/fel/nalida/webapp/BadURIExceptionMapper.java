package cz.cvut.fel.nalida.webapp;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.api.NotFoundException;

@Provider
public class BadURIExceptionMapper implements ExceptionMapper<NotFoundException> {

	@Override
	public Response toResponse(NotFoundException exception) {
		return Response.status(Response.Status.NOT_FOUND)
				.entity(new ErrorBean("URI " + exception.getNotFoundUri() + " not found.", Response.Status.NOT_FOUND.getStatusCode()))
				.build();

	}
}