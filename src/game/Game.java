package game;

import city.cs.engine.Shape;
import city.cs.engine.*;
import game.arcade.ArcadeView;
import game.arcade.ScoreWriter;
import game.bodies.Enemy;
import game.bodies.Player;
import game.master.Audio;
import game.survival.TimeWriter;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static game.Menu.frame;

/**
 * Your main game entry point
 */
public class Game {
    public static final HashMap<String, float[]> enemyTypes = new HashMap<>();
    public static final HashMap<String, float[]> gunTypes = new HashMap<>();
    public static final HashMap<String, String[]> gunImages = new HashMap<>();

    public static final ArrayList<Collect> collectibles = new ArrayList<>();
    static final World world = new World();
    public static ArrayList<Enemy> enemies = new ArrayList<>();
    public static Body cursor = new StaticBody(world);
    static View view;
    static Player player;
    //    static Update update;
    private final Color background = new Color(20, 20, 20);

    /**
     * Initialise a new Game.
     */
    public Game() {
        enemyTypes.put("small", new float[]{2f, 3f, 3f, 5, 10, 50, 70});
        enemyTypes.put("normal", new float[]{3f, 3f, 5f, 5, 20, 90, 50});
        enemyTypes.put("big", new float[]{4f, 2f, 7f, 10, 40, 140, 30});
        enemyTypes.put("fast", new float[]{3f, 4f, 5f, 3, 5, 20, 90});
        enemyTypes.put("tank", new float[]{2f, 1f, 3f, 9, 80, 200, 10});

        gunTypes.put("pistol", new float[]{1.5f, 1.1f, 5, 50f, 0.2f, 1});
        gunTypes.put("shotgun", new float[]{4f, 1f, 7, 40f, 0.8f, 3});
        gunTypes.put("rifle", new float[]{4f, 1.5f, 10, 80f, 0.1f, 2});
        gunTypes.put("sniper", new float[]{4.9f, 1f, 30, 100f, 1f, 5});
        gunTypes.put("knife", new float[]{1.75f, 0.5f, 25, 15f, 0.4f, 0});

        gunImages.put("pistol", new String[]{"data/guns/M1911.png", "data/guns/handgun.png"});
        gunImages.put("shotgun", new String[]{"data/guns/BAR.png", "data/guns/shotgun.png"});
        gunImages.put("rifle", new String[]{"data/guns/StG44.png", "data/guns/rifle.png"});
        gunImages.put("sniper", new String[]{"data/guns/SVT-40.png", "data/guns/sniper.png"});
        gunImages.put("knife", new String[]{"data/guns/Bayonet.png", "data/guns/knife.png"});

        //1. make an empty game world
        world.getSimulationSettings().setTargetFrameRate(60);
//        world.setGravity(0f);
//        update = new Update();

        //2. make a view to look into the game world
        view = new View(world, 1080, 720);
        view.setZoom(10f);
        frame.requestFocus();

        //3. populate it with bodies (ex: platforms, collectibles, characters)
        //make the player
        player = new Player(world, 99, 15, new Vec2(-2, -5), 300f, 2f, 60);
        view.setPlayer(player);
        playerCursor(cursor, player, world, view);
//        worldBuilder(world, update);

        //4. create a Java window (frame) and add the game view to it
//        windowMaker(cursor, view, new Mouse(cursor, player, world, view, update), new Keys(player, world, view));

        // 5. start our game world simulation!
        world.start();
    }

    // Add the gun to the player's inventory and remove it from the game world
    public static void collectGun(Gun cGun, Player player, UserView view) {
        Audio.playSound("data/sfx/gun.wav");
        cGun.obj.destroy();
        player.inventory.add(cGun);

        // Equip the new gun
        if (!player.sniping) {
            player.equipped = player.inventory.indexOf(cGun);
            player.useAmmo = cGun.ammo;
            player.damage = cGun.damage;

            player.lineRange = cGun.range;
            player.redrawLine(cGun.spread, view.getZoom());
        }
        if (view instanceof ArcadeView) {
            ((ArcadeView) view).addScore(5);
        }
        collectibles.remove(cGun);
    }

