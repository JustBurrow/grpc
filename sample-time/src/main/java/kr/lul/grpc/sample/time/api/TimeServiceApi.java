package kr.lul.grpc.sample.time.api;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import kr.lul.grpc.message.time.TemporalProto;
import kr.lul.grpc.sample.time.TimeServiceGrpc;
import kr.lul.grpc.util.time.TemporalMessageBuilder;
import kr.lul.grpc.util.time.TemporalMessageBuilderImpl;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import java.time.Instant;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author justburrow
 * @since 2019-05-28
 */
@GRpcService
public class TimeServiceApi extends TimeServiceGrpc.TimeServiceImplBase {
  private static final Logger log = getLogger(TimeServiceApi.class);

  private TemporalMessageBuilder builder;

  @PostConstruct
  private void postConstruct() {
    this.builder = new TemporalMessageBuilderImpl();

    log.info("init complete : {}", this);
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // kr.lul.grpc.sample.time.TimeServiceGrpc.TimeServiceImplBase
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public void ping(TemporalProto.Temporal request, StreamObserver<TemporalProto.Temporal> response) {
    if (log.isTraceEnabled()) {
      log.trace("args : request={}, response={}", request, response);
    }

    Instant instant = Instant.now();
    Timestamp timestamp = this.builder.build(instant);
    TemporalProto.Temporal message = TemporalProto.Temporal.newBuilder()
        .setTimestamp(timestamp)
        .build();


    if (log.isTraceEnabled()) {
      log.trace("instant={}, timestamp={}, message={}", instant, timestamp, message);
    }
    response.onNext(message);
    response.onCompleted();
  }
}