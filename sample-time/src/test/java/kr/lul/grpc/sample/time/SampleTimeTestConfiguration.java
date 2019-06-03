package kr.lul.grpc.sample.time;

import kr.lul.grpc.sample.time.api.TemporalSampleApi;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author justburrow
 * @since 2019-06-01
 */
@SpringBootApplication(scanBasePackageClasses = {TemporalSampleApi.class})
public class SampleTimeTestConfiguration {
}