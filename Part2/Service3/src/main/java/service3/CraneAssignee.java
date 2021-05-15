package service3;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CraneAssignee implements Runnable {

    private final ScheduledExecutorService ses;
    private final Ship ship;

    CraneAssignee(ScheduledExecutorService ses, Ship ship) {

        this.ses = ses;
        this.ship = ship;

    }

    private int getRescheduleTimeoutMs(Ship ship, Crane crane) {

        ship.timeWhenUnloadingWithTwoCranesStarts = Port.getTime();
        double performance = crane.performance.get(ship.cargo);
        ship.unloadedAmount = (int) ((ship.timeWhenUnloadingWithTwoCranesStarts - ship.timeWhenUnloadingWithOneCraneStarts) * performance);
        return (int) ((ship.amount - ship.unloadedAmount) / (performance * 2));

    }


    @Override
    public void run() {

        ship.timeWhenUnloaded = Port.getTime();

        LinkedBlockingQueue<Crane> cranes = Port.unloadingMaps.get(ship.cargo).get(ship);
        ConcurrentMap<Ship, LinkedBlockingQueue<Crane>> map = Port.unloadingMaps.get(ship.cargo);
        map.remove(ship);
        System.out.println("UNLOADED " + ship.shipName);
        try {
            Port.unloaded.put(ship);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (!cranes.isEmpty()) {

            for (Ship ship : map.keySet()) {

                if (map.get(ship).size() == 1) {

                    ship.timeWhenUnloadingWithTwoCranesStarts = Port.getTime();

                    try {
                        map.get(ship).put(cranes.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ship.anchorageFuture.cancel(true);
                    System.out.println("2 cranes unloading " + ship.shipName);
                    ship.anchorageFuture = ses.schedule(new CraneAssignee(ses, ship),
                            getRescheduleTimeoutMs(ship, map.get(ship).peek()), TimeUnit.MILLISECONDS);

                }

                if (cranes.isEmpty()) {
                    break;
                }
            }

            if (!cranes.isEmpty()) {
                Port.cranes.get(ship.cargo).addAll(cranes);
                break;
            }

        }

    }

}
