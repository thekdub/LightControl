package com.thekdub.lightcontrol.inputhandler;

import java.util.ArrayList;
import java.util.List;

public abstract class InputHandler {

  private static final List<Thread> THREADS = new ArrayList<>();

  public static boolean threadsRunning() {
    return !THREADS.isEmpty();
  }

  public abstract boolean accepts(String input);

  abstract boolean execute(String input);

  public boolean handle(String input) {
    if (accepts(input)) {
      if (async()) {
        Thread thread = new Thread(() -> {
          execute(input);
          THREADS.remove(Thread.currentThread());
        });
        THREADS.add(thread);
        thread.start();
        return true;
      }
      else {
        return execute(input);
      }
    }
    return false;
  }

  public abstract boolean async();

}
