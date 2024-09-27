package game.survival;

import city.cs.engine.Shape;
import city.cs.engine.*;
import game.*;
import game.bodies.Boss;
import game.bodies.Brute;
import game.bodies.Enemy;
import game.bodies.Player;
import game.master.Audio;
import game.master.Keys;
import game.master.Mouse;
import org.jbox2d.common.Vec2;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static game.Menu.frame;

/**
 * Your main game entry point
 */
public class GameSurvival extends Game {
    static World world;
    static SurvivalView view;
    static SurvivalUpdate update;

    static Player player;

    static int killed;
    static int wave;
    static int maxEnemies = 64;
    static int enemyCount;
    static boolean bossAlive;

    Clip bgm;

    /**
     * Initialise a new Game.
     */
    public GameSurvival() {
        //1. make an empty game world
        world = new World();
        world.getSimulationSettings().setTargetFrameRate(60);
        world.setGravity(10f);

        //2. make a view to look into the game world
        view = new SurvivalView(world, 1080, 720);
        view.setZoom(5f);
        frame.requestFocus();

        //3. populate it with bodies (ex: platforms, collectibles, characters)
        cursor = new StaticBody(world);

        //make the player
        player = new Player(world, 199, 0, new Vec2(-2, -5), 300f, 3f, 60);
        view.setPlayer(player);

        // initialise variables
        enemies = new ArrayList<>();
        killed = 0;
        enemyCount = 0;
        wave = 1;

        update = new SurvivalUpdate();
        playerCursor(cursor, player, world, view);
        worldBuilder(world, update);

        //4. create a Java window (frame) and add the game view to it
        Keys keys = new Keys(player, world, view, update);
        Mouse mouse = new Mouse(cursor, player, world, view, update);
        windowMaker(cursor, view, mouse, keys);

        // Add a destruction listener to the player
        player.addDestructionListener(d -> {
            scoredGameEnd(view, world, update, keys, mouse, "Time Survived: " + TimeWriter.convert(view.millis),
                    false, view.millis);
            if (bgm != null) Audio.stopMusic(bgm);
        });

        // this is not the cleanest way of adding the guns to the player's inventory, but it is the shortest
        player.inventory.add(new Gun(world, new Vec2(1.5f, 1.1f), 5, 50, 0.2f,
                new Vec2(1000, 1000), "data/guns/M1911.png", 1, "pistol", "data/guns/handgun.png"));
        player.inventory.add(new Gun(world, new Vec2(1.75f, 0.5f), 25, 15, 0.4f,
                new Vec2(1000, 1000), "data/guns/bayonet.png", 0, "knife", "data/guns/knife.png"));
        player.inventory.add(new Gun(world, new Vec2(4.9f, 1f), 30, 100, 1f,
                new Vec2(1000, 1000), "data/guns/SVT-40.png", 5, "sniper", "data/guns/sniper.png"));
        player.inventory.add(new Gun(world, new Vec2(4f, 1.5f), 10, 80, 0.1f,
                new Vec2(1000, 1000), "data/guns/StG44.png", 2, "rifle", "data/guns/rifle.png"));
        player.inventory.add(new Gun(world, new Vec2(4f, 1f), 7, 40, 0.8f,
                new Vec2(1000, 1000), "data/guns/BAR.png", 3, "shotgun", "data/guns/shotgun.png"));

        player.equipped = 0;
        player.useAmmo = player.inventory.get(0).ammo;
        player.damage = player.inventory.get(0).damage;

        player.lineRange = player.inventory.get(0).range;
        player.redrawLine(player.inventory.get(0).spread, view.getZoom());
        player.redrawGun(player.inventory.get(0), update);

        // 5. start our game world simulation!
        world.start();

        bgm = Audio.loopMusic("data/music/score.wav", 0.5f);
    }

    // Spawn an enemy
    static void spawnEnemy(String key, Vec2 pos) {
        float[] e = enemyTypes.get(key);
        Enemy enemy = new Enemy(key, e[0], e[1], e[2], (int) e[3], (int) e[4], e[5], e[6], world, pos);
        enemy.setView(view);
        view.addEnemy(enemy);
        enemies.add(enemy);

        Audio.playSound("data/sfx/spawn.wav");

        // Add a destruction listener to the enemy with drops
        enemy.addDestructionListener(ev -> {
            killed++;
            enemy.line.destroy();
        });
    }

    // Spawn a brute
    static void spawnBrute(Vec2 pos) {
        Brute brute = new Brute(5f, 10 * (int) Math.log10(wave), 50, pos, world, view);
        Audio.playSound("data/sfx/spawn.wav");

        // Add a destruction listener to the enemy with drops
        brute.addDestructionListener(ev -> killed++);
    }

    // Spawn a boss
    static void spawnBoss() {
        Boss boss = new Boss(world, new Vec2(0, 0), false);
        Audio.playSound("data/sfx/spawn.wav");
        boss.setView(view);
        bossAlive = true;

        // Add a destruction listener to the enemy with drops
        boss.addDestructionListener(ev -> {
            killed++;
            bossAlive = false;
        });
    }

