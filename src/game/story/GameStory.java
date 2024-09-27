package game.story;

import city.cs.engine.Shape;
import city.cs.engine.*;
import game.Menu;
import game.*;
import game.bodies.Boss;
import game.bodies.Enemy;
import game.bodies.Player;
import game.master.Audio;
import game.master.Keys;
import game.master.Level;
import game.master.Mouse;
import org.jbox2d.common.Vec2;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

import static game.Menu.frame;

/**
 * Your main game entry point
 */
public class GameStory extends Game {
    public static boolean bossKilled;
    public static ArrayList<Collect> collectibles = new ArrayList<>();
    public static ArrayList<Enemy> enemies = new ArrayList<>();
    static Level[] levels;
    static Level world;
    static StoryView view;
    static StoryUpdate update;
    static Player player;
    static int killed;
    static int currLevel;
    private final Color background = new Color(20, 20, 20);
    private Clip bgm = null;

    /**
     * Initialise a new Game.
     */
    public GameStory() {
        levels = new Level[]{new Level("data/levels/level1.json", 9, "What is this place? How did I get here?"),
                new Level("data/levels/level2.json", 8, "Please don't do this. I just want to go back home."),
                new Level("data/levels/level3.json", 7, "They won't let me leave, I don't have a choice..."),
                new BossLevel("data/levels/level4.json", 6, "The exit is right there, but I can't leave without defeating him.")};

        //1. make an empty game world
        currLevel = 0;
        world = levels[currLevel];
        world.getSimulationSettings().setTargetFrameRate(60);
        world.setGravity(10f);

        //2. make a view to look into the game world
        view = new StoryView(world, 1080, 720);
        view.setZoom(world.zoom);
        world.setView(view);
        frame.requestFocus();

        //3. populate it with bodies (ex: platforms, collectibles, characters)
        cursor = new StaticBody(world);
        playMusic();

        //make the player
        player = new Player(world, 99, 0, new Vec2(-2, -5), 300f, 2f, 60);
        view.setPlayer(player);

        // initialise variables
        bossKilled = false;
        killed = 0;
        enemies = new ArrayList<>();
        collectibles = world.collectibles;
        update = new StoryUpdate(this);

        playerCursor(cursor, player, world, view);
        worldBuilder(world.zoom);
        world.build();
        startEnemies();

        //4. create a Java window (frame) and add the game view to it
        Keys keys = new Keys(player, world, view, update);
        Mouse mouse = new Mouse(cursor, player, world, view, update);
        windowMaker(cursor, view, mouse, keys);

        // Add a destruction listener to the player
        player.addDestructionListener(d -> gameOver(view, world, update, keys, mouse));

        // Add a destruction listener to the collectibles
        for (Collect c : collectibles) {
            c.obj.addDestructionListener(ev -> {
                if (c instanceof Gun) {
                    player.redrawGun((Gun) c, update);
                }
            });
        }

        // 5. start our game world simulation!
        world.start();
    }

    // Spawn initial enemies and set their properties
    static void startEnemies() {
        // Make bouncing enemy balls
        world.load();
        enemies = world.enemies;

        for (Enemy enemy : enemies) {
            enemy.setView(view);
            view.addEnemy(enemy);

            // Add a destruction listener to the enemy
            enemy.addDestructionListener(ev -> {
                killed++;
                enemy.line.destroy();
            });
        }
    }

    // Create the world for each level
    private void worldBuilder(int zoom) {
        float y = 29;
        float x = 39;

        switch (zoom) {
            case 9 -> {
                x = 43;
                y = 30;
            }
            case 8 -> {
                x = 49;
                y = 35;
            }
            case 7 -> {
                x = 56;
                y = 40;
            }
            case 6 -> {
                x = 64;
                y = 48;
            }
        }

        Shape shape = new BoxShape(x + 1, 1f);
        new Platform(world, shape, new Vec2(0f, y), Color.DARK_GRAY);
        new Platform(world, shape, new Vec2(0f, -y), Color.DARK_GRAY);
        Shape wallShape = new BoxShape(1f, y);
        new Platform(world, wallShape, new Vec2(-x, 0f), Color.DARK_GRAY);
        new Platform(world, wallShape, new Vec2(x, 0f), Color.DARK_GRAY);

        // Update game state to draw accurate lines and check for collider intersections
        world.addStepListener(update);
    }

