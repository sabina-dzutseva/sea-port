package service1;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleGenerator {

    static boolean randomFlag = true;

    public enum Cargo {
        dry,
        liquid,
        container
    }

    @JsonAutoDetect
    public static class Schedule implements Comparable<Schedule> {

        int day;
        int arrivalTime;

        String shipName;
        Cargo cargo;
        int amount;
        int anchorageTime;


        public Schedule() {

            if (randomFlag) {
                Random random = new Random();

                day = random.nextInt(30) ;
                arrivalTime = random.nextInt(1440);
                shipName = RandomString.nextString();

                int index = random.nextInt(Cargo.values().length);
                cargo = Cargo.values()[index];

                if (cargo == Cargo.container) {
                    amount = random.nextInt(5000 - 1) + 1;
                } else {
                    amount = random.nextInt(10000 - 1) + 1;
                }

                setAnchorageTime();

            }

        }


        public int getDay() {
            return day;
        }

        public int getArrivalTime() {
            return arrivalTime;
        }

        public String getShipName() {
            return shipName;
        }

        public Cargo getCargo() {
            return cargo;
        }

        public int getAmount() {
            return amount;
        }

        public int getAnchorageTime() {
            return anchorageTime;
        }



        public void setDay(int day) {
            if (day < 0 || day > 30) {
                throw new IllegalStateException("Unexpected value: " + day);
            }
            this.day = day;
        }

        public void setArrivalTime(int arrivalTime) {
            if (arrivalTime > 1439 || arrivalTime < 0) {
                throw new IllegalStateException("Unexpected value: " + arrivalTime);
            }
            this.arrivalTime = arrivalTime;
        }

        public void setShipName(String shipName) {

            if ((shipName.length() > 10 || shipName.length() < 3)) {
                throw new IllegalStateException("Unexpected value: " + shipName);
            }

            Pattern pattern = Pattern.compile("\\A[A-Z]+\\Z", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(shipName);

            if (!matcher.find()) {
                throw new IllegalStateException("Unexpected value: " + shipName);
            }

            this.shipName = shipName.toUpperCase(Locale.ROOT);
        }

        public void setCargo(Cargo cargo) {
            this.cargo = cargo;
        }

        public void setCargo(String cargo) {

            switch (cargo) {
                case "dry" : { this.cargo = Cargo.dry; break; }
                case "liquid" : { this.cargo = Cargo.liquid; break; }
                case "container" : { this.cargo = Cargo.container; break; }
                default : throw new IllegalStateException("Unexpected value: " + cargo);
            }
        }

        public void setAmount(int amount) {

            if (this.cargo == Cargo.container) {
                if (amount < 0 || amount > 5000) {
                    throw new IllegalStateException("Unexpected value: " + amount);
                }
            } else {
                if (amount < 0 || amount > 10000) {
                    throw new IllegalStateException("Unexpected value: " + amount);
                }
            }

            this.amount = amount;
        }

        public void setAnchorageTime() {

            if (cargo == Cargo.container) {
                anchorageTime = (int) (amount / 5);
            } else {
                anchorageTime = (int) (amount / 7);
            }

        }



        @Override
        public String toString() {
            return "Schedule {" +
                    " DAY: " + (day + 1) +
                    " TIME: " + (arrivalTime % (24 * 60) / 60) + " : " + (arrivalTime % (24 * 60) % 60) +
                    " NAME: '" + shipName + '\'' +
                    " CARGO: " + cargo +
                    " CARGO AMOUNT: " + amount +
                    " PLANNED ANCHORAGE TIME: " + anchorageTime +
                    '}' + '\n';
        }

        @Override
        public int compareTo(Schedule o) {
            return this.day * 24 * 60 + this.arrivalTime - (o.day * 24 * 60 + o.arrivalTime);
        }
    }


    public static ArrayList<Schedule> generateSchedule() {

        setRandomFlag(true);
        Random random = new Random();
        ArrayList<Schedule> array = new ArrayList<>();

        int max = random.nextInt(300 - 30) + 30;

        for (int i = 0; i < max; i++) {
            array.add(new Schedule());
        }

        array.sort(Schedule::compareTo);
        return array;
    }

    public static void setRandomFlag(boolean randomFlag) {
        ScheduleGenerator.randomFlag = randomFlag;
    }

}