    // Spawn a wave of enemies
    static void spawnWave() {
        int x;
        if (wave % 2 == 0) x = -64;
        else x = 64;

        if (wave % 5 == 0) {
            spawnBoss();
        } else {
            if (wave >= 2) {
                for (int i = 0; i < wave / 2; i++) {
                    if (enemyCount - killed >= maxEnemies) break;
                    enemyCount++;
                    Timer timer = new Timer(3000 * i, e ->
                            spawnBrute(new Vec2((float) (Math.random() * 10 * (x / 64)) + x, (float) (Math.random() * 100) - 50)));

                    timer.setRepeats(false);
                    timer.start();

                }
            }
            for (int i = 0; i < (int) (Math.random() * wave * 2); i++) {
                if (enemyCount - killed >= maxEnemies) break;
                enemyCount++;
                Timer timer = new Timer((int) (Math.random() * 3000 * i), e ->
                        spawnEnemy("tank", new Vec2((float) (Math.random() * 10 * (x / 64)) + x, (float) (Math.random() * 100) - 50)));

                timer.setRepeats(false);
                timer.start();
            }
            for (int i = 0; i < (int) (Math.random() * wave * 3); i++) {
                if (enemyCount - killed >= maxEnemies) break;
                enemyCount++;
                Timer timer = new Timer((int) (Math.random() * 2000 * i), e ->
                        spawnEnemy("fast", new Vec2((float) (Math.random() * 10 * (x / 64)) + x, (float) (Math.random() * 100) - 50)));

                timer.setRepeats(false);
                timer.start();
            }

            for (int i = 0; i < wave; i++) {
                if (enemyCount - killed >= maxEnemies) break;
                enemyCount++;
                Timer timer = new Timer(2000 * i, e ->
                        spawnEnemy("big", new Vec2((float) (Math.random() * 10 * (x / 64)) + x, (float) (Math.random() * 100) - 50)));

                timer.setRepeats(false);
                timer.start();
            }
            for (int i = 0; i < wave * 2; i++) {
                if (enemyCount - killed >= maxEnemies) break;
                enemyCount++;
                Timer timer = new Timer(1000 * i, e ->
                        spawnEnemy("small", new Vec2((float) (Math.random() * 10 * (x / 64)) + x, (float) (Math.random() * 100) - 50)));

                timer.setRepeats(false);
                timer.start();
            }
            for (int i = 0; i < wave * 3; i++) {
                if (enemyCount - killed >= maxEnemies) break;
                enemyCount++;
                Timer timer = new Timer(500 * i, e ->
                        spawnEnemy("normal", new Vec2((float) (Math.random() * 10 * (x / 64)) + x, (float) (Math.random() * 100) - 50)));

                timer.setRepeats(false);
                timer.start();
            }
        }
        // update wave
        wave++;
    }

    // Make the platforms, trampolines, and other statics
    @Override
    protected void worldBuilder(World world, StepListener steps) {
        // Add boundaries
        Shape shape = new BoxShape(80f, 1f);
        new Platform(world, shape, new Vec2(0f, 60f), Color.DARK_GRAY);
        Platform ground = new Platform(world, shape, new Vec2(0f, -60f), Color.DARK_GRAY);
        SolidFixture sf = new SolidFixture(ground, shape);
        sf.setRestitution(2f); // Make it so that no enemy gets stuck on the ground

        Shape wallShape = new BoxShape(1f, 60f);
        new Platform(world, wallShape, new Vec2(-79f, 0f), Color.DARK_GRAY);
        new Platform(world, wallShape, new Vec2(79f, 0f), Color.DARK_GRAY);

        // Add game objects
        Vec2 platformShape = new Vec2(5, 1f);
        new Rotate(world, platformShape, new Vec2(60, 0f));
        new Rotate(world, platformShape, new Vec2(-60, 0f));

        new Vanish(world, platformShape, new Vec2(-20, -5f));
        new Vanish(world, platformShape, new Vec2(60, 30f));

        new Platform(world, platformShape, new Vec2(20, 5f), "data/platform.png", "static");

        new Trampoline(world, new Vec2(4.8f, 2), new Vec2(0, -25f));
        new Trampoline(world, new Vec2(4.8f, 2), new Vec2(40, -25f));

        new Platform(world, new Vec2(10, 1f), new Vec2(20, -50f), "data/wide-platform.png", "static");
        new Platform(world, new Vec2(10, 1f), new Vec2(-20, -50f), "data/wide-platform.png", "static");

        new Platform(world, new Vec2(1.8f, 1f), new Vec2(-60, -50), "data/short-platform.png", "static");
        new Platform(world, new Vec2(1.8f, 1f), new Vec2(-50, 30), "data/short-platform.png", "static");
        new Platform(world, new Vec2(1.8f, 1f), new Vec2(-30, 20), "data/short-platform.png", "static");
        new Platform(world, new Vec2(1.8f, 1f), new Vec2(0, 30), "data/short-platform.png", "static");
        new Platform(world, new Vec2(1.8f, 1f), new Vec2(20, 30), "data/short-platform.png", "static");


        // Update game state to draw accurate lines and check for collider intersections
        world.addStepListener(steps);
    }
}