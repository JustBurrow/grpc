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
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
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

  @Rule
  public GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private TemporalMessageBuilder builder = new TemporalMessageBuilderImpl();
  private TemporalAmountMessageParser parser = new TemporalAmountMessageParserImpl();

  private String serverName;
  private Server server;
  private TemporalAmountSampleServiceGrpc.TemporalAmountSampleServiceBlockingStub stub;

  @Before
  public void setUp() throws Exception {
    TemporalAmountSampleApi temporalAmountSampleApi = new TemporalAmountSampleApi();
    temporalAmountSampleApi.postConstruct();

    this.serverName = InProcessServerBuilder.generateName();
    this.server = InProcessServerBuilder.forName(this.serverName)
        .directExecutor()
        .addService(temporalAmountSampleApi)
        .build()
        .start();
    this.grpcCleanup.register(this.server);

    this.stub = TemporalAmountSampleServiceGrpc.newBlockingStub(
        this.grpcCleanup.register(InProcessChannelBuilder.forName(this.serverName).directExecutor().build()));
    log.info("SETUP - stub={}", this.stub);
  }

  @After
  public void tearDown() throws Exception {
    this.server.shutdown();
    this.server.awaitTermination();
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
    TemporalAmountResponse response = this.stub.calc(request);
    log.info("WHEN - response={}", response);

    // Then
    assertThat(response)
        .isNotNull()
        .extracting(TemporalAmountResponse::hasTemporalAmount)
        .isEqualTo(true);
  }
}