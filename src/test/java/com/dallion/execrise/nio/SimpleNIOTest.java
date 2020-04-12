package com.dallion.execrise.nio;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import com.dallion.execrise.client.SimpleClientImpl;
import com.dallion.execrise.config.Config;
import com.dallion.execrise.nio.server.SimpleNIOServerImpl;

@TestInstance(Lifecycle.PER_CLASS)
class SimpleNIOTest {

  private AtomicLong counter = new AtomicLong();

  Logger logger = Logger.getLogger(getClass());

  SimpleNIOServerImpl server;

  @BeforeAll
  void initServer() {
    server = new SimpleNIOServerImpl();
    server.run();
  }

  @RepeatedTest(value = Integer.MAX_VALUE)
  @Disabled
  void test() {
    // NIOClient client = new NIOClient(Config.HOST, Config.PORT);
    // new Thread(client).start();

    long start = System.currentTimeMillis();

    SimpleClientImpl client = new SimpleClientImpl(Config.HOST, Config.PORT);

    assertNotNull(client);

    logger.info("Request Success : " + counter.incrementAndGet() + " "
        + (System.currentTimeMillis() - start));

  }

}