    // Play the appropriate background music for the level
    private void playMusic() {
        if (bgm != null) Audio.stopMusic(bgm);
        switch (currLevel) {
            case 0 -> bgm = Audio.loopMusic("data/music/level1.wav", 0.5f);
            case 1 -> bgm = Audio.loopMusic("data/music/level2.wav", 0.5f);
            case 2 -> bgm = Audio.loopMusic("data/music/level3.wav", 0.5f);
            case 3 -> bgm = Audio.loopMusic("data/music/level4.wav", 0.5f);
        }
    }

    // Show game over menu
    @Override
    protected void gameOver(UserView view, World world, Update update, KeyListener keys, MouseAdapter mouse) {
        super.gameOver(view, world, update, keys, mouse);

        if (bgm != null) Audio.stopMusic(bgm);
        // Panel for if the player has lost
        if (player.health <= 0) {
            removeListeners(world, update);
            gameEnd("You couldn't get back home.");
        } else {
            Audio.playSound("data/sfx/level.wav");
        }
    }

    // Panel for if the player has won
    public void gameWon() {
        destroyWorld(world, update);
        frame.remove(view);
        frame.setCursor(Cursor.getDefaultCursor());

        gameEnd("You have escaped the facility!");
    }

    private void gameEnd(String text) {
        JPanel won = new JPanel();
        won.setBackground(background);
        won.setLayout(new BoxLayout(won, BoxLayout.Y_AXIS));
        JPanel over = new JPanel(new GridBagLayout());
        over.setBackground(background);

        JLabel title = new JLabel(text);
        title.setFont(new Font("Times New Roman", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        won.add(title);

        JButton menu = new JButton("Main Menu");
        menu.setFont(new Font("Roboto", Font.ITALIC, 24));
        menu.setPreferredSize(new Dimension(200, 72));
        menu.setAlignmentX(Component.CENTER_ALIGNMENT);
        menu.addActionListener(e -> {
            frame.remove(over);
            Menu.createMainMenuPanel();
            frame.revalidate();
            frame.repaint();
        });
        won.add(menu);

        over.add(won);
        frame.add(over);
        frame.revalidate();
        frame.repaint();
    }

    // Update the world to the new level
    public void changeLevel() {
        destroyWorld(world, update);

        world = levels[currLevel];
        world.getSimulationSettings().setTargetFrameRate(60);

        view = new StoryView(world, 1080, 720);
        view.setZoom(world.zoom);
        world.setView(view);
        view.setLevel(currLevel);

        cursor = new StaticBody(world);
        player = new Player(world, 99, 0, new Vec2(-2, 0), 300f, 2f, 60);
        view.setPlayer(player);

        enemies = new ArrayList<>();
        collectibles = world.collectibles;
        update = new StoryUpdate(this);
        killed = 0;

        playerCursor(cursor, player, world, view);
        worldBuilder(world.zoom);
        world.build();
        startEnemies();
        playMusic();

        Keys keys = new Keys(player, world, view, update);
        Mouse mouse = new Mouse(cursor, player, world, view, update);
        windowMaker(cursor, view, mouse, keys);
        frame.revalidate();
        frame.repaint();
        frame.requestFocus();

        player.removeAllDestructionListeners();
        player.addDestructionListener(d -> gameOver(view, world, update, keys, mouse));

        for (Collect c : collectibles) {
            c.obj.removeAllDestructionListeners();
            c.obj.addDestructionListener(ev -> {
                if (c instanceof Gun) {
                    player.redrawGun((Gun) c, update);
                }
            });
        }

        if (world instanceof BossLevel) {
            Boss boss = ((BossLevel) world).getBoss();
            boss.setView(view);
        }

        world.start();
    }
}