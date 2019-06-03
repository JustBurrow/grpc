package kr.lul.grpc.sample.time.runner;

import kr.lul.grpc.sample.time.api.TemporalSampleApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author justburrow
 * @since 2019-05-28
 */
@SpringBootApplication(scanBasePackageClasses = TemporalSampleApi.class)
public class SampleTimeRunner {
  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(SampleTimeRunner.class);
    application.setWebApplicationType(WebApplicationType.NONE);
    application.run(args);
  }
}