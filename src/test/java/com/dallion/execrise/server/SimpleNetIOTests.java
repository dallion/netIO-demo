package com.dallion.execrise.server;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.IOException;
import java.net.UnknownHostException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import com.dallion.execrise.client.SimpleClientImpl;
import com.dallion.execrise.config.Config;
import com.dallion.execrise.singleThread.server.SimpleServerImpl;

@DisplayName("Simple Server And Client Test")
@TestInstance(Lifecycle.PER_CLASS)
public class SimpleNetIOTests {

  private SimpleServerImpl server = null;

  private SimpleClientImpl client = null;

  private String testResponse = "ABC";

  @BeforeAll
  void createServer() throws UnknownHostException, IOException {
    server = new SimpleServerImpl(Config.PORT, testResponse);
    assertNotNull(server);
  }

  @BeforeAll
  void createClient() throws UnknownHostException, IOException {
    client = new SimpleClientImpl(Config.HOST, Config.PORT);
    assertNotNull(client);
  }

  @Test
  @Disabled
  void test() throws IOException {
    new Thread(server).start();

    assertNotNull(testResponse);
    assertNotNull(client);

  }

}
