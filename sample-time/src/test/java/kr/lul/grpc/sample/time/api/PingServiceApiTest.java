package kr.lul.grpc.sample.time.api;

import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import kr.lul.grpc.message.time.*;
import kr.lul.grpc.sample.time.PingRequest;
import kr.lul.grpc.sample.time.PingResponse;
import kr.lul.grpc.sample.time.PingServiceGrpc;
import kr.lul.grpc.sample.time.SampleTimeTestConfiguration;
import kr.lul.grpc.util.time.TemporalMessageBuilder;
import kr.lul.grpc.util.time.TemporalMessageBuilderImpl;
import kr.lul.grpc.util.time.TemporalMessageParser;
import kr.lul.grpc.util.time.TemporalMessageParserImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author justburrow
 * @since 2019-05-28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleTimeTestConfiguration.class)
public class PingServiceApiTest {
  private static final Logger log = getLogger(PingServiceApiTest.class);

  @Value("${grpc.inProcessServerName}")
  private String inProcessServerName;

  private ManagedChannel channel;
  private PingServiceGrpc.PingServiceBlockingStub stub;

  private TemporalMessageBuilder builder = new TemporalMessageBuilderImpl();
  private TemporalMessageParser parser = new TemporalMessageParserImpl();

  @Before
  public void setUp() throws Exception {
    this.channel = InProcessChannelBuilder.forName(this.inProcessServerName)
        .directExecutor()
        .build();
    log.info("SETUP - channel={}", this.channel);

    this.stub = PingServiceGrpc.newBlockingStub(this.channel);
    log.info("SETUP - stub={}", this.stub);
  }

  @After
  public void tearDown() throws Exception {
    this.channel.shutdown();
    this.channel.awaitTermination(5L, TimeUnit.SECONDS);
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