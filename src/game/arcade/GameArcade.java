package game.arcade;

import city.cs.engine.StaticBody;
import city.cs.engine.StepListener;
import city.cs.engine.World;
import game.*;
import game.bodies.Enemy;
import game.bodies.Player;
import game.custom.SettingsWriter;
import game.master.Audio;
import game.master.Keys;
import game.master.Level;
import game.master.Mouse;
import org.jbox2d.common.Vec2;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static game.Menu.background;
import static game.Menu.frame;

/**
 * Your main game entry point
 */
public class GameArcade extends Game {
    static World world;
    static ArcadeView view;
    static ArcadeUpdate update;

    static Player player;

    static int killed;
    static float dropRate;

    static ArrayList<Collect> collectibles = new ArrayList<>();

    Clip bgm = null;
    String path;

    /**
     * Initialise a new Game.
     */
    public GameArcade() {
        dropRate = 0.85f;

        frame.setCursor(Cursor.getDefaultCursor());
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(background);

        JButton standard = new JButton("Standard");
        standard.setFont(new Font("Roboto", Font.ITALIC, 24));
        standard.setPreferredSize(new Dimension(240, 100));
        standard.setAlignmentX(Component.CENTER_ALIGNMENT);
        standard.addActionListener(e -> {
            frame.remove(panel);
            world = new World();
            update = new ArcadeUpdate(5000, new int[]{0, 1, 3, 7, 15}, new int[]{5, 10, 15, 20, 50});

            startGame();
            frame.repaint();
            frame.revalidate();
        });

        JButton custom = new JButton("Custom");
        custom.setFont(new Font("Roboto", Font.ITALIC, 24));
        custom.setPreferredSize(new Dimension(240, 100));
        custom.setAlignmentX(Component.CENTER_ALIGNMENT);
        custom.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("data/levels");
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                path = fileChooser.getSelectedFile().getPath();
                String settingsPath = path.substring(0, path.length() - 4) + "txt";
                frame.remove(panel);
                world = new Level(path, 10, "");

                initSettings(settingsPath);
                super.worldBuilder(world, update);
                startGame();
                frame.revalidate();
                frame.repaint();
            }
        });

        panel.add(standard);
        panel.add(Box.createRigidArea(new Dimension(100, 20)));
        panel.add(custom);
        frame.add(panel);
        frame.repaint();
        frame.revalidate();
    }

    // Spawn an enemy
    static void spawnEnemy(String type, World world) {
        float[] e = enemyTypes.get(type);
        Vec2 pos = new Vec2((float) (Math.random() * 60) - 30, (float) ((Math.random() * 48) - 24));
        Enemy enemy = new Enemy(type, e[0], e[1], e[2], (int) e[3], (int) e[4], e[5], e[6], world, pos);
        enemies.add(enemy);
        enemy.setView(view);

        view.addEnemy(enemy);
        Audio.playSound("data/sfx/spawn.wav");

        // Add a destruction listener to the enemy with drops
        enemy.addDestructionListener(ev -> {
            killed++;
            dropPickup(ev.getSource().getPosition());
            view.addScore(10);
            enemy.line.destroy();
        });
    }

    // Drop a pickup
    static void dropPickup(Vec2 position) {
        Collect.Type type = Math.random() > dropRate ? Collect.Type.HEALTH : Collect.Type.AMMO;
        Collect collect = new Collect(world, new Vec2(1, 1), position, type, 8);
        collectibles.add(collect);
    }

    // Make the platforms, trampolines, and other statics
    @Override
    protected void worldBuilder(World world, StepListener steps) {
        super.worldBuilder(world, steps);

        // make 3 platforms
        Vec2 platformShape = new Vec2(5, 1f);
        new Platform(world, platformShape, new Vec2(-20, -15f), "data/platform.png", "static");
        new Platform(world, new Vec2(10, 1f), new Vec2(0, 0f), "data/wide-platform.png", "static");
        new Platform(world, platformShape, new Vec2(20, -15f), "data/platform.png", "static");
        new Platform(world, new Vec2(1.8f, 1f), new Vec2(0, -25f), "data/short-platform.png", "static");

        // make 2 trampolines
        new Trampoline(world, new Vec2(4.8f, 2), new Vec2(-10, -25f));
        new Trampoline(world, new Vec2(4.8f, 2), new Vec2(10, -25f));

        // make 2 vanishing platforms
        new Vanish(world, platformShape, new Vec2(-25, 5f));
        new Vanish(world, platformShape, new Vec2(25, 5f));

        // make a rotating platform
        new Rotate(world, platformShape, new Vec2(0, 20f));
    }

    // Spawn initial enemies
    void startEnemies() {
        // Make bouncing enemy balls
        for (int i = 0; i < 3; i++) {
            spawnEnemy("normal", world);
        }
        spawnEnemy("small", world);
        spawnEnemy("big", world);
    }

    // Set the values for all the game settings
    void initSettings(String path) {
        HashMap<String, String> settings = SettingsWriter.readSettings(path);
        int[] targets = SettingsWriter.parseTargets(settings);
        int[] enemyProbabilities = SettingsWriter.parseProbabilities(settings);
        float spawnRate = Float.parseFloat(settings.get("Spawn Rate"));

        dropRate = Integer.parseInt(settings.get("Drop Rate")) / 100f;
        update = new ArcadeUpdate(spawnRate * 1000, targets, enemyProbabilities);
    }

    void startGame() {
        //1. make an empty game world
        world.getSimulationSettings().setTargetFrameRate(60);
        world.setGravity(10f);

        //2. make a view to look into the game world
        view = new ArcadeView(world, 1080, 720);
        view.setZoom(10f);
        frame.requestFocus();

        //3. populate it with bodies (ex: platforms, collectibles, characters)
        cursor = new StaticBody(world);

        //make the player
        player = new Player(world, 99, 0, new Vec2(-2, -5), 300f, 2f, 60);
        view.setPlayer(player);

        // initialise variables
        collectibles = new ArrayList<>();
        enemies = new ArrayList<>();
        killed = 0;

        if (world instanceof Level) {
            ((Level) world).build();
        } else {
            worldBuilder(world, update);
        }
        playerCursor(cursor, player, world, view);
        startEnemies();

        //4. create a Java window (frame) and add the game view to it
        Keys keys = new Keys(player, world, view, update);
        Mouse mouse = new Mouse(cursor, player, world, view, update);
        mouse.setAmmoMode(true);
        windowMaker(cursor, view, mouse, keys);

        // Add a destruction listener to the player
        player.addDestructionListener(d -> {
            scoredGameEnd(view, world, update, keys, mouse, "Score: " + view.getScore(), true, view.getScore());
            Audio.stopMusic(bgm);
            update.timer.cancel();
        });

        // 5. start our game world simulation!
        world.start();

        bgm = Audio.loopMusic("data/music/score.wav", 0.5f);
    }
}