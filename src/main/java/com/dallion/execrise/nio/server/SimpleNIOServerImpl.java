package com.dallion.execrise.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import com.dallion.execrise.config.Config;

public class SimpleNIOServerImpl implements Runnable {

  private Logger logger = Logger.getLogger(SimpleNIOServerImpl.class);

  private volatile boolean isRunning;

  private ServerSocketChannel channel;

  private Selector selector;

  public SimpleNIOServerImpl() {

    try {
      selector = Selector.open();
      channel = ServerSocketChannel.open();

      channel.socket().bind(new InetSocketAddress(Config.PORT));
      channel.configureBlocking(false);

      channel.register(selector, SelectionKey.OP_ACCEPT);
      logger.debug("Server Start at : " + channel.getLocalAddress().toString());
    } catch (IOException e) {
      logger.warn(e.getLocalizedMessage(), e);
      System.exit(1);
    }
  }

  public void stop() {
    this.isRunning = false;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void run() {
    while (isRunning) {
      try {
        // 轮训
        selector.select(1000);

        // 找到对应的
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> it = selectionKeys.iterator();
        while (it.hasNext()) {
          SelectionKey key = it.next();
          it.remove(); // ?
          // 进行业务操作
          handleInput(key);

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

  public void handleInput(SelectionKey key) {
    if (!key.isValid()) {
      return;
    }
    
    logger.debug("Receive Client Request " + key.toString());

    if (key.isAcceptable()) {
      ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
      try {
        SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
      } catch (IOException e) {
        logger.warn(e.getLocalizedMessage(), e);
      }
    }

    if (key.isReadable()) {
      SocketChannel sc = (SocketChannel) key.channel();
      ByteBuffer buffer = ByteBuffer.allocate(1 << 10);

      try {
        int readBytes = sc.read(buffer);
        if (readBytes > 0) {
          buffer.flip();
          byte[] bytes = new byte[buffer.remaining()];
          buffer.get(bytes);
          logger.info(new String(bytes, StandardCharsets.UTF_8));
          doWrite(sc, new Date(System.currentTimeMillis()).toString());
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

  private void doWrite(SocketChannel channel, String response) throws IOException {
    if (response == null || response.trim().isEmpty()) {
      return;
    }

    byte[] bytes = response.getBytes();
    ByteBuffer buffer = ByteBuffer.allocate(bytes.length);

    buffer.put(bytes);
    buffer.flip();
    channel.write(buffer);
  }

}
