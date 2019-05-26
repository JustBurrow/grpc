package kr.lul.grpc.util.time;

import kr.lul.grpc.message.time.PeriodProto;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author justburrow
 * @since 2019-05-26
 */
public class TemporalAmountMessageBuilderTest {
  private static final Logger log = getLogger(TemporalAmountMessageBuilderTest.class);

  private TemporalAmountMessageBuilder builder;

  @Before
  public void setUp() throws Exception {
    this.builder = new TemporalAmountMessageBuilder() {
    };
  }

  @Test
  public void test_supportSourceTypes() throws Exception {
    assertThat(this.builder.supportsSourceTypes())
        .isNotEmpty();
  }

  @Test
  public void test_build_with_null() throws Exception {
    assertThatThrownBy(() -> this.builder.build(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("temporalAmount is null.");
  }

  @Test
  public void test_build_with_unsupported_type() throws Exception {
    assertThatThrownBy(() -> this.builder.build(""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("unsupported temporalAmount type");
  }

  @Test
  public void test_build_with_0_duration() throws Exception {
    // Given
    Duration duration = Duration.ZERO;
    log.info("GIVEN - duration={}", duration);

    // When
    com.google.protobuf.Duration message = this.builder.build(duration);
    log.info("WHEN - message={}", message);

    // Then
    assertThat(message)
        .isNotNull()
        .extracting(com.google.protobuf.Duration::getSeconds, com.google.protobuf.Duration::getNanos)
        .containsSequence(0L, 0);
  }

  @Test
  public void test_build_with_duration() throws Exception {
    // Given
    final Duration duration = Duration
        .between(Instant.now(), Instant.now().plusMillis(current().nextLong(1, 999_999_999L)));
    log.info("GIVEN - duration={}", duration);

    // When
    com.google.protobuf.Duration message = this.builder.build(duration);
    log.info("WHEN - message={}", message);

    // Then
    assertThat(message)
        .isNotNull()
        .extracting(com.google.protobuf.Duration::getSeconds, com.google.protobuf.Duration::getNanos)
        .containsSequence(duration.getSeconds(), duration.getNano());
  }

  @Test
  public void test_build_with_0_period() throws Exception {
    // Given
    Period period = Period.ZERO;
    log.info("GIVEN - period={}", period);

    // When
    PeriodProto.Period message = this.builder.build(period);
    log.info("WHEN - message={}", message);

    // Then
    assertThat(message)
        .isNotNull()
        .extracting(PeriodProto.Period::getYears, PeriodProto.Period::getMonths, PeriodProto.Period::getDays)
        .containsSequence(0, 0, 0);
  }

  @Test
  public void test_build_with_period() throws Exception {
    // Given
    Period period = Period.of(current().nextInt(), current().nextInt(), current().nextInt());
    log.info("GIVEN - period={}", period);

    // When
    PeriodProto.Period message = this.builder.build(period);
    log.info("WHEN - message={}", message);

    // Then
    assertThat(message)
        .isNotNull()
        .extracting(PeriodProto.Period::getYears, PeriodProto.Period::getMonths, PeriodProto.Period::getDays)
        .containsSequence(period.getYears(), period.getMonths(), period.getDays());
  }
}