    public static JTextField textField(String hint) {
        JTextField name = new JTextField(hint);
        name.setFont(new Font("Roboto", Font.ITALIC, 24));
        name.setForeground(Color.GRAY);
        name.setPreferredSize(new Dimension(200, 72));
        name.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (name.getText().equals(hint)) {
                    name.setText("");
                    name.setForeground(Color.BLACK); // Change text color to black when focused
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (name.getText().isEmpty()) {
                    name.setForeground(Color.GRAY); // Restore hint text color when unfocused
                    name.setText(hint);
                }
            }
        });

        return name;
    }

    // Code for cursor related objects that interact with the game
    protected void playerCursor(Body cursor, Player player, World world, View view) {
        // make a cursor object
        CircleShape cursorShape = new CircleShape(10f / view.getZoom());
        cursor.setPosition(new Vec2(0, 0));
        cursor.setFillColor(new Color(255, 255, 255, 20));
        cursor.setLineColor(new Color(0, 0, 0, 0));
        new GhostlyFixture(cursor, cursorShape, 0);

        // draw a line from player to cursor
        player.line = new StaticBody(world);
        player.line.setPosition(new Vec2(0, 0));
        player.lineColor = new Color(0, 0, 0, 0);
        player.line.setFillColor(player.lineColor);
        player.line.setLineColor(new Color(0, 0, 0, 0));
        new GhostlyFixture(player.line, new BoxShape(1f / view.getZoom(), player.lineRange / view.getZoom()), 0);

        // hide the cursor
        frame.setCursor(Menu.frame.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB),
                new Point(0, 0), "null"));

    }

    // Make the window for the game using JFrame and handle mouse and keyboard inputs
    protected void windowMaker(Body cursor, UserView view, MouseAdapter mouse, KeyListener keys) {
        frame.add(view);
        //TODO: Figure out why this is causing issues with the levels
//        frame.revalidate();
//        frame.repaint();
//        JFrame debugView = new DebugViewer(world, 1080, 720); //optional: uncomment this to make a debugging view

        // Check for mouse clicks
        frame.addMouseListener(mouse);

        // Getting mouse position
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Vec2 cursorPos = view.viewToWorld(e.getPoint());
                cursorPos.y += 30 / view.getZoom();
                cursor.setPosition(cursorPos);
            }
        });

        // Catch user's keyboard inputs
        frame.addKeyListener(keys);
    }

    // Make the ground, ceiling, walls and add a step listener to the world
    protected void worldBuilder(World world, StepListener steps) {
        // make ground, ceiling, and walls
        Shape shape = new BoxShape(40, 1f);
        new Platform(world, shape, new Vec2(0f, 31f), Color.DARK_GRAY);
        new Platform(world, shape, new Vec2(0f, -31f), Color.DARK_GRAY);

        Shape wallShape = new BoxShape(1f, 31);
        new Platform(world, wallShape, new Vec2(-39, 0f), Color.DARK_GRAY);
        new Platform(world, wallShape, new Vec2(39, 0f), Color.DARK_GRAY);

        // Update game state to draw accurate lines and check for collider intersections
        world.addStepListener(steps);
    }

    // Remove the game from the screen to allow for a game over menu
    protected void gameOver(UserView view, World world, Update update, KeyListener keys, MouseAdapter mouse) {
        world.stop();
        world.removeStepListener(update);
        frame.remove(view);
        // show the cursor
        frame.setCursor(Cursor.getDefaultCursor());
    }

    // Show game over menu
    protected void scoredGameEnd(UserView view, World world, Update update, KeyListener keys, MouseAdapter mouse, String scored,
                                 boolean arcade, int points) {
        gameOver(view, world, update, keys, mouse);
        removeListeners(world, update);

        for (Body b : world.getStaticBodies()) {
            b.destroy();
        }
        for (Body b : world.getDynamicBodies()) {
            if (!Objects.equals(b.getName(), "player")) b.destroy();
        }

        JPanel over = new JPanel();
        over.setBackground(background);
        over.setLayout(new BoxLayout(over, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Game Over");
        title.setFont(new Font("Times New Roman", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel score = new JLabel(scored);
        score.setFont(new Font("Roboto", Font.BOLD, 36));
        score.setForeground(Color.WHITE);
        score.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel namePanel = new JPanel(new GridBagLayout());
        namePanel.setBackground(background);
        JTextField name = textField("Your Name");
        namePanel.add(name);

        JButton submit = new JButton("Submit");
        submit.addActionListener(e -> {
            if (arcade) ScoreWriter.writeScoreToFile(new ScoreWriter.ScoreData(name.getText(), points));
            else TimeWriter.writeTimeToFile(new TimeWriter.TimeData(name.getText(), points));
            frame.remove(over);
            Menu.createMainMenuPanel();

        });
        submit.setFont(new Font("Roboto", Font.ITALIC, 24));
        submit.setPreferredSize(new Dimension(200, 72));
        namePanel.add(submit);

        over.add(title);
        over.add(score);
        over.add(namePanel);

        frame.add(over);
        frame.revalidate();
        frame.repaint();
    }

    // Stop the world and remove all bodies and listeners from it
    protected void destroyWorld(World world, Update update) {
        world.stop();
        removeListeners(world, update);

        for (Body b : world.getStaticBodies()) {
            b.destroy();
        }
        for (Body b : world.getDynamicBodies()) {
            b.destroy();
        }
    }

    // Remove all the listeners from the frame and world
    protected void removeListeners(World world, Update update) {
        world.removeStepListener(update);
        for (KeyListener k : frame.getKeyListeners()) {
            frame.removeKeyListener(k);
        }
        for (MouseListener m : frame.getMouseListeners()) {
            frame.removeMouseListener(m);
        }
        for (MouseMotionListener m : frame.getMouseMotionListeners()) {
            frame.removeMouseMotionListener(m);
        }
    }
}

//TODO MINOR:
// timer for player shooting (gunTimer, canShoot) to avoid spamming [pistol: 300ms, sniper: 2000ms, shotgun: 1000ms, rifle: 500ms, knife: 100ms]
// more types of collectibles, power ups, mag size, deflector etc; bow and arrow, grenades or proximity mines. bullet impact sounds?
// make the damage number add instead of replacing if the enemy is the same and the number hasn't vanished completely
// replace magic numbers with constants and encapsulate fields, fix excessive use of static fields and methods
// sub menus like settings with navigation; using Layout Managers? figure out pause menu for the game
// when you have multiple bosses in the game the health bar stays till you kill all of them
// add a full auto smg with 10rps and 3 damage? fsm? move the camera around and scroll?
// save and load feature for story mode (save in pause menu and load in main)
