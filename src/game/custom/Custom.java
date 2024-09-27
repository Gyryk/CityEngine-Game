package game.custom;

import city.cs.engine.Shape;
import city.cs.engine.*;
import game.Menu;
import game.*;
import game.master.Audio;
import game.master.LevelLoader;
import org.jbox2d.common.Vec2;

import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static game.Game.textField;
import static game.Menu.background;
import static game.Menu.frame;

public class Custom {
    static CustomView view;
    static ArrayList<Object> objects = new ArrayList<>();
    static int equipped = 0;
    static Vec2 mousePos;
    static Body cursor;
    static ArrayList<Platform> platforms = new ArrayList<>();
    JPanel customPanel;
    World world;
    StaticBody drag = null;
    Clip music;

    // Constructor
    public Custom() {
        music = Audio.loopMusic("data/music/lobby-time.wav", 0.75f);

        world = new World();
        world.getSimulationSettings().setTargetFrameRate(60);

        view = new CustomView(world, 1080, 720);
        view.setZoom(10f);
        frame.requestFocus();

        worldBuilder();
        windowMaker();
        createCursor();

        world.start();

        objects.add(new Object(Object.Type.SHORT));
        objects.add(new Object(Object.Type.PLATFORM));
        objects.add(new Object(Object.Type.WIDE));
        objects.add(new Object(Object.Type.VANISH));
        objects.add(new Object(Object.Type.ROTATE));
        objects.add(new Object(Object.Type.TRAMPOLINE));
    }

    public static Vec2 clampedVec2(Vec2 vec2, float minX, float minY, float maxX, float maxY) {
        Vec2 res = new Vec2(Math.min(Math.max(vec2.x, minX), maxX), Math.min(Math.max(vec2.y, minY), maxY));
        res.x = Math.round(res.x);
        res.y = Math.round(res.y);
        return res;
    }

    // Create a world for the game that mimics arcade mode
    void worldBuilder() {
        Shape wallShape = new BoxShape(1f, 31);
        new Platform(world, wallShape, new Vec2(-39, 0f), Color.DARK_GRAY);
        new Platform(world, wallShape, new Vec2(39, 0f), Color.DARK_GRAY);

        Shape shape = new BoxShape(40, 1f);
        new Platform(world, shape, new Vec2(0f, 31f), Color.DARK_GRAY);
        new Platform(world, shape, new Vec2(0f, -31f), Color.DARK_GRAY);

        world.addStepListener(new CustomUpdate());
    }

