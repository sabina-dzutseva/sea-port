package service3;

import org.springframework.web.client.RestTemplate;
import service1.ScheduleGenerator;
import service1.Schedules;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import static service3.Port.numberOfCalls;
import static service3.Port.queueLength;

public class Service3 {

    public static final String DATA = "data.json";
    public static final String RESULT = "result.json";

    public static void main(String[] args) {

        RestTemplate restTemplate = new RestTemplate();

        try {
            Files.deleteIfExists(Path.of(DATA));
            Files.deleteIfExists(Path.of(RESULT));
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        restTemplate.getForObject("http://localhost:8080/service2/formedjson", File.class);


        Schedules schedules = restTemplate
                .getForObject("http://localhost:8080/service2/getschedule?filename=" + DATA, Schedules.class);


        ArrayList<ScheduleGenerator.Schedule> scheduleDeserialized = schedules.schedules;
        System.out.println(scheduleDeserialized.toString());

        ResultFormer resultFormer = new ResultFormer();

        long dryFine;
        long liquidFine;
        long containerFine;

        int dryCranes = 1;
        int liquidCranes = 1;
        int containerCranes = 1;

        do {

            Port port = new Port(scheduleDeserialized);
            LinkedBlockingQueue<Ship> unloaded = port.startModeling(dryCranes, liquidCranes, containerCranes);

            resultFormer = new ResultFormer(unloaded, dryCranes, liquidCranes, containerCranes, queueLength, numberOfCalls);

            dryFine = resultFormer.fines.get(0);
            liquidFine = resultFormer.fines.get(1);
            containerFine = resultFormer.fines.get(2);

            if (dryFine >= Crane.COST) {

                dryCranes += dryFine / Crane.COST;

            }

            System.out.println("NEW DRY:   " +  dryCranes);

            if (liquidFine >= Crane.COST) {

                liquidCranes += liquidFine / Crane.COST;

            }

            System.out.println("NEW LIQUID:   " +  liquidCranes);

            if (containerFine >= Crane.COST) {

                containerCranes += containerFine / Crane.COST;

            }

            System.out.println("NEW CONTAINER:   " +  containerCranes);

            System.out.println(resultFormer.result.toString());
            System.out.println(resultFormer.toString());

        } while (!((dryFine < 30000) && (liquidFine < 30000) && (containerFine < 30000)));


        restTemplate.postForObject("http://localhost:8080/service2/formresult", resultFormer, Void.class);
    }

}
