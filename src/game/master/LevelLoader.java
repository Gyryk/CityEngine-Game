package game.master;

import game.Collect;
import game.Gun;
import game.Platform;
import game.bodies.Enemy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelLoader {
    public static void writeJSON(String filename, ArrayList<Platform> platforms, ArrayList<Enemy> enemies, ArrayList<Collect> collectibles, ArrayList<Gun> guns) {
        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write("{\n");

            // Write platforms
            fileWriter.write("\t\"platforms\": [\n");
            for (int i = 0; i < platforms.size(); i++) {
                Platform platform = platforms.get(i);
                fileWriter.write("\t\t{\"width\": \"" + platform.size.x + "\", \"height\": \"" + platform.size.y + "\", \"x\": \""
                        + platform.pos.x + "\", \"y\": \"" + platform.pos.y + "\", \"image\": \"" + platform.imgPath + "\", \"bounce\": \""
                        + platform.type + "\"}");
                if (i < platforms.size() - 1) {
                    fileWriter.write(",");
                }
                fileWriter.write("\n");
            }
            fileWriter.write("\t],\n");

            // Write enemies
            fileWriter.write("\t\"enemies\": [\n");
            for (int i = 0; i < enemies.size(); i++) {
                Enemy enemy = enemies.get(i);
                fileWriter.write("\t\t{\"x\": \"" + enemy.pos.x + "\", \"y\": \"" + enemy.pos.y + "\", \"type\": \""
                        + enemy.type + "\"}");
                if (i < enemies.size() - 1) {
                    fileWriter.write(",");
                }
                fileWriter.write("\n");
            }
            fileWriter.write("\t],\n");

            // Write collectibles
            fileWriter.write("\t\"collectibles\": [\n");
            for (int i = 0; i < collectibles.size(); i++) {
                Collect collectible = collectibles.get(i);
                fileWriter.write("\t\t{\"type\": \"" + collectible.getType() + "\", \"width\": \"" + collectible.size.x
                        + "\", \"height\": \"" + collectible.size.y + "\", \"x\": \"" + collectible.pos.x + "\", \"y\": \""
                        + collectible.pos.y + "\", \"value\": \"" + collectible.getValue() + "\"}");
                if (i < collectibles.size() - 1) {
                    fileWriter.write(",");
                }
                fileWriter.write("\n");
            }
            fileWriter.write("\t],\n");

            // Write guns
            fileWriter.write("\t\"guns\": [\n");
            for (int i = 0; i < guns.size(); i++) {
                Gun gun = guns.get(i);
                fileWriter.write("\t\t{\"x\": \"" + gun.pos.x + "\", \"y\": \"" + gun.pos.y + "\", \"name\": \""
                        + gun.name + "\"}");
                if (i < guns.size() - 1) {
                    fileWriter.write(",");
                }
                fileWriter.write("\n");
            }
            fileWriter.write("\t]\n");

            fileWriter.write("}\n");

            System.out.println("JSON data written to file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Read levels as JSON and store as Map
    public static Map<String, List<Map<String, String>>> readJSON(String filename) {
        Map<String, List<Map<String, String>>> jsonData = new HashMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonString.append(line);
            }

            // Remove white spaces
            String cleanedJsonString = jsonString.toString().replaceAll("\\s", "");

            // Check if JSON file is empty
            if (cleanedJsonString.isEmpty()) {
                System.out.println("JSON file is empty.");
                return jsonData;
            }

            // Extract data for platforms, enemies, collectibles, and guns
            jsonData = extractData(cleanedJsonString);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonData;
    }

    private static Map<String, List<Map<String, String>>> extractData(String jsonString) {
        Map<String, List<Map<String, String>>> jsonData = new HashMap<>();

        String[] types = {"platforms", "enemies", "collectibles", "guns"};

        // Extract data for each type
        for (String type : types) {
            List<Map<String, String>> dataList = new ArrayList<>();
            int startIndex = jsonString.indexOf("\"" + type + "\":[{");
            if (startIndex != -1) {
                int endIndex = jsonString.indexOf("}]", startIndex);
                if (endIndex != -1) {
                    String typeData = jsonString.substring(startIndex + type.length() + 5, endIndex + 1);
                    String[] entries = typeData.split("\\},\\{");
                    for (String entry : entries) {
                        Map<String, String> entryMap = new HashMap<>();
                        String[] fields = entry.split(",");
                        for (String field : fields) {
                            String[] keyValue = field.split(":");
                            entryMap.put(keyValue[0].replaceAll("[\"{}]", ""), keyValue[1].replaceAll("[\"{}]", ""));
                        }
                        dataList.add(entryMap);
                    }
                    jsonData.put(type, dataList);
                } else {
                    System.out.println("Invalid " + type);
                }
            } else {
                System.out.println(type + " not found");
            }
        }

        return jsonData;
    }

}
