package com.dallion.execrise.threadpool.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class Task implements Runnable {

  private Logger logger = Logger.getLogger(getClass());

  private Socket socket;
  private String response;

  public Task(Socket socket, String response) {
    this.socket = socket;
    this.response = response;
  }

  @SuppressWarnings("deprecation")
  public void run() {
    try (InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

      logger.trace("Server receive Request and socket is" + (socket.isConnected() ? " " : " not ")
          + "connected");

      byte[] buffer = new byte[1 << 10];
      is.read(buffer);
      bos.write(buffer);
      os.write(response.getBytes());
      os.flush();

      socket.close();
    } catch (IOException e) {
      logger.warn(e.getLocalizedMessage(), e);
    } finally {
      IOUtils.closeQuietly(socket);
    }
  }

}
