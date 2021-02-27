package de.fleigm.realtalk;

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
}
