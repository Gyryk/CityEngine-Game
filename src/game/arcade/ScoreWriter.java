package game.arcade;

import java.io.*;
import java.util.ArrayList;

public class ScoreWriter {
    // Add scores to the file
    public static void writeScoreToFile(ScoreData scoreData) {
        String filename = "data/scores.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            // Append the player name and score to the file
            writer.write(scoreData.playerName + ":" + scoreData.score);
            writer.newLine(); // Move to the next line for the next entry
            System.out.println("Score added to file successfully.");
        } catch (IOException e) {
            System.err.println("Error writing score to file: " + e.getMessage());
        }
    }

    // Return the scores that have been stored
    public static ArrayList<ScoreData> readScoresFromFile(String filename) {
        ArrayList<ScoreData> scores = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String playerName = parts[0];
                int score = Integer.parseInt(parts[1]);
                scores.add(new ScoreData(playerName, score));
            }

            // Sort the scores in descending order based on score
            scores.sort((s1, s2) -> Integer.compare(s2.score, s1.score));
        } catch (IOException e) {
            System.err.println("Error reading scores from file: " + e.getMessage());
        }

        return scores;
    }

    public record ScoreData(String playerName, int score) {
        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }
    }
}
