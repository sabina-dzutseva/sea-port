package service1;

import java.util.ArrayList;

public class Schedules {

    public ArrayList<ScheduleGenerator.Schedule> schedules;

    public Schedules() {
        this.schedules = new ArrayList<>();
    }

    public Schedules(ArrayList<ScheduleGenerator.Schedule> schedules) {
        this.schedules = schedules;
    }
}
