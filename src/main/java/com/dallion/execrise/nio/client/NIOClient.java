package com.dallion.execrise.nio.client;

import org.apache.log4j.Logger;

public class NIOClient extends NIOClientHandler {

  private Logger logger = Logger.getLogger(getClass());

  public NIOClient(String host, int port) {
    super(host, port);
  }

}
