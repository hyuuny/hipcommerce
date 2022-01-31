package com.hipcommerce.common.jpa.support;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class RestPageImpl<T> extends PageImpl<T> {

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public RestPageImpl(
      @JsonProperty("content") List<T> content,
      @JsonProperty("number") int number,
      @JsonProperty("size") int size,
      @JsonProperty("totalElements") Long totalElements,
      @JsonProperty("pageable") JsonNode pageable,
      @JsonProperty("last") boolean last,
      @JsonProperty("totalPages") int totalPages,
      @JsonProperty("sort") JsonNode sort,
      @JsonProperty("first") boolean first,
      @JsonProperty("numberOfElements") int numberOfElements,
      @JsonProperty("empty") boolean empty
  ) {
    super(content, PageRequest.of(number, size), totalElements);
  }

  public RestPageImpl(List<T> content, Pageable pageable, long total) {
    super(content, pageable, total);
  }

  public RestPageImpl(List<T> content) {
    super(content);
  }

  public RestPageImpl() {
    super(new ArrayList<T>());
  }

}
