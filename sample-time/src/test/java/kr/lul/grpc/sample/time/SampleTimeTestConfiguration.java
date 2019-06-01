package kr.lul.grpc.sample.time;

import kr.lul.grpc.sample.time.api.PingServiceApi;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author justburrow
 * @since 2019-06-01
 */
@SpringBootApplication(scanBasePackageClasses = {PingServiceApi.class})
public class SampleTimeTestConfiguration {
}