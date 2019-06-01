package kr.lul.grpc.sample.time.api;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import kr.lul.grpc.message.time.*;
import kr.lul.grpc.sample.time.PingRequest;
import kr.lul.grpc.sample.time.PingResponse;
import kr.lul.grpc.sample.time.PingServiceGrpc;
import kr.lul.grpc.util.time.TemporalMessageBuilder;
import kr.lul.grpc.util.time.TemporalMessageBuilderImpl;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import java.time.*;

import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author justburrow
 * @since 2019-05-28
 */
@GRpcService
public class PingServiceApi extends PingServiceGrpc.PingServiceImplBase {
  private static final Logger log = getLogger(PingServiceApi.class);

  private TemporalMessageBuilder builder;

  @PostConstruct
  private void postConstruct() {
    this.builder = new TemporalMessageBuilderImpl();

    log.info("init complete : {}", this);
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // kr.lul.grpc.sample.time.PingServiceGrpc.PingServiceImplBase
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
    if (log.isTraceEnabled()) {
      log.trace("args : request={}, responseObserver={}", request, responseObserver);
    }

    TemporalProto.Temporal sentAt = request.getSentAt();
    TemporalProto.Temporal receivedAt;
    switch (sentAt.getTemporalCase()) {
      case TIMESTAMP:
        receivedAt = TemporalProto.Temporal.newBuilder()
            .setTimestamp((Timestamp) this.builder.build(Instant.now()))
            .build();
        break;
      case ZONED_DATE_TIME:
        receivedAt = TemporalProto.Temporal.newBuilder()
            .setZonedDateTime((ZonedDateTimeProto.ZonedDateTime) this.builder.build(ZonedDateTime.now()))
            .build();
        break;
      case OFFSET_TIME:
        receivedAt = TemporalProto.Temporal.newBuilder()
            .setOffsetTime((OffsetTimeProto.OffsetTime) this.builder.build(OffsetTime.now()))
            .build();
        break;
      case OFFSET_DATE_TIME:
        receivedAt = TemporalProto.Temporal.newBuilder()
            .setOffsetDateTime((OffsetDateTimeProto.OffsetDateTime) this.builder.build(OffsetDateTime.now()))
            .build();
        break;
      case LOCAL_DATE:
        receivedAt = TemporalProto.Temporal.newBuilder()
            .setLocalDate((LocalDateProto.LocalDate) this.builder.build(LocalDate.now()))
            .build();
        break;
      case LOCAL_TIME:
        receivedAt = TemporalProto.Temporal.newBuilder()
            .setLocalTime((LocalTimeProto.LocalTime) this.builder.build(LocalTime.now()))
            .build();
        break;
      case LOCAL_DATE_TIME:
        receivedAt = TemporalProto.Temporal.newBuilder()
            .setLocalDateTime((LocalDateTimeProto.LocalDateTime) this.builder.build(LocalDateTime.now()))
            .build();
        break;
      default:
        String error = format("illegal temporal case : case=%s, temporal=%s", sentAt.getTemporalCase(), sentAt);
        log.error(error);
        throw new IllegalArgumentException(error);
    }

    PingResponse response = PingResponse.newBuilder()
        .setPing(sentAt)
        .setReceivedAt(receivedAt)
        .build();

    if (log.isTraceEnabled()) {
      log.trace("result : response={}", response);
    }
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}