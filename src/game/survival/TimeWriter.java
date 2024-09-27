package game.survival;

import java.io.*;
import java.util.ArrayList;

public class TimeWriter {
    //Convert from milliseconds to minutes and seconds
    public static String convert(int millis) {
        int milliseconds = millis % 1000;
        int seconds = (millis / 1000) % 60;
        int minutes = (millis / (60000)) % 60;
        String min = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
        String sec = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
        String mil = milliseconds < 10 ? "00" + milliseconds : milliseconds < 100 ? "0" + milliseconds : String.valueOf(milliseconds);
        return min + ":" + sec + ":" + mil;
    }

    // Add scores to the file
    public static void writeTimeToFile(TimeData timeData) {
        String filename = "data/times.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            // Append the player name and score to the file
            writer.write(timeData.playerName + ":" + timeData.millis);
            writer.newLine(); // Move to the next line for the next entry
            System.out.println("Time added to file successfully.");
        } catch (IOException e) {
            System.err.println("Error writing score to file: " + e.getMessage());
        }
    }

    // Return the scores that have been stored
    public static ArrayList<TimeData> readTimeFromFile(String filename) {
        ArrayList<TimeData> times = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String playerName = parts[0];
                int time = Integer.parseInt(parts[1]);
                times.add(new TimeData(playerName, time));
            }

            // Sort the scores in descending order based on score
            times.sort((s1, s2) -> Integer.compare(s2.millis, s1.millis));
        } catch (IOException e) {
            System.err.println("Error reading scores from file: " + e.getMessage());
        }

        return times;
    }

    public record TimeData(String playerName, int millis) {
        public String getPlayerName() {
            return playerName;
        }

        public int getTime() {
            return millis;
        }
    }
}
