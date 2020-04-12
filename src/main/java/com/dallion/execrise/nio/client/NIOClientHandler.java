package com.dallion.execrise.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class NIOClientHandler implements Runnable {

  private Logger logger = Logger.getLogger(getClass());

  private static final AtomicLong counter = new AtomicLong();

  private String host;
  private int port;
  private Selector selector;
  private SocketChannel channel;
  private volatile boolean isRunning;

  public NIOClientHandler(String host, int port) {
    this.host = host;
    this.port = port;
    try {
      selector = Selector.open();
      channel = SocketChannel.open();
      channel.configureBlocking(false);
      logger.debug("Start client : " + counter.incrementAndGet());
    } catch (IOException e) {
      logger.warn(e.getLocalizedMessage(), e);
      System.exit(0);
    }
  }

  @Override
  public void run() {
    try {
      doConnect();
    } catch (IOException e) {
      logger.warn(e.getLocalizedMessage(), e);
    }

    while (isRunning) {
      try {
        selector.select();
        Set<SelectionKey> keys = selector.selectedKeys();
        Iterator<SelectionKey> it = keys.iterator();
        SelectionKey key = null;

        while (it.hasNext()) {
          key = it.next();
          it.remove();
          try {
            handleInput(key);
          } catch (Exception e) {
            logger.warn(e.getLocalizedMessage(), e);
          }

          // 操作结束后销毁对应的资源
          if (key != null) {
            key.cancel();
            if (key.channel() != null) {
              key.channel().close();
            }
          }
        }
      } catch (IOException e) {
        logger.warn(e.getLocalizedMessage(), e);
      }
    }

    IOUtils.closeQuietly(selector);
  }


  public void handleInput(SelectionKey key) throws IOException {
    if (!key.isValid()) {
      return;
    }

    SocketChannel sc = (SocketChannel) key.channel();

    if (key.isConnectable()) {
      if (sc.finishConnect()) {
        sc.register(selector, SelectionKey.OP_READ);
        doWrite();
      } else {
        logger.warn("Failed Connected to server");
      }
    }

    if (key.isReadable()) {
      ByteBuffer buffer = ByteBuffer.allocate(1 << 10);

      try {
        int readBytes = sc.read(buffer);
        if (readBytes > 0) {
          buffer.flip();
          byte[] bytes = new byte[buffer.remaining()];
          buffer.get(bytes);
          logger.info(new String(bytes, StandardCharsets.UTF_8));
        } else if (readBytes == -1) {
          key.cancel();
          sc.close();
        } else {
          logger.debug("receive " + readBytes + " byte buffer ");
        }
      } catch (IOException e) {
        logger.warn(e.getLocalizedMessage(), e);
      }
    }
  }


  private void doConnect() throws IOException {
    if (channel.connect(new InetSocketAddress(host, port))) {
      channel.register(selector, SelectionKey.OP_READ);
    } else {
      channel.register(selector, SelectionKey.OP_CONNECT);
    }
  }

  private void doWrite() throws IOException {
    byte[] req = "Hello World".getBytes();
    ByteBuffer buffer = ByteBuffer.allocate(req.length);
    buffer.put(req);
    buffer.flip();
    channel.write(buffer);
    if (!buffer.hasRemaining()) {
      logger.debug("Request sent");
    }

  }
}
