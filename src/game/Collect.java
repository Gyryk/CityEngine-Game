package game;

import city.cs.engine.*;
import game.bodies.Player;
import org.jbox2d.common.Vec2;

import java.awt.*;

public class Collect {
    public final StaticBody obj;
    public final Vec2 size;
    public final Vec2 pos;
    private final float initialY;
    private String icon;
    private float currentTime;
    private Type type;
    private int value;

    // Constructor for Gun
    public Collect(World world, Vec2 size, Vec2 position, String img) {
        obj = new StaticBody(world);
        obj.addImage(new BodyImage(img, size.y * 2));
        obj.setPosition(position);
        obj.setLineColor(new Color(0, 0, 0, 0));
        new GhostlyFixture(obj, new BoxShape(size.x, size.y), 0);

        this.initialY = position.y;
        this.currentTime = (float) Math.random() * 300;

        this.size = size;
        this.pos = position;
    }

    // Constructor for pick-ups
    public Collect(World world, Vec2 size, Vec2 position, Type type, int value) {
        obj = new StaticBody(world);
        String img = type == Collect.Type.HEALTH ? "data/health.png" : "data/bullets.png";
        obj.addImage(new BodyImage(img, size.y * 2));
        obj.setName("pickup");
        obj.setPosition(position);
        obj.setLineColor(new Color(0, 0, 0, 0));
        new GhostlyFixture(obj, new BoxShape(size.x, size.y), 0);

        this.initialY = position.y;
        this.currentTime = (float) Math.random() * 300;

        this.type = type;
        this.value = value;
        this.size = size;
        this.pos = position;
    }

    // Get the path for the icon
    public String getIcon() {
        return icon;
    }

    // Set the path for the icon
    public void setIcon(String path) {
        icon = path;
    }

    // move along y-axis in a sine wave
    public void bob(float amplitude, float period) {
        currentTime += 1;
        float angle = 2 * (float) Math.PI * currentTime / period;
        float newY = initialY + amplitude * (float) Math.sin(angle);
        obj.setPosition(new Vec2(obj.getPosition().x, newY));
    }

    public void collected(Player player) {
        if (type == Type.HEALTH) {
            player.health += value;
            player.health = Math.min(player.health, 99);
        } else if (type == Type.AMMO) {
            player.ammo += value;
            player.ammo = Math.min(player.ammo, player.maxAmmo);
        }
        obj.destroy();
    }

    public Type getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    // Enumerator for types of collectibles
    public enum Type {
        HEALTH, AMMO
    }
}
