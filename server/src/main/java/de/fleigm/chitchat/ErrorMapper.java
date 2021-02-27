package de.fleigm.chitchat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ErrorMapper implements ExceptionMapper<WebApplicationException> {
  private static final Logger LOGGER = Logger.getLogger(ErrorMapper.class);

  @Inject
  ObjectMapper objectMapper;

  @Override
  public Response toResponse(WebApplicationException exception) {
    LOGGER.error(exception);

    int code = exception.getResponse().getStatus();

    ObjectNode exceptionJson = objectMapper.createObjectNode();
    exceptionJson.put("exceptionType", exception.getClass().getName());
    exceptionJson.put("code", code);

    if (exception.getMessage() != null) {
      exceptionJson.put("error", exception.getMessage());
    }

    return Response.status(code)
        .entity(exceptionJson)
        .build();
  }

}