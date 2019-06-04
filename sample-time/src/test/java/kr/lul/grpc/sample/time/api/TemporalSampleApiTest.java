package kr.lul.grpc.sample.time.api;

import com.google.protobuf.Timestamp;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import kr.lul.grpc.message.time.*;
import kr.lul.grpc.sample.time.rpc.PingRequest;
import kr.lul.grpc.sample.time.rpc.PingResponse;
import kr.lul.grpc.sample.time.rpc.TemporalSampleServiceGrpc;
import kr.lul.grpc.util.time.TemporalMessageBuilder;
import kr.lul.grpc.util.time.TemporalMessageBuilderImpl;
import kr.lul.grpc.util.time.TemporalMessageParser;
import kr.lul.grpc.util.time.TemporalMessageParserImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;

import java.time.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author justburrow
 * @since 2019-05-28
 */
public class TemporalSampleApiTest {
  private static final Logger log = getLogger(TemporalSampleApiTest.class);

  @Rule
  public GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  private TemporalMessageBuilder builder = new TemporalMessageBuilderImpl();
  private TemporalMessageParser parser = new TemporalMessageParserImpl();

  private String serverName;
  private Server server;
  private TemporalSampleServiceGrpc.TemporalSampleServiceBlockingStub stub;

  @Before
  public void setUp() throws Exception {
    TemporalSampleApi temporalSampleApi = new TemporalSampleApi();
    temporalSampleApi.postConstruct();

    this.serverName = InProcessServerBuilder.generateName();
    this.server = InProcessServerBuilder.forName(this.serverName)
        .directExecutor()
        .addService(temporalSampleApi)
        .build()
        .start();
    this.grpcCleanupRule.register(this.server);

    this.stub = TemporalSampleServiceGrpc.newBlockingStub(
        InProcessChannelBuilder.forName(this.serverName).directExecutor().build());
    log.info("SETUP - stub={}", this.stub);
  }

  @After
  public void tearDown() throws Exception {
    this.server.shutdown();
    this.server.awaitTermination(5L, TimeUnit.SECONDS);
  }

  @Test
  public void test_ping_with_Instant() throws Exception {
    // Given
    Instant sentAt = Instant.now();
    TemporalProto.Temporal temporal = TemporalProto.Temporal.newBuilder()
        .setTimestamp((Timestamp) this.builder.build(sentAt))
        .build();
    log.info("GIVEN - sentAt={}", sentAt);

    PingRequest request = PingRequest.newBuilder()
        .setSentAt(temporal)
        .build();
    log.info("GIVEN - request={}", request);

    // When
    PingResponse response = this.stub.ping(request);
    log.info("WHEN - response={}", response);

    // Then
    assertThat(response)
        .isNotNull();
    assertThat(response.getPing())
        .isNotNull()
        .extracting(TemporalProto.Temporal::getTemporalCase)
        .isEqualTo(TemporalProto.Temporal.TemporalCase.TIMESTAMP);
    assertThat((Instant) this.parser.parse(response.getPing()))
        .isEqualTo(sentAt);
    assertThat((Instant) this.parser.parse(response.getReceivedAt()))
        .isAfter(sentAt);
  }

  @Test
  public void test_ping_with_ZonedDateTime() throws Exception {
    // Given
    ZonedDateTime sentAt = ZonedDateTime.now();
    log.info("GIVEN - sentAt={}", sentAt);

    PingRequest request = PingRequest.newBuilder()
        .setSentAt(TemporalProto.Temporal.newBuilder()
            .setZonedDateTime((ZonedDateTimeProto.ZonedDateTime) this.builder.build(sentAt))
            .build())
        .build();
    log.info("GIVEN - request={}", request);

    // When
    PingResponse response = this.stub.ping(request);
    log.info("WHEN - response={}", response);

    // Then
    assertThat(response)
        .isNotNull()
        .extracting(PingResponse::hasPing, PingResponse::hasReceivedAt)
        .containsSequence(true, true);
    assertThat((ZonedDateTime) this.parser.parse(response.getPing()))
        .isNotNull()
        .isEqualTo(sentAt);
    assertThat((ZonedDateTime) this.parser.parse(response.getReceivedAt()))
        .isAfter(sentAt);
  }

