package server.gameobjects;

import java.util.Timer;
import java.util.TimerTask;

public class GameTimer {

  private static Timer timer;

  private GameTimer() {
  }

  public static void startTimer(TimerTask task, long time) {
    timer = new Timer();
    timer.schedule(task, time);
  }

  public static void stopTimer() {
    if (timer != null) {
      try {
        timer.cancel();
        timer.purge();
      } finally {
        timer = null;
      }
    }
  }
}
