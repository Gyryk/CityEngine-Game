package game.custom;

import java.io.*;
import java.util.HashMap;

public class SettingsWriter {

    // Write settings to file
    public static void writeSettings(String file, int[] enemyP, int spawnRate, int dropRate, String[] targets) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Writing enemy probabilities
            writer.write("Enemy Probabilities: ");
            for (int k : enemyP) {
                writer.write(k + ", ");
            }
            writer.newLine(); // Adding a newline for better readability

            // Writing spawn rate
            writer.write("Spawn Rate: " + spawnRate);
            writer.newLine();

            // Writing drop rate
            writer.write("Drop Rate: " + dropRate);
            writer.newLine();

            // Writing targets array
            writer.write("Targets: ");
            if (targets.length > 0) {
                for (int i = 0; i < targets.length - 1; i++) {
                    writer.write(targets[i] + ", ");
                }
                writer.write(targets[targets.length - 1]); // Write the last element without a trailing comma
            }
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Read settings from file
    public static HashMap<String, String> readSettings(String file) {
        HashMap<String, String> settings = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Enemy Probabilities:")) {
                    settings.put("Enemy Probabilities", line.substring(line.indexOf(':') + 2));
                } else if (line.startsWith("Spawn Rate:")) {
                    settings.put("Spawn Rate", line.substring(line.indexOf(':') + 2));
                } else if (line.startsWith("Drop Rate:")) {
                    settings.put("Drop Rate", line.substring(line.indexOf(':') + 2));
                } else if (line.startsWith("Targets:")) {
                    settings.put("Targets", line.substring(line.indexOf(':') + 2));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
        return settings;
    }

    // Parse the probabilities
    public static int[] parseProbabilities(HashMap<String, String> settings) {
        String[] probStr = settings.get("Enemy Probabilities").split(", ");
        int[] enemyProb = new int[probStr.length];
        int sum = 0;
        for (int i = 0; i < probStr.length; i++) {
            enemyProb[i] = Integer.parseInt(probStr[i]);
            sum += enemyProb[i];
        }
        for (int i = 0; i < probStr.length; i++) {
            enemyProb[i] = enemyProb[i] / sum;
        }

        return enemyProb;
    }

    // Parse the targets
    public static int[] parseTargets(HashMap<String, String> settings) {
        String[] targetStr = settings.get("Targets").split(", ");
        int[] targets = new int[targetStr.length];
        for (int i = 0; i < targetStr.length; i++) {
            targets[i] = Integer.parseInt(targetStr[i]);
        }

        return targets;
    }
}
