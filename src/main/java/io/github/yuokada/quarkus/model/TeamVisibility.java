package io.github.yuokada.quarkus.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TeamVisibility {
  @JsonProperty("public")
  PUBLIC,
  @JsonProperty("private")
  PRIVATE
}
