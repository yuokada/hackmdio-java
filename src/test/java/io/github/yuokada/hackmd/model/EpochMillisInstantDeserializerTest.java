package io.github.yuokada.hackmd.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class EpochMillisInstantDeserializerTest {

  private final ObjectMapper mapper = new ObjectMapper();

  record Wrapper(
      @JsonDeserialize(using = EpochMillisInstantDeserializer.class) Instant value) {}

  @Test
  void deserializeEpochMillis() throws Exception {
    String json = "{\"value\": 1723685238684}";
    Wrapper result = mapper.readValue(json, Wrapper.class);
    assertEquals(Instant.ofEpochMilli(1723685238684L), result.value());
  }

  @Test
  void deserializeIsoString() throws Exception {
    String json = "{\"value\": \"2024-08-15T10:00:00Z\"}";
    Wrapper result = mapper.readValue(json, Wrapper.class);
    assertEquals(Instant.parse("2024-08-15T10:00:00Z"), result.value());
  }

  @Test
  void deserializeNullReturnsNull() throws Exception {
    String json = "{\"value\": null}";
    Wrapper result = mapper.readValue(json, Wrapper.class);
    assertNull(result.value());
  }

  @Test
  void deserializeEpochZero() throws Exception {
    String json = "{\"value\": 0}";
    Wrapper result = mapper.readValue(json, Wrapper.class);
    assertEquals(Instant.EPOCH, result.value());
  }

  @Test
  void deserializeBlankStringReturnsNull() throws Exception {
    String json = "{\"value\": \"  \"}";
    Wrapper result = mapper.readValue(json, Wrapper.class);
    assertNull(result.value());
  }

  @Test
  void deserializeEmptyStringReturnsNull() throws Exception {
    String json = "{\"value\": \"\"}";
    Wrapper result = mapper.readValue(json, Wrapper.class);
    assertNull(result.value());
  }
}
