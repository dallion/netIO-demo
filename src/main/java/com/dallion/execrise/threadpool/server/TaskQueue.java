package com.dallion.execrise.threadpool.server;

import java.util.concurrent.ArrayBlockingQueue;
import org.apache.log4j.Logger;

/**
 * @author dallion
 *
 */
public final class TaskQueue extends ArrayBlockingQueue<Task> {

  private Logger logger = Logger.getLogger(TaskQueue.class);
  
  public TaskQueue(int capacity) {
    super(capacity);
  }

  private static final long serialVersionUID = 4790678596193667717L;

  @Override
  public boolean offer(Task e) {
    boolean result = super.offer(e);
    logger.trace("offer Task " + result + " \t " + size());
    return result;
  }

}
