package service3;

import service1.ScheduleGenerator;

import java.util.HashMap;
import java.util.Map;

public class Crane {

    public static int COST = 30000;
    public Map<ScheduleGenerator.Cargo, Integer> performance = new HashMap<>();

    public Crane() {
        performance.put(ScheduleGenerator.Cargo.dry, 7);
        performance.put(ScheduleGenerator.Cargo.liquid, 7);
        performance.put(ScheduleGenerator.Cargo.container, 5);
    }

}
















