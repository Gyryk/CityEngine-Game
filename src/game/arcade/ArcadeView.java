package game.arcade;

import city.cs.engine.World;
import game.View;
import game.bodies.Enemy;
import game.bodies.Player;
import game.master.GameMath;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ArcadeView extends View {
    private final ArrayList<Enemy> enemies;
    private float score;
    private Player player;

    public ArcadeView(World w, int width, int height) {
        super(w, width, height);
        score = 0;
        enemies = new ArrayList<>();
        initializeTextFadeTimer();
    }

    public void addEnemy(Enemy e) {
        enemies.add(e);
    }

    public void addScore(float s) {
        score += s;
    }

    public void setPlayer(Player p) {
        super.setPlayer(p);
        player = p;
    }

    public int getScore() {
        return (int) score;
    }

    @Override
    protected void paintForeground(Graphics2D g) {
        super.paintForeground(g);
        FontMetrics metrics = g.getFontMetrics();

        // Ammo bar through icon
        Image ammo = new ImageIcon("data/ammo.png").getImage();
        g.setPaint(Color.YELLOW);
        int width = GameMath.mapRange(player.ammo, 0, player.maxAmmo, 3.5f, 80);
        g.fillRect(getWidth() - 110, 270, width, 80);
        g.drawImage(ammo, getWidth() - 110, 270, 80, 80, this);

        enemyHealth(enemies, g);

        // Show player's score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Roboto", Font.BOLD, 20));
        g.drawString("Score:", getWidth() - metrics.stringWidth("Score:") - 40, getHeight() - 70);
        g.drawString(String.valueOf((int) score), getWidth() - metrics.stringWidth(String.valueOf((int) score)) - 40, getHeight() - 40);
    }

    @Override
    protected void paintBackground(Graphics2D g) {
        setBackground(new Color(20, 20, 20));
        super.paintBackground(g);
    }
}
