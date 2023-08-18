package com.cjrequena.sample;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 */
@Log4j2
@SpringBootApplication
public class ConsumerMainApplication implements CommandLineRunner {


  private static Class<ConsumerMainApplication> mainApplicationClass = ConsumerMainApplication.class;

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    try {
      SpringApplication springApplication = new SpringApplication(mainApplicationClass);
      springApplication.setBannerMode(Banner.Mode.OFF);
      springApplication.run(args);
    } catch (Exception ex) {
      log.error("Error: " + ex);
      throw ex;
    }
  }

  @Override
  public void run(String... strings) throws InterruptedException {
  }
}

