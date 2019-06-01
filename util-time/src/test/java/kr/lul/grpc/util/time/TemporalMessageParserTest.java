package kr.lul.grpc.util.time;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import kr.lul.grpc.message.time.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author justburrow
 * @since 2019-05-26
 */
public class TemporalMessageParserTest {
  private static final Logger log = getLogger(TemporalMessageParserTest.class);

  private TemporalMessageParser parser;
  private TemporalMessageBuilder builder;

  @Before
  public void setUp() throws Exception {
    this.parser = new TemporalMessageParser() {
    };
    this.builder = new TemporalMessageBuilder() {
    };
  }

  @Test
  public void test_supportsMessageTypes() throws Exception {
    assertThat(this.parser.supportsMessageTypes())
        .isNotEmpty();
  }

  @Test
  public void test_parse_with_null() throws Exception {
    assertThatThrownBy(() -> this.parser.parse(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("message is null.");
  }

  @Test
  public void test_parse_with_unsupported_message() throws Exception {
    assertThatThrownBy(() -> this.parser.parse(Empty.newBuilder().build()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("unsupported temporal message type");
  }

  @Test
  public void test_parse_with_Timestamp() throws Exception {
    // Given
    Instant expected = Instant.now();
    Timestamp timestamp = this.builder.build(expected);
    log.info("GIVEN - expected={}, timestamp={}", expected, timestamp);

    // When
    Instant actual = this.parser.parse(timestamp);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }

  @Test
  public void test_parse_with_ZonedDateTime() throws Exception {
    // Given
    ZonedDateTime expected = ZonedDateTime.now();
    ZonedDateTimeProto.ZonedDateTime message = this.builder.build(expected);
    log.info("GIVEN - expected={}, message={}", expected, message);

    // When
    ZonedDateTime actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }

  @Test
  public void test_parse_with_OffsetDateTime() throws Exception {
    // Given
    OffsetDateTime expected = OffsetDateTime.now();
    OffsetDateTimeProto.OffsetDateTime message = this.builder.build(expected);
    log.info("GIVEN - expected={}, message={}", expected, message);

    // When
    OffsetDateTime actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }

  @Test
  public void test_parse_with_OffsetTime() throws Exception {
    // Given
    OffsetTime expected = OffsetTime.now();
    OffsetTimeProto.OffsetTime message = this.builder.build(expected);
    log.info("GIVEN - expected={}, message={}", expected, message);

    // When
    OffsetTime actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }

  @Test
  public void test_parse_with_LocalDate() throws Exception {
    // Given
    LocalDate expected = LocalDate.now();
    LocalDateProto.LocalDate message = this.builder.build(expected);
    log.info("GIVEN - expected={}, message={}", expected, message);

    // When
    LocalDate actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }

  @Test
  public void test_parse_with_LocalDateTime() throws Exception {
    // Given
    LocalDateTime expected = LocalDateTime.now();
    LocalDateTimeProto.LocalDateTime message = this.builder.build(expected);
    log.info("GIVEN - expected={}, message={}", expected, message);

    // When
    LocalDateTime actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }

  @Test
  public void test_parse_with_LocalTime() throws Exception {
    // Given
    LocalTime expected = LocalTime.now();
    LocalTimeProto.LocalTime message = this.builder.build(expected);
    log.info("GIVEN - expected={}, message={}", expected, message);

    // When
    LocalTime actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }

  @Test
  public void test_parse_with_temporal_timestamp() throws Exception {
    // Given
    Instant expected = Instant.now();
    log.info("GIVEN - expected={}", expected);

    TemporalProto.Temporal message = TemporalProto.Temporal.newBuilder()
        .setTimestamp((Timestamp) this.builder.build(expected))
        .build();
    log.info("GIVEN - message={}", message);

    // When
    Instant actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }

  @Test
  public void test_parse_with_temporal_ZonedDateTime() throws Exception {
    // Given
    ZonedDateTime expected = ZonedDateTime.now();
    log.info("GIVEN - expected={}", expected);

    TemporalProto.Temporal message = TemporalProto.Temporal.newBuilder()
        .setZonedDateTime((ZonedDateTimeProto.ZonedDateTime) this.builder.build(expected))
        .build();
    log.info("GIVEN - message={}", message);

    // When
    ZonedDateTime actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }

  @Test
  public void test_parse_with_temporal_OffsetDateTime() throws Exception {
    // Given
    OffsetDateTime expected = OffsetDateTime.now();
    log.info("GIVEN - expected={}", expected);

    TemporalProto.Temporal message = TemporalProto.Temporal.newBuilder()
        .setOffsetDateTime((OffsetDateTimeProto.OffsetDateTime) this.builder.build(expected))
        .build();
    log.info("GIVEN - message={}", message);

    // When
    OffsetDateTime actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }

  @Test
  public void test_parse_with_temporal_OffsetTime() throws Exception {
    // Given
    OffsetTime expected = OffsetTime.now();
    log.info("GIVEN - expected={}", expected);

    TemporalProto.Temporal message = TemporalProto.Temporal.newBuilder()
        .setOffsetTime((OffsetTimeProto.OffsetTime) this.builder.build(expected))
        .build();
    log.info("GIVEN - message={}", message);

    // When
    OffsetTime actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }

  @Test
  public void test_parse_with_temporal_LocalDate() throws Exception {
    // Given
    LocalDate expected = LocalDate.now();
    log.info("GIVEN - expected={}", expected);

    TemporalProto.Temporal message = TemporalProto.Temporal.newBuilder()
        .setLocalDate((LocalDateProto.LocalDate) this.builder.build(expected))
        .build();
    log.info("GIVEN - message={}", message);

    // When
    LocalDate actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }

  @Test
  public void test_parse_with_temporal_LocalDateTime() throws Exception {
    // Given
    LocalDateTime expected = LocalDateTime.now();
    log.info("GIVEN - expected={}", expected);

    TemporalProto.Temporal message = TemporalProto.Temporal.newBuilder()
        .setLocalDateTime((LocalDateTimeProto.LocalDateTime) this.builder.build(expected))
        .build();
    log.info("GIVEN - message={}", message);

    // When
    LocalDateTime actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }

  @Test
  public void test_parse_with_temporal_LocalTime() throws Exception {
    // Given
    LocalTime expected = LocalTime.now();
    log.info("GIVEN - expected={}", expected);

    TemporalProto.Temporal message = TemporalProto.Temporal.newBuilder()
        .setLocalTime((LocalTimeProto.LocalTime) this.builder.build(expected))
        .build();
    log.info("GIVEN - message={}", message);

    // When
    LocalTime actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }
}