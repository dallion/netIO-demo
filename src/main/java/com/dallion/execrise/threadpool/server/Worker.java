package com.dallion.execrise.threadpool.server;

import org.apache.log4j.Logger;

public class Worker implements Runnable {

  private Logger logger = Logger.getLogger(getClass());

  private TaskQueue queue;

  private volatile boolean shouldRun = true;

  public Worker(TaskQueue queue) {
    this.queue = queue;
    logger.info("Worker Created");
  }

  @Override
  public void run() {
    while (shouldRun) {
      try {
        Task task = queue.take();
        logger.debug(Thread.currentThread().getName() + " has fetch a task");
        task.run();
      } catch (Exception e) {
        logger.warn(e.getLocalizedMessage(), e);
      }
    }
  }

}
