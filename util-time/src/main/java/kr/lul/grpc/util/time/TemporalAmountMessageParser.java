package kr.lul.grpc.util.time;

import com.google.protobuf.Duration;
import com.google.protobuf.Message;
import kr.lul.grpc.message.time.PeriodProto;
import kr.lul.grpc.message.time.TemporalAmountProto;
import kr.lul.grpc.util.common.MessageParser;

import java.time.Period;
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
public interface TemporalAmountMessageParser extends MessageParser {
  Set<Class<? extends Message>> TEMPORAL_AMOUNT_MESSAGE_TYPES = unmodifiableSet(new HashSet<>(asList(
      Duration.class, PeriodProto.Period.class,
      TemporalAmountProto.TemporalAmount.class)));

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // kr.lul.grpc.util.common.MessageParser
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  default Collection<Class<? extends Message>> supportsMessageTypes() {
    return new HashSet<>(TEMPORAL_AMOUNT_MESSAGE_TYPES);
  }

  @SuppressWarnings("unchecked")
  @Override
  default <T, M extends Message> T parse(M message) {
    if (null == message) {
      throw new NullPointerException("message is null.");
    } else if (!TEMPORAL_AMOUNT_MESSAGE_TYPES.contains(message.getClass())) {
      throw new IllegalArgumentException(format("unsupported temporal amount message type : %s", message.getClass()));
    }

    T amount;
    if (message instanceof Duration) {
      amount = (T) java.time.Duration.ofSeconds(((Duration) message).getSeconds(),
          ((Duration) message).getNanos());
    } else if (message instanceof PeriodProto.Period) {
      amount = (T) Period.of(((PeriodProto.Period) message).getYears(),
          ((PeriodProto.Period) message).getMonths(),
          ((PeriodProto.Period) message).getDays());
    } else if (message instanceof TemporalAmountProto.TemporalAmount) {
      switch (((TemporalAmountProto.TemporalAmount) message).getAmountCase()) {
        case PERIOD:
          amount = parse(((TemporalAmountProto.TemporalAmount) message).getPeriod());
          break;
        case DURATION:
          amount = parse(((TemporalAmountProto.TemporalAmount) message).getDuration());
          break;
        default:
          throw new IllegalArgumentException(format("illegal message type : message.case=%s, message=%s",
              ((TemporalAmountProto.TemporalAmount) message).getAmountCase(), message));
      }
    } else {
      throw new IllegalStateException(format("illegal supports temporal amount message type configuration : %s",
          TEMPORAL_AMOUNT_MESSAGE_TYPES));
    }

    return amount;
  }
}