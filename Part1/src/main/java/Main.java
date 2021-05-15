import service1.ScheduleGenerator;
import service3.Crane;
import service3.Port;
import service3.ResultFormer;
import service3.Ship;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import static service2.JsonGenerator.*;
import static service3.Port.numberOfCalls;
import static service3.Port.queueLength;


public class Main {

    public static void main(String[] args) {

        try {
            Files.deleteIfExists(Path.of(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<ScheduleGenerator.Schedule> schedule = ScheduleGenerator.generateSchedule();
        jsonGenerate(schedule);
        scheduleConsoleInput();
        ArrayList<ScheduleGenerator.Schedule> scheduleDeserialized = scheduleDeserialize();
        System.out.println(scheduleDeserialized.toString());

        long dryFine;
        long liquidFine;
        long containerFine;

        int dryCranes = 1;
        int liquidCranes = 1;
        int containerCranes = 1;

        do {

            Port port = new Port(scheduleDeserialized);
            LinkedBlockingQueue<Ship> unloaded = port.startModeling(dryCranes, liquidCranes, containerCranes);

            ResultFormer resultFormer = new ResultFormer(unloaded, dryCranes, liquidCranes, containerCranes, queueLength, numberOfCalls);
            resultFormer.calculateResult();

            dryFine = resultFormer.fines.get(0);
            liquidFine = resultFormer.fines.get(1);
            containerFine = resultFormer.fines.get(2);

            if (dryFine >= Crane.COST) {

                dryCranes += dryFine / Crane.COST;

            }

            System.out.println("NEW DRY:   " + dryCranes);

            if (liquidFine >= Crane.COST) {

                liquidCranes += liquidFine / Crane.COST;

            }

            System.out.println("NEW LIQUID:   " + liquidCranes);

            if (containerFine >= Crane.COST) {

                containerCranes += containerFine / Crane.COST;

            }

            System.out.println("NEW CONTAINER:   " + containerCranes);

            System.out.println(resultFormer.result.toString());
            System.out.println(resultFormer.toString());

        } while (!((dryFine < 30000) && (liquidFine < 30000) && (containerFine < 30000)));


    }
}
