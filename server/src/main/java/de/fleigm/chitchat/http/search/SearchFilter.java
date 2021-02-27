package de.fleigm.chitchat.http.search;

@FunctionalInterface
public interface SearchFilter<T> {

  boolean apply(SearchCriteria searchCriteria, T object);
}
