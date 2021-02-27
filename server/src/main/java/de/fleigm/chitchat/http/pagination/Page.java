package de.fleigm.chitchat.http.pagination;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import lombok.Data;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;


@Data
public class Page<T> {

  private final long total;
  private final int pageSize;
  private final long from;
  private final long to;
  private final int currentPage;
  private final int lastPage;
  private final URI prevPageUrl;
  private final URI nextPageUrl;
  private final URI firstPageUrl;
  private final URI lastPageUrl;

  private final List<T> entries;

  public Page(URI uri, int currentPage, int pageSize, long total, List<T> entries) {
    this.currentPage = currentPage;
    this.pageSize = pageSize;
    this.total = total;
    this.entries = entries;
    this.lastPage = (int) Math.ceil(total / (double) pageSize);
    this.from = (currentPage - 1) * pageSize;
    this.to = currentPage * pageSize;
    lastPageUrl = UriBuilder.fromUri(uri)
        .queryParam("page", lastPage)
        .build();

    firstPageUrl = UriBuilder.fromUri(uri)
        .queryParam("page", 1)
        .build();

      nextPageUrl = UriBuilder.fromUri(uri)
          .queryParam("page", Math.min(this.currentPage + 1, lastPage))
          .build();

      prevPageUrl = UriBuilder.fromUri(uri)
          .queryParam("page", Math.max(this.currentPage - 1, 1))
          .build();
  }

  public static <T> PageBuilder<T> builder() {
    return new PageBuilder<T>();
  }

  public static <T> Page<T> create(PanacheQuery<T> query, Pagination pagination, UriInfo uriInfo) {
    return Page.<T>builder()
        .entries(query.list())
        .currentPage(pagination.getPage())
        .pageSize(pagination.getPageSize())
        .total(query.count())
        .uri(uriInfo.getAbsolutePath())
        .build();
  }


  public static class PageBuilder<T> {
    private URI uri;
    private int currentPage;
    private int pageSize;
    private long total;
    private List<T> entries;

    PageBuilder() {
    }

    public PageBuilder<T> uri(URI uri) {
      this.uri = uri;
      return this;
    }

    public PageBuilder<T> currentPage(int currentPage) {
      this.currentPage = currentPage;
      return this;
    }

    public PageBuilder<T> pageSize(int pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public PageBuilder<T> total(long total) {
      this.total = total;
      return this;
    }

    public PageBuilder<T> entries(List<T> entries) {
      this.entries = entries;
      return this;
    }

    public Page<T> build() {
      return new Page<T>(uri, currentPage, pageSize, total, entries);
    }
  }
}
