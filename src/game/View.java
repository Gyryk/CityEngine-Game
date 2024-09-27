package game;

import city.cs.engine.UserView;
import city.cs.engine.World;
import game.bodies.Enemy;
import game.bodies.Player;
import game.master.GameMath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class View extends UserView {
    private Player player;
    private String damageText = "";
    private Point2D.Float damageTextPosition = new Point2D.Float(0, 0);
    private float textAlpha = 1.0f;
    private Timer textFadeTimer;
    private int bossHealth;

    // Constructor
    public View(World w, int width, int height) {
        super(w, width, height);
        initializeTextFadeTimer();
        bossHealth = 999;
    }

    // Setters and Getters
    public void setBossHealth(int health) {
        bossHealth = health;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player p) {
        player = p;
    }

    public void setDamageText(String text, Point2D.Float position) {
        damageText = text;
        damageTextPosition = position;
        textAlpha = 1.0f;
        startTextFadeTimer();
    }

    protected void initializeTextFadeTimer() {
        int fadeDelay = 120;
        ActionListener fadeAction = e -> {
            // Reduce the alpha value gradually
            textAlpha -= 0.1f;
            if (textAlpha <= 0.0f) {
                // Stop the timer when the text is fully faded out
                stopTextFadeTimer();
            }
            repaint();
        };
        textFadeTimer = new Timer(fadeDelay, fadeAction);
        textFadeTimer.setRepeats(true);
    }

    // Start fading the new text and stop fading the old one
    protected void startTextFadeTimer() {
        stopTextFadeTimer();
        textFadeTimer.start();
    }

    protected void stopTextFadeTimer() {
        textFadeTimer.stop();
    }

    @Override
    protected void paintForeground(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, (int) (255 * textAlpha))); // Set text color with adjusted alpha
        g.drawString(damageText, damageTextPosition.x, damageTextPosition.y);

        // Show player health bar through heart icon
        Image heart = new ImageIcon("data/heart.png").getImage();
        g.setPaint(Color.RED);
        int height = GameMath.mapRange(player.health, 0, player.maxHealth, 7, 73);
        int top = 120 - height;
        g.fillRect(getWidth() - 110, top, 80, height);
        g.drawImage(heart, getWidth() - 110, 40, 80, 80, this);

        //Show player dash status through boost icon
        Image boost = new ImageIcon("data/boost.png").getImage();
        g.setPaint(Color.GREEN);
        int width = GameMath.mapRange(player.timer, 0, player.delay, 0, 80);
        g.fillRect(getWidth() - 110, 160, width, 80);
        g.drawImage(boost, getWidth() - 110, 160, 80, 80, this);

        // Loop through the player's inventory and draw the icons
        for (int i = 0; i < player.inventory.size(); i++) {
            Collect gun = player.inventory.get(i);
            if (i == player.equipped) {
                g.setColor(new Color(0, 0, 0, 0));
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(String.valueOf(i + 1), 100, 60 + (i * 90));

            Image img = new ImageIcon(gun.getIcon()).getImage();
            g.drawImage(img, 30, 30 + (i * 90), 60, 60, this);
        }

        super.paintForeground(g);
    }

    @Override
    protected void paintBackground(Graphics2D g) {
        setBackground(new Color(20, 20, 20));
        super.paintBackground(g);
    }

    // Loop through the list of enemies and show their current health if not currently sniping
    public void enemyHealth(ArrayList<Enemy> enemies, Graphics2D g) {
        FontMetrics metrics = g.getFontMetrics();

        // Loop through the list of enemies and show their current health if not currently sniping
        if (!player.sniping) {
            for (Enemy enemy : enemies) {
                Point2D.Float healthPos = worldToView(enemy.getPosition());
                int textWidth = metrics.stringWidth(String.valueOf(enemy.health));
                if (enemy.health > 0) {
                    g.setColor(Color.WHITE);
                    // Calculate the position for center alignment of texts
                    int x = (int) (healthPos.x - textWidth / 2);
                    int y = (int) (healthPos.y + (metrics.getHeight() / 3));

                    g.drawString(String.valueOf(enemy.health), x, y);
                }
            }
        }
    }

    // Show the health bar for the boss
    public void bossHealth(Graphics2D g) {
        g.setPaint(Color.WHITE);
        g.drawRect(getWidth() / 2 - 300, 16, 600, 20);
        g.setPaint(Color.CYAN);
        int width = GameMath.mapRange(bossHealth, 0, 999, 0, 600);
        g.fillRect(getWidth() / 2 - 300, 16, width, 20);
    }
}
