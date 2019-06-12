package kr.lul.grpc.sample.time.api;

import com.google.protobuf.Timestamp;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import kr.lul.grpc.message.time.*;
import kr.lul.grpc.sample.time.rpc.TemporalAmountResponse;
import kr.lul.grpc.sample.time.rpc.TemporalAmountSampleServiceGrpc;
import kr.lul.grpc.sample.time.rpc.TemporalPairRequest;
import kr.lul.grpc.util.time.TemporalAmountMessageParser;
import kr.lul.grpc.util.time.TemporalAmountMessageParserImpl;
import kr.lul.grpc.util.time.TemporalMessageBuilder;
import kr.lul.grpc.util.time.TemporalMessageBuilderImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author justburrow
 * @since 2019-06-03
 */
public class TemporalAmountSampleApiTest {
  private static final Logger log = getLogger(TemporalAmountSampleApiTest.class);

  private static final long MILLIS_BOUND = 1000L * 60 * 60 * 24 * 30 * 2;

  @ClassRule
  public static GrpcCleanupRule GRPC_CLEANUP = new GrpcCleanupRule();

  private static Server SERVER;
  private static TemporalAmountSampleServiceGrpc.TemporalAmountSampleServiceBlockingStub STUB;

  private TemporalMessageBuilder builder = new TemporalMessageBuilderImpl();
  private TemporalAmountMessageParser parser = new TemporalAmountMessageParserImpl();

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    TemporalAmountSampleApi temporalAmountSampleApi = new TemporalAmountSampleApi();
    temporalAmountSampleApi.postConstruct();

    String serverName = InProcessServerBuilder.generateName();
    SERVER = InProcessServerBuilder.forName(serverName)
        .directExecutor()
        .addService(temporalAmountSampleApi)
        .build()
        .start();
    GRPC_CLEANUP.register(SERVER);

    STUB = TemporalAmountSampleServiceGrpc.newBlockingStub(
        GRPC_CLEANUP.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
    log.info("SETUP CLASS - STUB={}", STUB);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    SERVER.shutdown();
    SERVER.awaitTermination();
  }

  private void assertResponse(TemporalAmountResponse response, TemporalAmount... expected) {
    log.debug("THEN : response={}, expected={}", response, expected);

    assertThat(response)
        .isNotNull()
        .extracting(TemporalAmountResponse::hasTemporalAmount)
        .isEqualTo(true);
    assertThat((TemporalAmount) this.parser.parse(response.getTemporalAmount()))
        .isIn(expected);
  }

  @Test
  public void test_calc_with_Instant_and_Instant() throws Exception {
    // Given
    Instant t1 = Instant.now();
    Instant t2 = Instant.ofEpochMilli(System.currentTimeMillis() + current().nextLong(1L, MILLIS_BOUND));
    log.info("GIVEN - t1={}, t2={}", t1, t2);

    TemporalPairRequest request = TemporalPairRequest.newBuilder()
        .setTemporal1(TemporalProto.Temporal.newBuilder()
            .setTimestamp((Timestamp) this.builder.build(t1))
            .build())
        .setTemporal2(TemporalProto.Temporal.newBuilder()
            .setTimestamp((Timestamp) this.builder.build(t2))
            .build())
        .build();
    log.info("GIVEN - request={}", request);

    // When
    TemporalAmountResponse response = this.STUB.calc(request);
    log.info("WHEN - response={}", response);

    // Then
    assertResponse(response, Period.between(ZonedDateTime.ofInstant(t1, ZoneId.systemDefault()).toLocalDate(),
        ZonedDateTime.ofInstant(t2, ZoneId.systemDefault()).toLocalDate()),
        Duration.between(t1, t2));
  }

  @Test
  public void test_calc_with_ZonedDateTime_and_ZonedDateTime() throws Exception {
    // Given
    ZonedDateTime t1 = ZonedDateTime.now();
    ZonedDateTime t2 = t1.plusSeconds(1L + current().nextLong(MILLIS_BOUND / 1000L));
    log.info("GIVEN - t1={}, t2={}", t1, t2);

    TemporalPairRequest request = TemporalPairRequest.newBuilder()
        .setTemporal1(TemporalProto.Temporal.newBuilder()
            .setZonedDateTime((ZonedDateTimeProto.ZonedDateTime) this.builder.build(t1))
            .build())
        .setTemporal2(TemporalProto.Temporal.newBuilder()
            .setZonedDateTime((ZonedDateTimeProto.ZonedDateTime) this.builder.build(t2))
            .build())
        .build();
    log.info("GIVEN - request={}", request);

    // When
    TemporalAmountResponse response = STUB.calc(request);
    log.info("WHEN - response={}", response);

    // Then
    assertResponse(response, Period.between(t1.toLocalDate(), t2.toLocalDate()),
        Duration.between(t1, t2));
  }

