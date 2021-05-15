package service3;

import service1.ScheduleGenerator;

import java.util.Random;
import java.util.concurrent.Future;

public class Ship implements Comparable<Ship> {

    int day;
    int arrivalTime;
    String shipName;
    ScheduleGenerator.Cargo cargo;
    int amount;
    int plannedAnchorageTime;
    int anchorageTime;

    int scheduleDeviation;
    int anchorageTimeDeviation;

    int unloadedAmount = 0;


    long timeWhenUnloadingWithOneCraneStarts = -1;
    long timeWhenUnloadingWithTwoCranesStarts = -1;
    long timeWhenUnloaded = -1;

    Future anchorageFuture;

    public Ship(ScheduleGenerator.Schedule schedule) {

        day = schedule.getDay();
        arrivalTime = schedule.getArrivalTime();

        shipName = schedule.getShipName();
        cargo = schedule.getCargo();
        amount = schedule.getAmount();
        plannedAnchorageTime = schedule.getAnchorageTime();

        Random random = new Random();

        scheduleDeviation = random.nextInt(14) - 7;
        anchorageTimeDeviation = Math.abs(random.nextInt(1440));

        if (day + scheduleDeviation < 0) {
            day = 0;
            scheduleDeviation = 0;
        }

        anchorageTime = plannedAnchorageTime + anchorageTimeDeviation;
    }

    public Ship() { }


    public int getDay() {
        return day;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public String getShipName() {
        return shipName;
    }

    public ScheduleGenerator.Cargo getCargo() {
        return cargo;
    }

    public int getAmount() {
        return amount;
    }

    public int getAnchorageTime() {
        return anchorageTime;
    }

    public int getScheduleDeviation() {
        return scheduleDeviation;
    }

    public long getAnchorageTimeDeviation() {
        return anchorageTimeDeviation;
    }

    public int getUnloadedAmount() {
        return unloadedAmount;
    }

    public long getArrivalTimeMs() {
        return arrivalTime + (long) day * 24 * 60 + (long) scheduleDeviation * 24 * 60;
    }



    public long getWaitingTime() {
        return timeWhenUnloadingWithOneCraneStarts - getArrivalTimeMs();
}

    public long getAnchorageDuration() {
        return timeWhenUnloaded - timeWhenUnloadingWithOneCraneStarts;
    }

    public long getAnchorageBeginning() {
        return timeWhenUnloadingWithOneCraneStarts;
    }

    @Override
    public String toString() {
        return "Ship {" +
                "\n DAY: " + (day + 1 + scheduleDeviation) +
                "\n TIME: " + (arrivalTime % (24 * 60) / 60) + " : " + (arrivalTime % (24 * 60) % 60) +
                "\n NAME: '" + shipName + '\'' +
                "\n CARGO: " + cargo +
                "\n CARGO AMOUNT: " + amount +
                "\n ANCHORAGE TIME: " + anchorageTime +
                //", timeWhenUnloadingStarts=" + timeWhenUnloadingStarts +
                "}\n";
    }

    @Override
    public int compareTo(Ship o) {
        return this.day * 24 * 60 + this.arrivalTime - (o.day * 24 * 60 + o.arrivalTime)
                + this.scheduleDeviation * 24 * 60 - o.scheduleDeviation * 24 * 60;
    }
}
