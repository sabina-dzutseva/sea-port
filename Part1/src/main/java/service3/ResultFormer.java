package service3;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

@JsonAutoDetect
public class ResultFormer {

    public LinkedList<Ship> ships = new LinkedList<>();
    public LinkedList<Integer> cranes = new LinkedList<>();
    public int queueLength;
    public int numberOfQueueCalls;

    public LinkedList<Statistics> result;

    public Double averageQueueWaiting;

    public Long maxAnchorageDelay;
    public Double averageAnchorageDelay;

    public Double averageQueueLength;

    public ArrayList<Long> fines;

    int requiredDryCranes;
    int requiredLiquidCranes;
    int requiredContainerCranes;

    public ResultFormer() {
    }

    public ResultFormer(LinkedBlockingQueue<Ship> unloadedShips, int dryCranesAmount, int liquidCranesAmount, int containerCranesAmount, int queueLength, int calls) {
        ships = new LinkedList<>();
        cranes = new LinkedList<>();
        result = new LinkedList<>();

        requiredDryCranes = dryCranesAmount;
        requiredLiquidCranes = liquidCranesAmount;
        requiredContainerCranes = containerCranesAmount;

        for (Ship ship : unloadedShips) {

            if (ship.getDay() + ship.getScheduleDeviation() < 30) {
                ships.add(ship);
            }

        }

        cranes.add(dryCranesAmount);
        cranes.add(liquidCranesAmount);
        cranes.add(containerCranesAmount);

        this.queueLength = queueLength;
        numberOfQueueCalls = calls;

        calculateResult();
    }

    public void calculateResult() {

        long sumQueueWaiting = 0;
        long dryQueueWaiting = 0;
        long liquidQueueWaiting = 0;
        long containerQueueWaiting = 0;

        long sumAnchorageDelay = 0;

        maxAnchorageDelay = 0L;

        for (Ship ship : ships) {

            result.add(new Statistics(ship));

            if (maxAnchorageDelay < ship.getAnchorageTimeDeviation()) {

                maxAnchorageDelay = ship.getAnchorageTimeDeviation();

            }

            switch (ship.getCargo()) {

                case dry: {
                    dryQueueWaiting += ship.getWaitingTime();
                    break;
                }
                case liquid: {
                    liquidQueueWaiting += ship.getWaitingTime();
                    break;
                }
                case container: {
                    containerQueueWaiting += ship.getWaitingTime();
                    break;
                }
            }

            sumQueueWaiting += ship.getWaitingTime();
            sumAnchorageDelay += ship.getAnchorageTimeDeviation();
        }

        averageQueueLength = ((double) (queueLength) / (numberOfQueueCalls));
        averageQueueWaiting = ((double) sumQueueWaiting / ships.size());
        averageAnchorageDelay = ((double) sumAnchorageDelay / ships.size());

        fines = new ArrayList<>();
        fines.add(0, dryQueueWaiting / 60 * 100);
        fines.add(1, liquidQueueWaiting / 60 * 100);
        fines.add(2, containerQueueWaiting / 60 * 100);
    }


    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("== RESULT == \n\n");

        out.append("\nNumber of unloaded ships: ").append(result.size());
        out.append("\nAverage queue length: ").append(averageQueueLength);
        out.append("\nAverage waiting time in queue: ").append(averageQueueWaiting);
        out.append("\nMax anchorage delay: ").append(maxAnchorageDelay);
        out.append("\nAverage anchorage delay: ").append(averageAnchorageDelay);
        out.append("\nFine: ").append(fines.get(0) + fines.get(1) + fines.get(2)).append("\n\n");
        out.append("FINAL CRANES AMOUNT\n").append("for dry cargo: ").append(requiredDryCranes)
                .append("\nfor liquid cargo: ").append(requiredLiquidCranes)
                .append("\nfor container cargo: ").append(requiredContainerCranes);

        return out.toString();

    }
}
