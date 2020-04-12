package com.dallion.execrise.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.log4j.Logger;

public class SimpleClientImpl implements Runnable {

  private Logger logger = Logger.getLogger(SimpleClientImpl.class);

  private String host;

  private int port;

  public SimpleClientImpl(String host, int port) {
    this.host = host;
    this.port = port;
  }

  @Override
  public void run() {
    byte[] buffer = new byte[2 << 4];

    byte[] begin = String.valueOf(System.currentTimeMillis()).getBytes();

    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Socket client = new Socket(host, port);
        InputStream is = client.getInputStream();
        OutputStream os = client.getOutputStream();) {

      os.write(begin);
      os.flush();
      logger.trace("Client Request Sent");

      int length;
      while ((length = is.read(buffer)) > 0) {
        bos.write(buffer, 0, length);
      }

      logger.trace("Server Response Receive");

      logger.debug(new String(bos.toByteArray()));
    } catch (Exception e) {
      logger.warn(e.getLocalizedMessage(), e);
      e.printStackTrace();
      System.exit(2);
    }

  }
}
