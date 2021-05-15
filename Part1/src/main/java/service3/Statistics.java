package service3;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class Statistics {

    public String name;
    public long arrivalTime;
    public long waitingTime;
    public long anchorageBeginning;
    public long anchorageDuration;

    Statistics(Ship ship) {

        name = ship.getShipName();
        arrivalTime = ship.getArrivalTimeMs();
        waitingTime = ship.getWaitingTime();
        anchorageBeginning = ship.getAnchorageBeginning();
        anchorageDuration = ship.getAnchorageDuration();

    }

    @Override
    public String toString() {
        return "Statistics{" +
                " Ship name '" + name + '\'' +
                " arrivalTime=" + (arrivalTime / (24 * 60)) + ":" + (arrivalTime % (24 * 60) / 60) + ":" + (arrivalTime % (24 * 60) % 60) +
                " waitingTime=" + (waitingTime / (24 * 60)) + ":" + (waitingTime % (24 * 60) / 60) + ":" + (waitingTime % (24 * 60) % 60) +
                " anchorage began at " + (anchorageBeginning / (24 * 60)) + ":" + (anchorageBeginning % (24 * 60) / 60) + ":" + (anchorageBeginning % (24 * 60) % 60) +
                " anchorage duration =" + (anchorageDuration / (24 * 60)) + ":" + (anchorageDuration % (24 * 60) / 60) + ":" + (anchorageDuration % (24 * 60) % 60) +
                "}\n";
    }
}