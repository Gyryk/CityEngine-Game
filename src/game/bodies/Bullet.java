package game.bodies;

import city.cs.engine.CircleShape;
import city.cs.engine.CollisionListener;
import city.cs.engine.World;
import org.jbox2d.common.Vec2;

import java.awt.*;

public class Bullet extends Projectile {
    public final int damage;

    // Constructor
    public Bullet(World w, Vec2 mousePos, Vec2 playerPos, CollisionListener bulletListener, float speed, int damage) {
        super(w, new CircleShape(0.3f), 0f, speed, mousePos, playerPos, bulletListener);
        setFillColor(Color.YELLOW);
        this.damage = damage;
    }

    // Spread Constructor
    public Bullet(World w, Vec2 playerPos, CollisionListener bulletListener, float speed, int damage, Vec2 dir) {
        super(w, new CircleShape(0.2f), 0f, speed, playerPos, bulletListener, dir);
        setFillColor(Color.YELLOW);
        this.damage = damage;
    }

    // Calculated Constructor
    public Bullet(World w, CollisionListener bulletListener, float radius, float mass, float speed, int damage, Vec2 dir, Vec2 offset) {
        super(w, new CircleShape(radius), mass, speed, bulletListener, dir, offset);
        setFillColor(Color.MAGENTA);
        this.damage = damage;
    }
}
