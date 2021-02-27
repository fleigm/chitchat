package de.fleigm.chitchat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

public final class EntityNotFoundException extends RuntimeException {
  private final Object id;
  private final Class<?> entityClass;

  public EntityNotFoundException(Object id, Class<?> entityClass) {
    super(String.format("Could not find entity of type %s with id %s", entityClass, id));

    this.id = id;
    this.entityClass = entityClass;
  }

  public static EntityNotFoundException of(Object id, Class<?> entityClass) {
    return new EntityNotFoundException(id, entityClass);
  }

  public Object id() {
    return id;
  }

  public Class<?> entityClass() {
    return entityClass;
  }

  @Override
  public String toString() {
    return "EntityNotFoundException{" +
           "id=" + id +
           ", entityClass=" + entityClass +
           '}';
  }

  @Provider
  public static final class JaxRSErrorMapper implements ExceptionMapper<EntityNotFoundException> {
    @Inject
    ObjectMapper objectMapper;

    @Override
    public Response toResponse(EntityNotFoundException exception) {
      ObjectNode exceptionJson = objectMapper.createObjectNode();
      exceptionJson.put("exceptionType", exception.getClass().getName());
      exceptionJson.put("code", 404);

      if (exception.getMessage() != null) {
        exceptionJson.put("error", exception.getMessage());
      }

      return Response.status(404)
          .entity(exceptionJson)
          .build();
    }
  }
}
