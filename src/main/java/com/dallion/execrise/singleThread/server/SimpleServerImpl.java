package com.dallion.execrise.singleThread.server;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;

public class SimpleServerImpl implements Runnable {

  private Logger logger = Logger.getLogger(SimpleServerImpl.class);

  private volatile boolean isRunning = true;

  private int port;

  private String response;

  public SimpleServerImpl(int port) {
    this.port = port;
    this.response = "Hello World";
  }

  public SimpleServerImpl(int port, String response) {
    this(port);
    this.response = response;
  }

  @Override
  public void run() {
    try (ServerSocket server = new ServerSocket(port)) {
      logger.debug("Server Started at " + port);

      Socket s = null;
      while (isRunning) {
        s = server.accept();
        OutputStream os = s.getOutputStream();
        os.write(response.getBytes());
        os.flush();
        s.close();
      }

    } catch (Exception e) {
      logger.warn(e.getMessage());
    }
  }
}
