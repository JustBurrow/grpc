package kr.lul.grpc.util.time;

import com.google.protobuf.Timestamp;
import kr.lul.grpc.message.time.OffsetDateTimeProto;
import kr.lul.grpc.message.time.OffsetTimeProto;
import kr.lul.grpc.message.time.ZonedDateTimeProto;
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
public class TemporalMessageBuilderTest {
  private static final Logger log = getLogger(TemporalMessageBuilderTest.class);

  private TemporalMessageBuilder generator;

  @Before
  public void setUp() throws Exception {
    this.generator = new TemporalMessageBuilder() {
    };
  }

  @Test
  public void test_supportsSourceTypes() throws Exception {
    assertThat(this.generator.supportsSourceTypes())
        .isNotEmpty();
  }

  @Test
  public void test_build_with_null() throws Exception {
    assertThatThrownBy(() -> this.generator.build(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("temporal is null.");
  }

  @Test
  public void test_build_with_now_Instant() throws Exception {
    // Given
    final Instant instant = Instant.now();
    log.info("GIVEN - instant={}, seconds={}, nanos={}", instant, instant.getEpochSecond(), instant.getNano());

    // When
    Timestamp message = this.generator.build(instant);
    log.info("WHEN - message={}", message);

    // Then
    assertThat(message)
        .isNotNull()
        .extracting(Timestamp::getSeconds, Timestamp::getNanos)
        .containsSequence(instant.getEpochSecond(), instant.getNano());
  }

  @Test
  public void test_build_with_now_ZonedDateTime() throws Exception {
    // Given
    ZonedDateTime zonedDateTime = ZonedDateTime.now();
    log.info("GIVEN - zonedDateTime={}, zone={}, seconds={}, nanos={}", zonedDateTime,
        zonedDateTime.getZone(), zonedDateTime.toEpochSecond(), zonedDateTime.getNano());

    // When
    ZonedDateTimeProto.ZonedDateTime message = this.generator.build(zonedDateTime);
    log.info("WHEN - message={}", message);

    // Then
    assertThat(message)
        .isNotNull()
        .extracting(ZonedDateTimeProto.ZonedDateTime::getZoneId, ZonedDateTimeProto.ZonedDateTime::getSeconds,
            ZonedDateTimeProto.ZonedDateTime::getNanos)
        .containsSequence(zonedDateTime.getZone().getId(), zonedDateTime.toEpochSecond(), zonedDateTime.getNano());
  }

  @Test
  public void test_build_with_now_OffsetDateTime() throws Exception {
    // Given
    OffsetDateTime offsetDateTime = OffsetDateTime.now();
    log.info("GIVEN - offsetDateTime={}, offset={}, seconds={}, nanos={}",
        offsetDateTime,
        offsetDateTime.getOffset().getTotalSeconds(), offsetDateTime.toEpochSecond(), offsetDateTime.getNano());

    // When
    OffsetDateTimeProto.OffsetDateTime message = this.generator.build(offsetDateTime);
    log.info("WHEN - message={}", message);

    // Then
    assertThat(message)
        .isNotNull()
        .extracting(OffsetDateTimeProto.OffsetDateTime::getOffset, OffsetDateTimeProto.OffsetDateTime::getSeconds,
            OffsetDateTimeProto.OffsetDateTime::getNanos)
        .containsSequence(offsetDateTime.getOffset().getTotalSeconds(), offsetDateTime.toEpochSecond(),
            offsetDateTime.getNano());
  }

  @Test
  public void test_build_with_now_OffsetTime() throws Exception {
    // Given
    OffsetTime offsetTime = OffsetTime.now();
    log.info("GIVEN - offsetTime={}, offset={}", offsetTime, offsetTime.getOffset());

    // When
    OffsetTimeProto.OffsetTime message = this.generator.build(offsetTime);
    log.info("WHEN - message={}", message);

    // Then
    assertThat(message)
        .isNotNull()
        .extracting(OffsetTimeProto.OffsetTime::getOffset,
            OffsetTimeProto.OffsetTime::getHour, OffsetTimeProto.OffsetTime::getMinute,
            OffsetTimeProto.OffsetTime::getSecond, OffsetTimeProto.OffsetTime::getNano)
        .containsSequence(offsetTime.getOffset().getTotalSeconds(),
            offsetTime.getHour(), offsetTime.getMinute(),
            offsetTime.getSecond(), offsetTime.getNano());
  }

  @Test
  public void test_build_with_Year() throws Exception {
    // Given
    Year year = Year.now();
    log.info("GIVEN - year={}", year);

    // Then
    assertThatThrownBy(() -> this.generator.build(year))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("unsupported temporal type")
        .hasMessageContaining(Year.class.getName());
  }
}