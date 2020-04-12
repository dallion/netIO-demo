package com.dallion.execrise.threadpool.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class PooledServerImpl implements Runnable {

  private Logger logger = Logger.getLogger(getClass());

  private volatile boolean isRunning = true;

  private int port;

  private String response;

  private TaskQueue queue;

  private WorkerPool workerPool;

  ThreadPoolExecutor executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
      50, 120l, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2 << 9));

  public PooledServerImpl(int port) {
    this.port = port;
    this.response = "Hello World";
    queue = new TaskQueue(1024);
    workerPool = new WorkerPool(queue);
  }

  public PooledServerImpl(int port, String response) {
    this(port);
    this.response = response;
  }

  @Override
  public void run() {
    try (ServerSocket server = new ServerSocket(port)) {
      logger.debug("Server Started at " + port);

      workerPool.init();

      Socket s = null;
      while (isRunning) {
        s = server.accept();
        boolean success = queue.offer(new Task(s, response));
        if (!success) {
          logger.warn("Server Receive Request Fail Cause Queue is Full");
        }
      }

    } catch (IOException e) {
      logger.warn(e.getLocalizedMessage(), e);
    }
  }
}
