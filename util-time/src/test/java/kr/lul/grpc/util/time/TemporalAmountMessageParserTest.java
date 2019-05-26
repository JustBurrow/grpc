package kr.lul.grpc.util.time;

import com.google.protobuf.Empty;
import kr.lul.grpc.message.time.PeriodProto;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.Period;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author justburrow
 * @since 2019-05-26
 */
public class TemporalAmountMessageParserTest {
  private static final Logger log = getLogger(TemporalAmountMessageParserTest.class);

  private TemporalAmountMessageParser parser;

  private TemporalAmountMessageBuilder builder;

  @Before
  public void setUp() throws Exception {
    this.parser = new TemporalAmountMessageParser() {
    };
    this.builder = new TemporalAmountMessageBuilder() {
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
        .hasMessageStartingWith("unsupported temporal amount message type");
  }

  @Test
  public void test_parse_0_duration() throws Exception {
    // Given
    Duration expected = Duration.ZERO;
    com.google.protobuf.Duration message = this.builder.build(expected);
    log.info("GIVEN - expected={}, message={}", expected, message);

    // When
    Duration actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isSameAs(expected);
  }

  @Test
  public void test_parse_duration() throws Exception {
    // Given
    Duration expected = Duration.ofSeconds(current().nextLong(-999_999_999L, 999_999_999L),
        current().nextLong(999_999_999L));
    com.google.protobuf.Duration message = this.builder.build(expected);
    log.info("GIVEN - expected={}, message={}", expected, message);

    // When
    Duration actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }

  @Test
  public void test_parse_0_period() throws Exception {
    // Given
    Period expected = Period.ZERO;
    PeriodProto.Period message = this.builder.build(expected);
    log.info("GIVEN - expected={}, message={}", expected, message);

    // When
    Period actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isSameAs(expected);
  }

  @Test
  public void test_parse_period() throws Exception {
    // Given
    Period expected = Period.of(current().nextInt(), current().nextInt(), current().nextInt());
    PeriodProto.Period message = this.builder.build(expected);
    log.info("GIVEN - expected={}, message={}", expected, message);

    // When
    Period actual = this.parser.parse(message);
    log.info("WHEN - actual={}", actual);

    // Then
    assertThat(actual)
        .isNotNull()
        .isNotSameAs(expected)
        .isEqualTo(expected);
  }
}