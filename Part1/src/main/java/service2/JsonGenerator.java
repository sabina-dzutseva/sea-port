package service2;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import service1.ScheduleGenerator;
import service1.ScheduleGenerator.Schedule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class JsonGenerator {

    public static String fileName = "data.json";
    private static int objects = 0;

    public static void jsonGenerate(ArrayList<Schedule> scheduledData) {

        ScheduleGenerator.setRandomFlag(true);

        try {

            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

            for (Schedule scheduledDatum : scheduledData) {
                writer.writeValue(new FileOutputStream(fileName, true), scheduledDatum);
            }

            System.out.println("JSON formed");

            objects += scheduledData.size();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void scheduleConsoleInput() {

        Scanner in = new Scanner(System.in);

        ScheduleGenerator.setRandomFlag(false);
        Schedule schedule = new Schedule();

        System.out.println("Enter name:");
        schedule.setShipName(in.next());
        System.out.println("Enter day:");
        schedule.setDay(in.nextInt());
        System.out.println("Enter arrival time:");
        schedule.setArrivalTime(in.nextInt());
        System.out.println("Enter cargo type:");
        schedule.setCargo(in.next());
        System.out.println("Enter cargo amount:");
        schedule.setAmount(in.nextInt());
        schedule.setAnchorageTime();

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

        try {
            writer.writeValue(new FileOutputStream(fileName, true), schedule);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("JSON formed");
        objects++;

    }

    public static ArrayList<Schedule> scheduleDeserialize() {

        ArrayList<Schedule> array = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(Schedule.class);

        MappingIterator<Schedule> scheduleMappingIterator = null;
        try {
            scheduleMappingIterator = reader.readValues(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < objects; i++) {
            try {
                if (scheduleMappingIterator != null) {
                    array.add(scheduleMappingIterator.nextValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return array;
    }
}
