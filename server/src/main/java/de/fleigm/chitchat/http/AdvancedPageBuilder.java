package de.fleigm.chitchat.http;

import de.fleigm.chitchat.http.pagination.Page;
import de.fleigm.chitchat.http.pagination.Pagination;
import de.fleigm.chitchat.http.search.SearchFilter;
import de.fleigm.chitchat.http.search.SearchQuery;
import de.fleigm.chitchat.http.search.StreamSearchHelper;
import de.fleigm.chitchat.http.sort.SortQuery;
import de.fleigm.chitchat.http.sort.StreamSortHelper;

import javax.ws.rs.core.UriInfo;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdvancedPageBuilder<T> {
  private final StreamSearchHelper<T> searchHelper = new StreamSearchHelper<>();
  private final StreamSortHelper<T> sortHelper = new StreamSortHelper<>();

  private Pagination pagination;
  private SearchQuery searchQuery;
  private SortQuery sortQuery;
  private UriInfo uriInfo;

  public AdvancedPageBuilder<T> pagination(Pagination pagination) {
    this.pagination = pagination;
    return this;
  }

  public AdvancedPageBuilder<T> searchQuery(SearchQuery searchQuery) {
    this.searchQuery = searchQuery;
    return this;
  }

  public AdvancedPageBuilder<T> sortQuery(SortQuery sortQuery) {
    this.sortQuery = sortQuery;
    return this;
  }

  public AdvancedPageBuilder<T> uriInfo(UriInfo uriInfo) {
    this.uriInfo = uriInfo;
    return this;
  }

  public AdvancedPageBuilder<T> addSearch(String key, SearchFilter<T> searchFilter) {
    searchHelper.add(key, searchFilter);
    return this;
  }

  public AdvancedPageBuilder<T> addSort(String key, Comparator<T> comparator) {
    sortHelper.add(key, comparator);
    return this;
  }

  public Page<T> build(List<T> resource) {
    Stream<T> resourceStream = resource.stream();
    resourceStream = searchHelper.apply(searchQuery, resourceStream);
    resourceStream = sortHelper.apply(sortQuery, resourceStream);

    List<T> searchedAndSortedResource = resourceStream.collect(Collectors.toList());

    return Page.<T>builder()
        .currentPage(pagination.getPage())
        .perPage(pagination.getPageSize())
        .total(searchedAndSortedResource.size())
        .uri(uriInfo.getAbsolutePath())
        .data(searchedAndSortedResource.stream()
            .skip(pagination.getOffset())
            .limit(pagination.getPageSize())
            .collect(Collectors.toList()))
        .build();
  }

  public <R> Page<R> build(List<T> resource, Function<T, R> mapper) {
    Stream<T> resourceStream = resource.stream();
    resourceStream = searchHelper.apply(searchQuery, resourceStream);
    resourceStream = sortHelper.apply(sortQuery, resourceStream);

    List<T> searchedAndSortedResource = resourceStream.collect(Collectors.toList());

    return Page.<R>builder()
        .currentPage(pagination.getPage())
        .perPage(pagination.getPageSize())
        .total(searchedAndSortedResource.size())
        .uri(uriInfo.getAbsolutePath())
        .data(searchedAndSortedResource.stream()
            .skip(pagination.getOffset())
            .limit(pagination.getPageSize())
            .map(mapper)
            .collect(Collectors.toList()))
        .build();
  }
}

