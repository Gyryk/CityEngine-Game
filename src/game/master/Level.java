package game.master;

import city.cs.engine.World;
import game.*;
import game.bodies.Brute;
import game.bodies.Enemy;
import org.jbox2d.common.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static game.Game.*;

public class Level extends World {
    public int zoom;
    public String text;
    public ArrayList<Enemy> enemies;
    public ArrayList<Collect> collectibles;
    Map<String, List<Map<String, String>>> levelData;

    private View view;

    // Constructor
    public Level(String path, int zoom, String text) {
        this.zoom = zoom;
        this.text = text;
        levelData = LevelLoader.readJSON(path);

        enemies = new ArrayList<>();
        collectibles = new ArrayList<>();
    }

    // Load static data from file to world
    public void build() {
        // Load platforms
        for (Map<String, String> platform : levelData.get("platforms")) {
            Vec2 size = getSize(platform);
            Vec2 pos = getPosition(platform);
            String image = platform.get("image");
            String type = platform.get("type");
            if (Objects.equals(type, "bounce"))
                new Trampoline(this, size, pos);
            else if (Objects.equals(type, "turn")) {
                new Rotate(this, size, pos);
            } else if (Objects.equals(type, "vanish")) {
                new Vanish(this, size, pos);
            } else
                new Platform(this, size, pos, image, "static");
        }
    }

    // Load dynamic data from file to world
    public void load() {
        // Load enemies
        if (levelData.get("enemies") != null) {
            for (Map<String, String> enemy : levelData.get("enemies")) {
                String type = enemy.get("type");
                if (Objects.equals(type, "brute")) {
                    new Brute(5, 10, 50, getPosition(enemy), this, view);
                } else {
                    float[] e = enemyTypes.get(type);

                    float speed = e[6];
                    float range = e[1];
                    int health = (int) e[4];
                    int damage = (int) e[3];
                    float delay = e[5];
                    float radius = e[0];
                    float offset = e[2];
                    Vec2 pos = getPosition(enemy);
                    enemies.add(new Enemy(type, radius, range, offset, damage, health, delay, speed, this, pos));
                }
            }
        }

        // Load collectibles
        if (levelData.get("collectibles") != null) {
            for (Map<String, String> collectible : levelData.get("collectibles")) {
                Collect.Type type = Objects.equals(collectible.get("type"), "HEALTH") ? Collect.Type.HEALTH : Collect.Type.AMMO;
                Vec2 size = getSize(collectible);
                Vec2 pos = getPosition(collectible);
                int value = Integer.parseInt(collectible.get("value"));
                Collect c = new Collect(this, size, pos, type, value);
                collectibles.add(c);
            }
        }

        // Load guns
        if (levelData.get("guns") != null) {
            for (Map<String, String> gun : levelData.get("guns")) {
                String name = gun.get("name");
                float[] gf = gunTypes.get(name);
                String[] gs = gunImages.get(name);

                int damage = (int) gf[2];
                int ammo = (int) gf[5];
                float spread = gf[4];
                float range = gf[3];
                Vec2 size = new Vec2(gf[0], gf[1]);
                Vec2 pos = getPosition(gun);
                String image = gs[0];
                String icon = gs[1];
                collectibles.add(new Gun(this, size, damage, range, spread, pos, image, ammo, name, icon));
            }
        }
    }

    // Setter
    public void setView(View view) {
        this.view = view;
    }

    // Get Vec2 from json data
    Vec2 getPosition(Map<String, String> data) {
        float x = Float.parseFloat(data.get("x"));
        float y = Float.parseFloat(data.get("y"));
        return new Vec2(x, y);
    }

    Vec2 getSize(Map<String, String> data) {
        float x = Float.parseFloat(data.get("width"));
        float y = Float.parseFloat(data.get("height"));
        return new Vec2(x, y);
    }
}
