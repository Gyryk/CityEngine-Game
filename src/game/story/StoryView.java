package game.story;

import city.cs.engine.World;
import game.View;
import game.bodies.Enemy;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static game.story.GameStory.currLevel;
import static game.story.GameStory.levels;

public class StoryView extends View {
    private final ArrayList<Enemy> enemies;
    private int level = 0;

    public StoryView(World w, int width, int height) {
        super(w, width, height);
        enemies = new ArrayList<>();
        initializeTextFadeTimer();
    }

    public void addEnemy(Enemy e) {
        enemies.add(e);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    protected void paintForeground(Graphics2D g) {
        super.paintForeground(g);
        enemyHealth(enemies, g);

        FontMetrics metrics = g.getFontMetrics();

        g.setFont(new Font("Roboto", Font.BOLD, 32));
        g.setColor(Color.WHITE);
        String level = "Level " + (currLevel + 1);
        String text = "\"" + levels[currLevel].text + "\"";
        g.setFont(new Font("Roboto", Font.ITALIC, 32));
        g.drawString(text, (float) (getWidth() / 2) - (metrics.stringWidth(text) * 1.25f), getHeight() - 20);
        if (!(levels[currLevel] instanceof BossLevel))
            g.drawString(level, getWidth() / 2 - metrics.stringWidth(level), 40);
        else bossHealth(g);
    }

    @Override
    protected void paintBackground(Graphics2D g) {
        setBackground(new Color(20, 20, 20));
        super.paintBackground(g);
        switch (level) {
            case 0:
                g.drawImage(new ImageIcon("data/level1.png").getImage(), 160, 80, getWidth() - 320,
                        getHeight() - 160, null);
                break;
            case 1:
                g.drawImage(new ImageIcon("data/level2.png").getImage(), 150, 70, getWidth() - 300,
                        getHeight() - 140, null);
                break;
            case 2:
                g.drawImage(new ImageIcon("data/level3.jpeg").getImage(), 150, 70, getWidth() - 300,
                        getHeight() - 140, null);
                break;
            case 3:
                g.drawImage(new ImageIcon("data/level4.gif").getImage(), 160, 60, getWidth() - 320,
                        getHeight() - 120, null);
                break;

        }

    }
}