  @Test
  public void test_calc_with_OffsetDateTime_and_OffsetDateTime() throws Exception {
    // Given
    final OffsetDateTime t1 = OffsetDateTime.from(ZonedDateTime.now());
    final OffsetDateTime t2 = OffsetDateTime.ofInstant(
        Instant.ofEpochMilli(System.currentTimeMillis() + current().nextLong(1L, MILLIS_BOUND)),
        ZoneId.systemDefault());
    log.info("GIVEN - t1={}, t2={}", t1, t2);

    TemporalPairRequest request = TemporalPairRequest.newBuilder()
        .setTemporal1(TemporalProto.Temporal.newBuilder()
            .setOffsetDateTime((OffsetDateTimeProto.OffsetDateTime) this.builder.build(t1))
            .build())
        .setTemporal2(TemporalProto.Temporal.newBuilder()
            .setOffsetDateTime((OffsetDateTimeProto.OffsetDateTime) this.builder.build(t2))
            .build())
        .build();

    // When
    TemporalAmountResponse response = STUB.calc(request);
    log.info("WHEN - response={}", response);

    // Then
    assertResponse(response,
        Period.between(t1.toLocalDate(), t2.toLocalDate()),
        Duration.between(t1, t2));
  }

  @Test
  public void test_calc_with_OffsetTime_and_OffsetTime() throws Exception {
    // Given
    OffsetTime t1 = OffsetTime.now();
    OffsetTime t2 = t1.plusSeconds(current().nextLong(1L, MILLIS_BOUND / 1000L));
    log.info("GIVEN - t1={}, t2={}", t1, t2);

    TemporalPairRequest request = TemporalPairRequest.newBuilder()
        .setTemporal1(TemporalProto.Temporal.newBuilder()
            .setOffsetTime((OffsetTimeProto.OffsetTime) this.builder.build(t1))
            .build())
        .setTemporal2(TemporalProto.Temporal.newBuilder()
            .setOffsetTime((OffsetTimeProto.OffsetTime) this.builder.build(t2))
            .build())
        .build();
    log.info("GIVEN - request={}", request);

    // When
    TemporalAmountResponse response = STUB.calc(request);
    log.info("WHEN - response={}", response);

    // Then
    assertResponse(response,
        Duration.between(t1, t2));
  }

  @Test
  public void test_calc_with_LocalDate_and_LocalDate() throws Exception {
    // Given
    LocalDate t1 = LocalDate.now();
    LocalDate t2 = Instant.ofEpochMilli(System.currentTimeMillis() + current().nextLong(MILLIS_BOUND))
        .atZone(ZoneId.systemDefault()).toLocalDate();
    log.info("GIVEN - t1={}, t2={}", t1, t2);


    TemporalPairRequest request = TemporalPairRequest.newBuilder()
        .setTemporal1(TemporalProto.Temporal.newBuilder()
            .setLocalDate((LocalDateProto.LocalDate) this.builder.build(t1))
            .build())
        .setTemporal2(TemporalProto.Temporal.newBuilder()
            .setLocalDate((LocalDateProto.LocalDate) this.builder.build(t2))
            .build())
        .build();
    log.info("GIVEN - request={}", request);

    // When
    TemporalAmountResponse response = STUB.calc(request);
    log.info("WHEN - response={}", response);

    // Then
    assertResponse(response, Period.between(t1, t2));
  }

  @Test
  public void test_calc_with_LocalDateTime_and_LocalDateTime() throws Exception {
    // Given
    LocalDateTime t1 = LocalDateTime.now();
    LocalDateTime t2 = t1.plus(current().nextLong(1, MILLIS_BOUND), ChronoUnit.MILLIS);
    log.info("GIVEN - t1={}, t2={}", t1, t2);

    TemporalPairRequest request = TemporalPairRequest.newBuilder()
        .setTemporal1(TemporalProto.Temporal.newBuilder()
            .setLocalDateTime((LocalDateTimeProto.LocalDateTime) this.builder.build(t1))
            .build())
        .setTemporal2(TemporalProto.Temporal.newBuilder()
            .setLocalDateTime((LocalDateTimeProto.LocalDateTime) this.builder.build(t2))
            .build())
        .build();
    log.info("GIVEN - request={}", request);

    // When
    TemporalAmountResponse response = STUB.calc(request);
    log.info("WHEN - response={}", response);

    // Then
    assertResponse(response,
        Duration.between(t1, t2),
        Period.between(t1.toLocalDate(), t2.toLocalDate()));
  }

  @Test
  public void test_calc_with_LocalTime_and_LocalTime() throws Exception {
    // Given
    LocalTime t1 = LocalTime.now();
    LocalTime t2 = t1.plusSeconds(current().nextLong(1, 1000L * 60 * 60 * 24));
    log.info("GIVEN - t1={}, t2={}", t1, t2);

    TemporalPairRequest request = TemporalPairRequest.newBuilder()
        .setTemporal1(TemporalProto.Temporal.newBuilder()
            .setLocalTime((LocalTimeProto.LocalTime) this.builder.build(t1))
            .build())
        .setTemporal2(TemporalProto.Temporal.newBuilder()
            .setLocalTime((LocalTimeProto.LocalTime) this.builder.build(t2))
            .build())
        .build();
    log.info("GIVEN - request={}", request);

    // When
    TemporalAmountResponse response = STUB.calc(request);
    log.info("WHEN - response={}", response);

    // Then
    assertResponse(response, Duration.between(t1, t2));
  }
}