package com.dallion.execrise.threadpool.server;


public class WorkerPool {

  private Worker[] workers = new Worker[10];

  private TaskQueue queue;

  public WorkerPool(TaskQueue queue) {
    this.queue = queue;
  }

  public void init() {
    for (int i = 0; i < workers.length; i++) {
      if (workers[i] == null) {
        workers[i] = new Worker(queue);
        new Thread(workers[i], "Worker-" + i).start();
      }
    }
  }
}
