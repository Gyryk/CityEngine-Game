package game.survival;

import city.cs.engine.World;
import game.View;
import game.bodies.Enemy;

import java.awt.*;
import java.util.ArrayList;

import static game.survival.GameSurvival.bossAlive;

public class SurvivalView extends View {
    private final ArrayList<Enemy> enemies;
    public int millis;
    private String time;

    public SurvivalView(World w, int width, int height) {
        super(w, width, height);
        enemies = new ArrayList<>();
        initializeTextFadeTimer();
    }

    public void addEnemy(Enemy e) {
        enemies.add(e);
    }

    @Override
    protected void paintForeground(Graphics2D g) {
        super.paintForeground(g);
        enemyHealth(enemies, g);

        FontMetrics metrics = g.getFontMetrics();
        time = TimeWriter.convert(millis);

        // Time passed so far
        g.setColor(Color.WHITE);
        g.setFont(new Font("Roboto", Font.BOLD, 20));
        g.drawString("Survived:", getWidth() - metrics.stringWidth("Survived:") - 50, getHeight() - 70);
        g.drawString(time, getWidth() - metrics.stringWidth(time) - 50, getHeight() - 40);

        if (bossAlive) {
            bossHealth(g);
        }
    }

    @Override
    protected void paintBackground(Graphics2D g) {
        setBackground(new Color(20, 20, 20));
        super.paintBackground(g);
    }
}
