package game.bodies;

import city.cs.engine.*;
import game.View;
import org.jbox2d.common.Vec2;

import java.awt.*;
import java.awt.geom.Point2D;

public class Enemy extends Ball {
    public final StaticBody line;
    public final String type;
    public final float range;
    public final float offset;
    public final Vec2 pos;
    public final float delay;
    public float timer;
    private View view;

    // Constructor
    public Enemy(String type, float radius, float range, float offset, int damage, int health, float delay, float max, World world, Vec2 pos) {
        super(world, radius, health, damage, max, pos);
        this.type = type;
        this.range = range;
        this.offset = offset;
        this.delay = delay;
        this.timer = delay;
        this.pos = pos;

        setName("enemy");
        setFillColor(Color.RED);
        setLineColor(new Color(0, 0, 0, 0));
        SolidFixture fix = new SolidFixture(this, new CircleShape(radius));
        fix.setRestitution(1.01f);

        line = new StaticBody(world);
        BoxShape lineShape = new BoxShape(0.1f, range);
        new GhostlyFixture(line, lineShape, 0);
        line.setFillColor(new Color(255, 0, 0, 75));
        line.setLineColor(new Color(0, 0, 0, 0));
    }

    // Set the view
    public void setView(View view) {
        this.view = view;
    }

    // Damage the enemy and kill if health below 0
    @Override
    public void damage(int amount) {
        super.damage(amount);

        Point2D.Float damagePos = view.worldToView(getPosition());
        damagePos.y = damagePos.y < (float) view.getHeight() / 2 ? damagePos.y + 50 : damagePos.y - 50;

        view.setDamageText(String.valueOf(amount), damagePos);
    }
}
