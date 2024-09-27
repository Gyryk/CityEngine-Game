package game.bodies;

import city.cs.engine.CircleShape;
import city.cs.engine.SolidFixture;
import city.cs.engine.World;
import game.View;
import game.master.Audio;
import org.jbox2d.common.Vec2;

import java.awt.*;
import java.awt.geom.Point2D;

public class Brute extends Ball {
    private final View view;

    public Brute(float radius, int damage, float max, Vec2 pos, World world, View view) {
        super(world, radius, 5, damage, max, pos);
        this.damage = damage;
        this.view = view;

        setName("enemy");
        setFillColor(Color.RED);
        setLineColor(new Color(0, 0, 0, 0));
        SolidFixture fix = new SolidFixture(this, new CircleShape(radius));
        fix.setRestitution(1f);

        addCollisionListener(collisionEvent -> {
            if (collisionEvent.getOtherBody() instanceof Player p) {
                p.damage(damage);
                destroy();
                Audio.playSound("data/sfx/die.wav");
            }
        });
    }

    // Damage the enemy and kill if health below 0
    @Override
    public void damage(int amount) {
        super.damage(amount);
        Point2D.Float damagePos = view.worldToView(getPosition());
        damagePos.y = damagePos.y < (float) view.getHeight() / 2 ? damagePos.y + 64 : damagePos.y - 64;

        view.setDamageText(String.valueOf(amount), damagePos);
    }
}
