package cz.cvut.fel.nalida.webapp;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.core.ExtendedUriInfo;

@Provider
public class BadURIExceptionMapper implements ExceptionMapper<NotFoundException> {

	@Context
	private HttpHeaders headers;

	@Context
	private ExtendedUriInfo uriInfo;

	@Override
	public Response toResponse(NotFoundException exception) {
		if (this.headers.getMediaType() == null || this.headers.getMediaType().equals(MediaType.TEXT_HTML)) {
			return Response
					.status(Response.Status.NOT_FOUND)
					.type(MediaType.TEXT_HTML)
					.entity("<h1>Page not found</h1><p>URI " + exception.getNotFoundUri()
							+ " not found.</p><p>Continue to the <a href=\"/nalida-web\">home page</a>.</p>").build();
		} else {
			return Response.status(Response.Status.NOT_FOUND)
					.entity(new ErrorBean("URI " + exception.getNotFoundUri() + " not found.", Response.Status.NOT_FOUND.getStatusCode()))
					.build();
		}

	}
}