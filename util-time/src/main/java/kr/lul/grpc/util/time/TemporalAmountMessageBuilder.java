package kr.lul.grpc.util.time;

import com.google.protobuf.Message;
import kr.lul.grpc.message.time.PeriodProto;
import kr.lul.grpc.util.common.MessageBuilder;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;
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
public interface TemporalAmountMessageBuilder extends MessageBuilder {
  Set<Class<? extends TemporalAmount>> TEMPORAL_AMOUNT_TYPES = unmodifiableSet(new HashSet<>(asList(
      Duration.class, Period.class)));

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // kr.lul.grpc.util.common.MessageBuilder
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  default Collection<Class> supportsSourceTypes() {
    return new HashSet<>(TEMPORAL_AMOUNT_TYPES);
  }

  @SuppressWarnings("unchecked")
  @Override
  default <S, M extends Message> M build(S temporalAmount) {
    if (null == temporalAmount) {
      throw new NullPointerException("temporalAmount is null.");
    } else if (!TEMPORAL_AMOUNT_TYPES.contains(temporalAmount.getClass())) {
      throw new IllegalArgumentException(format("unsupported temporalAmount type : temporalAmount=%s, supports=%s",
          temporalAmount.getClass(), TEMPORAL_AMOUNT_TYPES));
    }

    M message;
    if (temporalAmount instanceof Duration) {
      message = (M) com.google.protobuf.Duration.newBuilder()
          .setSeconds(((Duration) temporalAmount).getSeconds())
          .setNanos(((Duration) temporalAmount).getNano())
          .build();
    } else if (temporalAmount instanceof Period) {
      message = (M) PeriodProto.Period.newBuilder()
          .setYears(((Period) temporalAmount).getYears())
          .setMonths(((Period) temporalAmount).getMonths())
          .setDays(((Period) temporalAmount).getDays())
          .build();
    } else {
      throw new IllegalStateException(
          format("illegal support temporalAmount type configuration : temporalAmount=%s, supports=%s",
              temporalAmount.getClass(), TEMPORAL_AMOUNT_TYPES));
    }

    return message;
  }
}