  @Test
  public void test_ping_with_OffsetTime() throws Exception {
    // Given
    OffsetTime sentAt = OffsetTime.now();
    log.info("GIVEN - sentAt={}", sentAt);

    PingRequest request = PingRequest.newBuilder()
        .setSentAt(TemporalProto.Temporal.newBuilder()
            .setOffsetTime((OffsetTimeProto.OffsetTime) this.builder.build(sentAt))
            .build())
        .build();
    log.info("GIVEN - request={}", request);

    // When
    PingResponse response = this.stub.ping(request);
    log.info("WHEN - response={}", response);

    // Then
    assertThat(response)
        .isNotNull()
        .extracting(PingResponse::hasPing, PingResponse::hasReceivedAt)
        .containsSequence(true, true);
    assertThat((OffsetTime) this.parser.parse(response.getPing()))
        .isEqualTo(sentAt);
    assertThat((OffsetTime) this.parser.parse(response.getReceivedAt()))
        .isAfter(sentAt);
  }

  @Test
  public void test_ping_with_LocalDate() throws Exception {
    // Given
    LocalDate sentAt = LocalDate.now();
    log.info("GIVEN - sentAt={}", sentAt);

    PingRequest request = PingRequest.newBuilder()
        .setSentAt(TemporalProto.Temporal.newBuilder()
            .setLocalDate((LocalDateProto.LocalDate) this.builder.build(sentAt))
            .build())
        .build();
    log.info("GIVEN - request={}", request);

    // When
    PingResponse response = this.stub.ping(request);
    log.info("WHEN - response={}", response);

    // Then
    assertThat(response)
        .isNotNull()
        .extracting(PingResponse::hasPing, PingResponse::hasReceivedAt)
        .containsSequence(true, true);
    assertThat((LocalDate) this.parser.parse(response.getPing()))
        .isEqualTo(sentAt);
    assertThat((LocalDate) this.parser.parse(response.getReceivedAt()))
        .isEqualTo(sentAt);
  }

  @Test
  public void test_ping_with_LocalDateTime() throws Exception {
    // Given
    LocalDateTime sentAt = LocalDateTime.now();
    log.info("GIVEN - sentAt={}", sentAt);

    PingRequest request = PingRequest.newBuilder()
        .setSentAt((TemporalProto.Temporal.newBuilder()
            .setLocalDateTime((LocalDateTimeProto.LocalDateTime) this.builder.build(sentAt))
            .build()))
        .build();
    log.info("GIVEN - request={}", request);

    // When
    PingResponse response = this.stub.ping(request);
    log.info("WHEN - response={}", response);

    // Then
    assertThat(response)
        .isNotNull()
        .extracting(PingResponse::hasPing, PingResponse::hasReceivedAt)
        .containsSequence(true, true);
    assertThat((LocalDateTime) this.parser.parse(response.getPing()))
        .isEqualTo(sentAt);
    assertThat((LocalDateTime) this.parser.parse(response.getReceivedAt()))
        .isAfter(sentAt);
  }

  @Test
  public void test_ping_with_LocalTime() throws Exception {
    // Given
    LocalTime sentAt = LocalTime.now();
    log.info("GIVEN - sentAt={}", sentAt);

    PingRequest request = PingRequest.newBuilder()
        .setSentAt(TemporalProto.Temporal.newBuilder()
            .setLocalTime((LocalTimeProto.LocalTime) this.builder.build(sentAt))
            .build())
        .build();
    log.info("GIVEN - request={}", request);

    // When
    PingResponse response = this.stub.ping(request);
    log.info("WHEN - response={}", response);

    // Then
    assertThat(response)
        .isNotNull()
        .extracting(PingResponse::hasPing, PingResponse::hasReceivedAt)
        .containsSequence(true, true);
    assertThat((LocalTime) this.parser.parse(response.getPing()))
        .isEqualTo(sentAt);
    assertThat((LocalTime) this.parser.parse(response.getReceivedAt()))
        .isAfter(sentAt);
  }
}