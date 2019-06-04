package kr.lul.grpc.sample.time.api;

import com.google.protobuf.Timestamp;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import kr.lul.grpc.message.time.TemporalProto;
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

import java.time.Instant;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author justburrow
 * @since 2019-06-03
 */
public class TemporalAmountSampleApiTest {
  private static final Logger log = getLogger(TemporalAmountSampleApiTest.class);

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

  @Test
  public void test_calc_with_Instant_and_Instant() throws Exception {
    // Given
    Instant now = Instant.now();
    Instant then = Instant.ofEpochMilli(System.currentTimeMillis() + current().nextLong(1L, 1_000_000L));
    log.info("GIVEN - now={}, then={}", now, then);

    TemporalPairRequest request = TemporalPairRequest.newBuilder()
        .setTemporal1(TemporalProto.Temporal.newBuilder()
            .setTimestamp((Timestamp) this.builder.build(now))
            .build())
        .setTemporal2(TemporalProto.Temporal.newBuilder()
            .setTimestamp((Timestamp) this.builder.build(then))
            .build())
        .build();
    log.info("GIVEN - request={}", request);

    // When
    TemporalAmountResponse response = this.STUB.calc(request);
    log.info("WHEN - response={}", response);

    // Then
    assertThat(response)
        .isNotNull()
        .extracting(TemporalAmountResponse::hasTemporalAmount)
        .isEqualTo(true);
  }
}