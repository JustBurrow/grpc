package kr.lul.grpc.util.time;

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import kr.lul.grpc.message.time.*;
import kr.lul.grpc.util.common.MessageParser;

import java.time.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

/**
 * @author justburrow
 * @since 2019-05-26
 */
public interface TemporalMessageParser extends MessageParser {
  Set<Class<? extends Message>> TEMPORAL_TYPES = unmodifiableSet(new HashSet<>(asList(
      Timestamp.class,
      ZonedDateTimeProto.ZonedDateTime.class, OffsetDateTimeProto.OffsetDateTime.class,
      OffsetTimeProto.OffsetTime.class,
      LocalDateProto.LocalDate.class, LocalDateTimeProto.LocalDateTime.class, LocalTimeProto.LocalTime.class,

      TemporalProto.Temporal.class
  )));

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // kr.lul.grpc.util.common.MessageParser
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  default Collection<Class<? extends Message>> supportsMessageTypes() {
    return TEMPORAL_TYPES;
  }

  @SuppressWarnings("unchecked")
  @Override
  default <T, M extends Message> T parse(M message) {
    if (null == message) {
      throw new NullPointerException("message is null.");
    } else if (!TEMPORAL_TYPES.contains(message.getClass())) {
      throw new IllegalArgumentException(format("unsupported temporal message type : %s", message.getClass()));
    }

    T temporal;
    if (message instanceof Timestamp) {
      temporal = (T) Instant.ofEpochSecond(
          ((Timestamp) message).getSeconds(),
          ((Timestamp) message).getNanos());
    } else if (message instanceof ZonedDateTimeProto.ZonedDateTime) {
      temporal = (T) ZonedDateTime.ofInstant(
          Instant.ofEpochSecond(
              ((ZonedDateTimeProto.ZonedDateTime) message).getSeconds(),
              ((ZonedDateTimeProto.ZonedDateTime) message).getNanos()),
          ZoneId.of(((ZonedDateTimeProto.ZonedDateTime) message).getZoneId()));
    } else if (message instanceof OffsetDateTimeProto.OffsetDateTime) {
      temporal = (T) OffsetDateTime.ofInstant(
          Instant.ofEpochSecond(
              ((OffsetDateTimeProto.OffsetDateTime) message).getSeconds(),
              ((OffsetDateTimeProto.OffsetDateTime) message).getNanos()),
          ZoneOffset.ofTotalSeconds(((OffsetDateTimeProto.OffsetDateTime) message).getOffset()));
    } else if (message instanceof OffsetTimeProto.OffsetTime) {
      temporal = (T) OffsetTime.of(
          ((OffsetTimeProto.OffsetTime) message).getHour(),
          ((OffsetTimeProto.OffsetTime) message).getMinute(),
          ((OffsetTimeProto.OffsetTime) message).getSecond(),
          ((OffsetTimeProto.OffsetTime) message).getNano(),
          ZoneOffset.ofTotalSeconds(((OffsetTimeProto.OffsetTime) message).getOffset()));
    } else if (message instanceof LocalDateProto.LocalDate) {
      temporal = (T) LocalDate.of(
          ((LocalDateProto.LocalDate) message).getYear(),
          ((LocalDateProto.LocalDate) message).getMonth(),
          ((LocalDateProto.LocalDate) message).getDate());
    } else if (message instanceof LocalDateTimeProto.LocalDateTime) {
      temporal = (T) LocalDateTime.of(
          ((LocalDateTimeProto.LocalDateTime) message).getYear(),
          ((LocalDateTimeProto.LocalDateTime) message).getMonth(),
          ((LocalDateTimeProto.LocalDateTime) message).getDate(),
          ((LocalDateTimeProto.LocalDateTime) message).getHour(),
          ((LocalDateTimeProto.LocalDateTime) message).getMinute(),
          ((LocalDateTimeProto.LocalDateTime) message).getSecond(),
          ((LocalDateTimeProto.LocalDateTime) message).getNano());
    } else if (message instanceof LocalTimeProto.LocalTime) {
      temporal = (T) LocalTime.of(((LocalTimeProto.LocalTime) message).getHour(),
          ((LocalTimeProto.LocalTime) message).getMinute(),
          ((LocalTimeProto.LocalTime) message).getSecond(),
          ((LocalTimeProto.LocalTime) message).getNano());
    } else if (message instanceof TemporalProto.Temporal) {
      switch (((TemporalProto.Temporal) message).getTemporalCase()) {
        case TIMESTAMP:
          temporal = parse(((TemporalProto.Temporal) message).getTimestamp());
          break;
        case ZONED_DATE_TIME:
          temporal = parse(((TemporalProto.Temporal) message).getZonedDateTime());
          break;
        case OFFSET_DATE_TIME:
          temporal = parse(((TemporalProto.Temporal) message).getOffsetDateTime());
          break;
        case OFFSET_TIME:
          temporal = parse(((TemporalProto.Temporal) message).getOffsetTime());
          break;
        case LOCAL_DATE:
          temporal = parse(((TemporalProto.Temporal) message).getLocalDate());
          break;
        case LOCAL_DATE_TIME:
          temporal = parse(((TemporalProto.Temporal) message).getLocalDateTime());
          break;
        case LOCAL_TIME:
          temporal = parse(((TemporalProto.Temporal) message).getLocalTime());
          break;
        default:
          throw new IllegalArgumentException(format("unsupported temporal case : temporal=%s", message));
      }
    } else {
      throw new IllegalStateException(
          format("illegal supports temporal message type configuration : message=%s, supports=%s",
              message.getClass(), TEMPORAL_TYPES));
    }

    return temporal;
  }
}