package io.github.yuokada.hackmd.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;

/**
 * Jackson deserializer that converts epoch millisecond values to {@link Instant}.
 */
public class EpochMillisInstantDeserializer extends JsonDeserializer<Instant> {

  @Override
  public Instant deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
    if (parser.currentToken().isNumeric()) {
      return Instant.ofEpochMilli(parser.getLongValue());
    }
    String text = parser.getValueAsString();
    if (text == null || text.isBlank()) {
      return null;
    }
    // Fallback to ISO-8601 parsing if the API ever returns string timestamps.
    return Instant.parse(text);
  }
}
