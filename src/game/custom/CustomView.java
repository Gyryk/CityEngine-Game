package game.custom;

import city.cs.engine.UserView;
import city.cs.engine.World;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;

import static game.custom.Custom.equipped;
import static game.custom.Custom.objects;

public class CustomView extends UserView {
    public CustomView(World w, int width, int height) {
        super(w, width, height);
        setCentre(new Vec2(-10, 0));
    }

    @Override
    protected void paintForeground(Graphics2D g) {
        super.paintForeground(g);

        // Draw the grid
        int x1Lim = 280;
        int x2Lim = getWidth() - 80;
        int y1Lim = 66;
        int y2Lim = getHeight() - 66;
        int res = 20;
        g.setColor(new Color(255, 255, 255, 100));
        for (int i = x1Lim; i <= x2Lim; i += res) {
            g.drawLine(i, y1Lim - res, i, y2Lim + res);
        }
        for (int i = y1Lim; i <= y2Lim; i += res) {
            g.drawLine(x1Lim - res, i, x2Lim + res, i);
        }

        // Loop through the available objects and draw them
        for (int i = 0; i < objects.size(); i++) {
            Object object = objects.get(i);
            if (i == equipped) {
                g.setColor(new Color(0, 0, 0, 0));
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(String.valueOf(i + 1), 200, 100 + (i * 100));

            Image img = new ImageIcon(object.getIcon()).getImage();
            g.drawImage(img, 40, 40 + (i * 100), 120, 120, this);
        }
    }

    @Override
    protected void paintBackground(Graphics2D g) {
        super.paintBackground(g);
        setBackground(new Color(20, 20, 20));
    }
}
