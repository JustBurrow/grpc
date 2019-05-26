package kr.lul.grpc.util.time;

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import kr.lul.grpc.message.time.*;
import kr.lul.grpc.util.common.MessageBuilder;

import java.time.*;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

/**
 * {@link java.time} 오브젝트에 해당하는 Proto Buffers 메시지를 생성한다.
 *
 * @author justburrow
 * @since 2019-05-25
 */
public interface TemporalMessageBuilder extends MessageBuilder {
  /**
   * 메시지를 생성할 수 있는 {@link java.time.temporal.Temporal} 타입.
   */
  Set<Class<? extends Temporal>> TEMPORAL_TYPES = unmodifiableSet(new HashSet<>(asList(
      Instant.class,
      ZonedDateTime.class, OffsetDateTime.class, OffsetTime.class,
      LocalDate.class, LocalDateTime.class, LocalTime.class
  )));

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // kr.lul.grpc.util.common.MessageGenerator
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  default Collection<Class> supportsSourceTypes() {
    return new HashSet<>(TEMPORAL_TYPES);
  }

  @Override
  @SuppressWarnings("unchecked")
  default <S, M extends Message> M build(S temporal) {
    if (null == temporal) {
      throw new NullPointerException("temporal is null.");
    } else if (!TEMPORAL_TYPES.contains(temporal.getClass())) {
      throw new IllegalArgumentException(format("unsupported temporal type : %s", temporal.getClass().getName()));
    }

    final M message;
    if (temporal instanceof Instant) {
      message = (M) Timestamp.newBuilder()
          .setSeconds(((Instant) temporal).getEpochSecond())
          .setNanos(((Instant) temporal).getNano())
          .build();
    } else if (temporal instanceof ZonedDateTime) {
      message = (M) ZonedDateTimeProto.ZonedDateTime.newBuilder()
          .setZoneId(((ZonedDateTime) temporal).getZone().getId())
          .setSeconds(((ZonedDateTime) temporal).toEpochSecond())
          .setNanos(((ZonedDateTime) temporal).getNano())
          .build();
    } else if (temporal instanceof OffsetDateTime) {
      message = (M) OffsetDateTimeProto.OffsetDateTime.newBuilder()
          .setOffset(((OffsetDateTime) temporal).getOffset().getTotalSeconds())
          .setSeconds(((OffsetDateTime) temporal).toEpochSecond())
          .setNanos(((OffsetDateTime) temporal).getNano())
          .build();
    } else if (temporal instanceof OffsetTime) {
      message = (M) OffsetTimeProto.OffsetTime.newBuilder()
          .setOffset(((OffsetTime) temporal).getOffset().getTotalSeconds())
          .setHour(((OffsetTime) temporal).getHour())
          .setMinute(((OffsetTime) temporal).getMinute())
          .setSecond(((OffsetTime) temporal).getSecond())
          .setNano(((OffsetTime) temporal).getNano())
          .build();
    } else if (temporal instanceof LocalDate) {
      message = (M) LocalDateProto.LocalDate.newBuilder()
          .setYear(((LocalDate) temporal).getYear())
          .setMonth(((LocalDate) temporal).getMonthValue())
          .setDate(((LocalDate) temporal).getDayOfMonth())
          .build();
    } else if (temporal instanceof LocalDateTime) {
      message = (M) LocalDateTimeProto.LocalDateTime.newBuilder()
          .setYear(((LocalDateTime) temporal).getYear())
          .setMonth(((LocalDateTime) temporal).getMonthValue())
          .setDate(((LocalDateTime) temporal).getDayOfMonth())
          .setHour(((LocalDateTime) temporal).getHour())
          .setMinute(((LocalDateTime) temporal).getMinute())
          .setSecond(((LocalDateTime) temporal).getSecond())
          .setNano(((LocalDateTime) temporal).getNano())
          .build();
    } else if (temporal instanceof LocalTime) {
      message = (M) LocalTimeProto.LocalTime.newBuilder()
          .setHour(((LocalTime) temporal).getHour())
          .setMinute(((LocalTime) temporal).getMinute())
          .setSecond(((LocalTime) temporal).getSecond())
          .setNano(((LocalTime) temporal).getNano())
          .build();
    } else {
      throw new IllegalStateException(format("illegal supports temporal type configuration : temporal=%s, supports=%s",
          temporal.getClass(), TEMPORAL_TYPES));
    }

    return message;
  }
}