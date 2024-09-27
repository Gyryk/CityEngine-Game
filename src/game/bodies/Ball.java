package game.bodies;

import city.cs.engine.CircleShape;
import city.cs.engine.DynamicBody;
import city.cs.engine.World;
import game.master.Audio;
import game.master.GameMath;
import org.jbox2d.common.Vec2;

public class Ball extends DynamicBody {
    public final float maxSpeed;
    public int health;
    public int damage;
    public float radius;

    // Constructor
    public Ball(World w, float radius, int health, int damage, float maxSpeed, Vec2 pos) {
        super(w, new CircleShape(radius));
        this.radius = radius;
        this.health = health;
        this.damage = damage;
        this.maxSpeed = maxSpeed;
        setPosition(pos);
    }

    // Take damage
    protected void damage(int amount) {
        health -= amount;
        if (health <= 0) {
            destroy();
            Audio.playSound("data/sfx/die.wav");
        }
    }

    // Cap speeds
    public void limitVelocity() {
        setLinearVelocity(GameMath.capVelocity(getLinearVelocity(), maxSpeed));
    }
}