    // Add input listeners to the frame so that the player can add elements
    void windowMaker() {
        frame.add(view);
        frame.revalidate();
        frame.repaint();

        frame.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    mousePos = view.viewToWorld(e.getPoint());
                    mousePos.y += 2.5f;
                    drag = checkIntersection();
                    if (drag == null) {
                        Object object = objects.get(equipped);
                        switch (object.getType()) {
                            case PLATFORM -> {
                                mousePos = clampedVec2(mousePos, -33f, -29f, 33f, 29f);
                                Platform platform = new Platform(world, new Vec2(5f, 1f), mousePos, object.getImage(), "static");
                                platforms.add(platform);
                            }
                            case WIDE -> {
                                mousePos = clampedVec2(mousePos, -28f, -29f, 28f, 29f);
                                Platform platform = new Platform(world, new Vec2(10f, 1f), mousePos, object.getImage(), "static");
                                platforms.add(platform);
                            }
                            case TRAMPOLINE -> {
                                mousePos = clampedVec2(mousePos, -33f, -29f, 33f, 28f);
                                Trampoline trampoline = new Trampoline(world, new Vec2(4.8f, 2f), mousePos);
                                platforms.add(trampoline);
                            }
                            case SHORT -> {
                                mousePos = clampedVec2(mousePos, -36f, -29f, 36f, 29f);
                                Platform platform = new Platform(world, new Vec2(1.8f, 1f), mousePos, object.getImage(), "static");
                                platforms.add(platform);
                            }
                            case ROTATE -> {
                                mousePos = clampedVec2(mousePos, -33f, -29f, 33f, 29f);
                                Rotate rotate = new Rotate(world, new Vec2(5f, 1f), mousePos);
                                platforms.add(rotate);
                            }
                            case VANISH -> {
                                mousePos = clampedVec2(mousePos, -33f, -29f, 33f, 29f);
                                Vanish vanish = new Vanish(world, new Vec2(5f, 1f), mousePos);
                                platforms.add(vanish);
                            }
                        }

                    }
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    StaticBody object = checkIntersection();
                    if (object != null) {
                        if (object instanceof Platform) {
                            platforms.remove(object);
                            object.destroy();
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (drag != null) {
                        drag.setPosition(mousePos);
                    }
                    drag = null;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char keyChar = e.getKeyChar();

                // check if the key pressed is a number and if it is, equip that weapon
                if (Character.isDigit(keyChar) && keyChar != '0') {
                    int inx = Integer.parseInt(String.valueOf(keyChar)) - 1;
                    if (inx < objects.size()) equipped = inx;
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Audio.stopMusic(music);
                    frame.remove(view);
                    frame.setCursor(Cursor.getDefaultCursor());
                    customiseSettings();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    void createCursor() {
        cursor = new StaticBody(world);
        cursor.setFillColor(new Color(255, 255, 255, 155));
        cursor.setLineColor(new Color(0, 0, 0, 0));
        new GhostlyFixture(cursor, new BoxShape(1f, 1f));

        frame.setCursor(Menu.frame.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB),
                new Point(0, 0), "null"));
    }

    StaticBody checkIntersection() {
        for (StaticBody body : world.getStaticBodies()) {
            if (body == cursor) continue;
            if (cursor.intersects(body)) {
                return body;
            }
        }

        return null;
    }

    JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 24));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    public void customiseSettings() {
        // Define sliders for different enemy types and text field for spawner
        JSlider tankSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 5);
        JSlider fastSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
        JSlider bigSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 15);
        JSlider smallSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 20);
        JSlider normalSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);

        JSlider[] sliders = {tankSlider, fastSlider, bigSlider, smallSlider, normalSlider};
        for (JSlider slider : sliders) {
            slider.setMajorTickSpacing(20);
            slider.setMinorTickSpacing(5);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setBackground(Color.WHITE);
            slider.setForeground(Color.WHITE);
            slider.setPreferredSize(new Dimension(80, 200));
        }

        JTextField spawn = textField("5");

        // Targets for weapons and slider for drops
        JTextField targetsField = textField("0, 1, 3, 7, 15");
        targetsField.setFont(new Font("Arial", Font.PLAIN, 24));

        JSlider drops = new JSlider(JSlider.HORIZONTAL, 0, 100, 85);
        drops.setPreferredSize(new Dimension(200, 32));
        drops.setUI(new BasicSliderUI(drops) {
            @Override
            public void paintTrack(Graphics g) {
                super.paintTrack(g);
                g.setColor(Color.GRAY);
                g.fillRect(trackRect.x, trackRect.y, trackRect.width, trackRect.height);
            }
        });


        JPanel verticalPanel = new JPanel();
        verticalPanel.setBackground(background);
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));

        customPanel = new JPanel(new BorderLayout());
        customPanel.setBackground(background);

        JLabel gameTitle = new JLabel("Play it your way!");
        gameTitle.setFont(new Font("Arial", Font.BOLD, 48));
        gameTitle.setForeground(Color.WHITE);
        gameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel settingsPanel = new JPanel(new GridLayout(2, 5));
        settingsPanel.setBackground(background);

        settingsPanel.add(tankSlider);
        settingsPanel.add(fastSlider);
        settingsPanel.add(bigSlider);
        settingsPanel.add(smallSlider);
        settingsPanel.add(normalSlider);

        JLabel tankLabel = label("Tank");
        JLabel fastLabel = label("Fast");
        JLabel bigLabel = label("Big");
        JLabel smallLabel = label("Small");
        JLabel normalLabel = label("Normal");

        JPanel tankFlow = new JPanel(new FlowLayout());
        tankFlow.setBackground(background);
        JPanel fastFlow = new JPanel(new FlowLayout());
        fastFlow.setBackground(background);
        JPanel bigFlow = new JPanel(new FlowLayout());
        bigFlow.setBackground(background);
        JPanel smallFlow = new JPanel(new FlowLayout());
        smallFlow.setBackground(background);
        JPanel normalFlow = new JPanel(new FlowLayout());
        normalFlow.setBackground(background);

        tankFlow.add(tankLabel);
        fastFlow.add(fastLabel);
        bigFlow.add(bigLabel);
        smallFlow.add(smallLabel);
        normalFlow.add(normalLabel);

        settingsPanel.add(tankFlow);
        settingsPanel.add(fastFlow);
        settingsPanel.add(bigFlow);
        settingsPanel.add(smallFlow);
        settingsPanel.add(normalFlow);

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(background);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // margins for better spacing

        JLabel dropLabel = label("Ammo enemy-drop %age");
        JLabel spawnLabel = label("Enemy spawn rate (seconds)");

        bottomPanel.add(dropLabel, gbc);
        bottomPanel.add(drops, gbc);
        bottomPanel.add(spawnLabel, gbc);
        bottomPanel.add(spawn, gbc);

        // Text field for targets array
        JLabel targetsLabel = label("Weapon Target values:");
        bottomPanel.add(targetsLabel, gbc);
        bottomPanel.add(targetsField, gbc);

        // Save the data to a json and txt file
        JPanel namePanel = new JPanel(new GridLayout(1, 4));
        namePanel.setBackground(background);
        JTextField name = textField("File Name");
        name.setSize(new Dimension(200, 50));
        namePanel.add(Box.createRigidArea(new Dimension(200, 10)));
        namePanel.add(name);

        JButton save = new JButton("Save");
        save.addActionListener(e -> {
            // Save the level to a json file
            String fileName = name.getText();
            if (fileName.isEmpty()) fileName = "custom";
            String jsonPath = "data/levels/" + fileName + ".json";
            String txtPath = "data/levels/" + fileName + ".txt";
            LevelLoader.writeJSON(jsonPath, platforms, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            SettingsWriter.writeSettings(txtPath, new int[]{tankSlider.getValue(), fastSlider.getValue(), bigSlider.getValue(),
                            smallSlider.getValue(), normalSlider.getValue()}, Integer.parseInt(spawn.getText()), drops.getValue(),
                    targetsField.getText().split(", "));
            frame.remove(customPanel);
            Menu.createMainMenuPanel();

        });
        save.setFont(new Font("Roboto", Font.ITALIC, 24));
        save.setPreferredSize(new Dimension(200, 72));
        namePanel.add(save);
        namePanel.add(Box.createRigidArea(new Dimension(200, 10)));

        verticalPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        verticalPanel.add(gameTitle);
        verticalPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        verticalPanel.add(settingsPanel);
        verticalPanel.add(bottomPanel);
        verticalPanel.add(namePanel);
        verticalPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        customPanel.add(verticalPanel, BorderLayout.CENTER);

        frame.add(customPanel);
        frame.revalidate();
        frame.repaint();
    }
}
