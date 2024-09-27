package game.bodies;

import city.cs.engine.CollisionListener;
import city.cs.engine.DynamicBody;
import city.cs.engine.Shape;
import city.cs.engine.World;
import org.jbox2d.common.Vec2;

public class Projectile extends DynamicBody {
    // Constructor
    public Projectile(World w, Shape shape, float mass, float speed, Vec2 endPos, Vec2 startPos, CollisionListener listener) {
        super(w, shape);
        setGravityScale(mass);
        Vec2 dir = calculateDir(startPos, endPos);
        Vec2 offset = startPos.add(dir.mul(4f));

        setBullet(true);
        setPosition(offset);
        setLinearVelocity(dir.mul(speed));
        setName("projectile");

        addCollisionListener(listener);
    }

    // Shotgun Pellet Constructor
    public Projectile(World w, Shape shape, float mass, float speed, Vec2 originPos, CollisionListener listener, Vec2 dir) {
        super(w, shape);
        setGravityScale(mass);
        Vec2 offset = originPos.add(dir.mul(4f));

        setPosition(offset);
        setLinearVelocity(dir.mul(speed));
        setName("projectile");

        addCollisionListener(listener);
    }

    // Enemy Bullet Constructor
    public Projectile(World w, Shape shape, float mass, float speed, CollisionListener listener, Vec2 dir, Vec2 offset) {
        super(w, shape);
        setGravityScale(mass);

        setPosition(offset);
        setLinearVelocity(dir.mul(speed));
        setName("projectile");

        addCollisionListener(listener);
    }

    public static Vec2 calculateDir(Vec2 originPos, Vec2 targetPos) {
        Vec2 dir = targetPos.sub(originPos);
        dir.normalize();
        return dir;
    }
}
