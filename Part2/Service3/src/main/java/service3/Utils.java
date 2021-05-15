package service3;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static service3.Port.*;

public class Utils {

    static class TimeManager implements Runnable {

        long lastShipTime = 0;

        @Override
        public void run() {

            for (Ship ship : allShips) {

                try {
                    sleep((long) (ship.day) * 24 * 60 + ship.arrivalTime + (long) ship.scheduleDeviation * 24 * 60 - lastShipTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                lastShipTime = (((long) (ship.day) * 24 * 60 + ship.arrivalTime
                        + (long) ship.scheduleDeviation * 24 * 60));

                try {
                    current.put(ship);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    static class QueueCounter implements Runnable {

        private final ScheduledExecutorService ses;
        private final LinkedBlockingQueue<Ship> currentQueue;

        QueueCounter(ScheduledExecutorService ses, LinkedBlockingQueue<Ship> currentQueue) {

            this.ses = ses;
            this.currentQueue = currentQueue;
        }

        @Override
        public void run() {

            numberOfCalls++;
            queueLength += currentQueue.size();

            ses.schedule(this, 60, TimeUnit.MILLISECONDS);

        }
    }

}
