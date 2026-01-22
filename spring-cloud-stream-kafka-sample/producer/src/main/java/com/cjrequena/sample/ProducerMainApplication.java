package com.cjrequena.sample;

import com.cjrequena.sample.service.ProducerService1;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerMainApplication implements CommandLineRunner {

  private ProducerService1 producerService1;

  public static void main(String[] args) {
    SpringApplication.run(ProducerMainApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
  }
}
