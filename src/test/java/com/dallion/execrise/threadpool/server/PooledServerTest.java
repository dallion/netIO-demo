package com.dallion.execrise.threadpool.server;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import com.dallion.execrise.client.SimpleClientImpl;
import com.dallion.execrise.config.Config;


@TestInstance(Lifecycle.PER_CLASS)
public class PooledServerTest {

  private Logger logger = Logger.getLogger(getClass());

  private PooledServerImpl server = null;

  private AtomicLong counter = new AtomicLong();

  @BeforeAll
  void createServer() throws UnknownHostException, IOException {
    server = new PooledServerImpl(Config.PORT, Config.RESPONSE);
    assertNotNull(server);
    new Thread(server).start();
  }

  @RepeatedTest(value = Integer.MAX_VALUE)
  void test() throws IOException {
    long start = System.currentTimeMillis();

    SimpleClientImpl client = new SimpleClientImpl(Config.HOST, Config.PORT);

    assertNotNull(client);

    new Thread(client).start();

    logger.info("Request Success : " + counter.incrementAndGet() + " "
        + (System.currentTimeMillis() - start));

  }

}
