package service3;

import service1.ScheduleGenerator;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class Port {

    public static List<Ship> dryCargos = new LinkedList<>();
    public static List<Ship> liquidCargos = new LinkedList<>();
    public static List<Ship> containerCargos = new LinkedList<>();

    static LinkedBlockingQueue<Crane> waitingDryCranes = new LinkedBlockingQueue<>();
    static LinkedBlockingQueue<Crane> waitingLiquidCranes = new LinkedBlockingQueue<>();
    static LinkedBlockingQueue<Crane> waitingContainerCranes = new LinkedBlockingQueue<>();

    static ConcurrentMap<Ship, LinkedBlockingQueue<Crane>> unloadingDryCargo = new ConcurrentHashMap<>();
    static ConcurrentMap<Ship, LinkedBlockingQueue<Crane>> unloadingLiquidCargo = new ConcurrentHashMap<>();
    static ConcurrentMap<Ship, LinkedBlockingQueue<Crane>> unloadingContainerCargo = new ConcurrentHashMap<>();

    static List<Ship> allShips = new LinkedList<>();
    public static LinkedBlockingQueue<Ship> current = new LinkedBlockingQueue<>();
    public static LinkedBlockingQueue<Ship> unloaded = new LinkedBlockingQueue<>();


    public static Map<ScheduleGenerator.Cargo, LinkedBlockingQueue<Crane>> cranes = new ConcurrentHashMap<>();
    public static Map<ScheduleGenerator.Cargo, ConcurrentMap<Ship, LinkedBlockingQueue<Crane>>> unloadingMaps = new ConcurrentHashMap<>();

    static int shipAmount;

    public Port(ArrayList<ScheduleGenerator.Schedule> schedule) {

        numberOfCalls = 0;
        queueLength = 0;
        initQueuesAndMaps(schedule);

    }

    static public int numberOfCalls;
    static public int queueLength;


    static AtomicLong start = new AtomicLong();

    public static long getTime() {

        long stop = System.currentTimeMillis();
        return stop - start.get();

    }

    public LinkedBlockingQueue<Ship> startModeling(int dryCranesAmount, int liquidCranesAmount, int containerCranesAmount) {

        unloaded = new LinkedBlockingQueue<>();

        waitingDryCranes = new LinkedBlockingQueue<>();

        for (int i = 0; i < dryCranesAmount; i++) {
            waitingDryCranes.add(new Crane());
        }

        waitingLiquidCranes = new LinkedBlockingQueue<>();

        for (int i = 0; i < liquidCranesAmount; i++) {
            waitingLiquidCranes.add(new Crane());
        }

        waitingContainerCranes = new LinkedBlockingQueue<>();

        for (int i = 0; i < containerCranesAmount; i++) {
            waitingContainerCranes.add(new Crane());
        }

        cranes.put(ScheduleGenerator.Cargo.dry, waitingDryCranes);
        cranes.put(ScheduleGenerator.Cargo.liquid, waitingLiquidCranes);
        cranes.put(ScheduleGenerator.Cargo.container, waitingContainerCranes);

        shipAmount = allShips.size();

        ExecutorService timer = Executors.newSingleThreadExecutor();
        timer.submit(new Utils.TimeManager());

        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

        start.set(System.currentTimeMillis());

        ses.schedule(new Utils.QueueCounter(ses, current), 60, TimeUnit.MILLISECONDS);

        do {
            if (!current.isEmpty()) {

                Ship ship;

                try {

                    ship = current.peek();
                    LinkedBlockingQueue<Crane> currentCranes = cranes.get(ship.cargo);

                    if (!currentCranes.isEmpty()) {

                        ship.timeWhenUnloadingWithOneCraneStarts = getTime();
                        ship = current.take();
                        LinkedBlockingQueue<Crane> newCrane = new LinkedBlockingQueue<>();
                        newCrane.add(currentCranes.take());
                        unloadingMaps.get(ship.cargo).put(ship, newCrane);
                        ship.anchorageFuture = ses.schedule(new CraneAssignee(ses, ship), ship.anchorageTime, TimeUnit.MILLISECONDS);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        } while (unloaded.size() != shipAmount);


        timer.shutdown();
        ses.shutdown();

        return unloaded;

    }


    private void initQueuesAndMaps(ArrayList<ScheduleGenerator.Schedule> schedules) {

        ArrayList<ScheduleGenerator.Schedule> array = schedules;
        array.sort(ScheduleGenerator.Schedule::compareTo);
        allShips = new LinkedList<>();

        for (ScheduleGenerator.Schedule schedule : array) {

            Ship ship = new Ship(schedule);

            allShips.add(ship);

            switch (schedule.getCargo()) {
                case dry: {
                    dryCargos.add(ship);
                    break;
                }
                case liquid: {
                    liquidCargos.add(ship);
                    break;
                }
                case container: {
                    containerCargos.add(ship);
                    break;
                }
            }
        }

        allShips.sort(Ship::compareTo);
        dryCargos.sort(Ship::compareTo);
        liquidCargos.sort(Ship::compareTo);
        containerCargos.sort(Ship::compareTo);


        current = new LinkedBlockingQueue<>();
        cranes = new ConcurrentHashMap<>();
        unloadingMaps = new ConcurrentHashMap<>();

        unloadingMaps.put(ScheduleGenerator.Cargo.dry, unloadingDryCargo);
        unloadingMaps.put(ScheduleGenerator.Cargo.liquid, unloadingLiquidCargo);
        unloadingMaps.put(ScheduleGenerator.Cargo.container, unloadingContainerCargo);

        cranes = Collections.synchronizedMap(cranes);
        unloadingMaps = Collections.synchronizedMap(unloadingMaps);
    }

}
