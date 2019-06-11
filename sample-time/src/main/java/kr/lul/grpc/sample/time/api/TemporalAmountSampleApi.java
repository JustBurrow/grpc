package kr.lul.grpc.sample.time.api;

import io.grpc.stub.StreamObserver;
import kr.lul.grpc.message.time.PeriodProto;
import kr.lul.grpc.message.time.TemporalAmountProto;
import kr.lul.grpc.sample.time.rpc.TemporalAmountResponse;
import kr.lul.grpc.sample.time.rpc.TemporalAmountSampleServiceGrpc;
import kr.lul.grpc.sample.time.rpc.TemporalPairRequest;
import kr.lul.grpc.util.time.TemporalAmountMessageBuilder;
import kr.lul.grpc.util.time.TemporalAmountMessageBuilderImpl;
import kr.lul.grpc.util.time.TemporalMessageParser;
import kr.lul.grpc.util.time.TemporalMessageParserImpl;
import org.slf4j.Logger;

import java.time.*;
import java.time.temporal.Temporal;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author justburrow
 * @since 2019-06-03
 */
public class TemporalAmountSampleApi extends TemporalAmountSampleServiceGrpc.TemporalAmountSampleServiceImplBase {
  private static final Logger log = getLogger(TemporalAmountSampleApi.class);

  private TemporalMessageParser parser;
  private TemporalAmountMessageBuilder builder;

  public void postConstruct() {
    this.parser = new TemporalMessageParserImpl();
    this.builder = new TemporalAmountMessageBuilderImpl();

    log.info("{} init compolete.", this);
  }

  private LocalDate toLocalDate(Temporal temporal) {
    LocalDate localDate;

    if (temporal instanceof Instant) {
      localDate = ((Instant) temporal).atZone(ZoneId.systemDefault()).toLocalDate();
    } else if (temporal instanceof ZonedDateTime) {
      localDate = ((ZonedDateTime) temporal).toLocalDate();
    } else if (temporal instanceof OffsetDateTime) {
      localDate = ((OffsetDateTime) temporal).toLocalDate();
    } else if (temporal instanceof LocalDate) {
      localDate = (LocalDate) temporal;
    } else if (temporal instanceof LocalDateTime) {
      localDate = ((LocalDateTime) temporal).toLocalDate();
    } else {
      localDate = LocalDate.now();
    }

    return localDate;
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // kr.lul.grpc.sample.time.rpc.TemporalAmountSampleServiceGrpc.TemporalAmountSampleServiceImplBase
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public void calc(TemporalPairRequest request, StreamObserver<TemporalAmountResponse> responseObserver) {
    if (log.isTraceEnabled()) {
      log.trace("args : request={}, responseObserver={}", request, responseObserver);
    }

    Temporal temporal1 = this.parser.parse(request.getTemporal1());
    Temporal temporal2 = this.parser.parse(request.getTemporal2());
    if (log.isTraceEnabled()) {
      log.trace("temporal1={}, temporal2={}", temporal1, temporal2);
    }

    Duration duration;
    try {
      duration = Duration.between(temporal1, temporal2);
    } catch (Exception e) {
      log.warn(e.getMessage(), e);
      duration = null;
    }
    Period period = Period.between(toLocalDate(temporal1), toLocalDate(temporal2));
    if (log.isTraceEnabled()) {
      log.trace("duration={}, period={}", duration, period);
    }

    TemporalAmountResponse.Builder responseBuilder = TemporalAmountResponse.newBuilder();
    if (null == duration || 0 < period.getYears() || 0 < period.getMonths() || 0 < period.getDays()) {
      responseBuilder.setTemporalAmount(TemporalAmountProto.TemporalAmount.newBuilder()
          .setPeriod((PeriodProto.Period) this.builder.build(period))
          .build());
    } else {
      responseBuilder.setTemporalAmount(TemporalAmountProto.TemporalAmount.newBuilder()
          .setDuration((com.google.protobuf.Duration) this.builder.build(duration))
          .build());
    }

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
  }
}