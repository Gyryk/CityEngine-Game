package game.bodies;

import city.cs.engine.*;
import game.Gun;
import game.Update;
import game.master.Audio;
import org.jbox2d.common.Vec2;

import java.awt.*;
import java.util.ArrayList;

public class Player extends Ball {
    public final ArrayList<Gun> inventory;
    public final DynamicBody gun;
    public final StaticBody detect;

    public final int maxHealth;
    public final int maxAmmo;
    public final float delay;
    public final float offset = 32f;
    public int meleeDamage;
    public int equipped;
    public int ammo;
    public int useAmmo;
    public boolean dashing = false;
    public boolean canDash = true;
    public boolean canKill = false;
    public boolean hittable = true;
    public boolean moving = true;
    public boolean hasSniper = false;
    public boolean sniping = false;
    public float timer;

    public Color lineColor;
    public Body line;
    public float lineRange = 0f;

    // Constructor
    public Player(World world, int health, int damage, Vec2 position, float delay, float radius, float max) {
        super(world, radius, health, damage, max, position);
        this.maxHealth = health;
        this.meleeDamage = 5;
        this.delay = delay;
        this.timer = delay;

        addImage(new BodyImage("data/player.png", radius * 2));
        setFillColor(new Color(0, 0, 0, 0));
        setLineColor(new Color(0, 0, 0, 0));
        setName("player");
        SolidFixture pFix = new SolidFixture(this, new CircleShape(radius));
        pFix.setRestitution(0.99f);

        // Detects whether some enemy or object is nearby
        detect = new StaticBody(world);
        detect.setFillColor(new Color(255, 0, 0, 0));
        detect.setLineColor(new Color(0, 0, 0, 0));
        new GhostlyFixture(detect, new CircleShape(radius * ((float) 3 / 2)), 0);

        // Initialise variables
        equipped = -1;
        ammo = 100;
        maxAmmo = 100;
        useAmmo = 0;
        gun = new DynamicBody(world);
        gun.setGravityScale(0);

        inventory = new ArrayList<>();
    }

    // Damage player and check if dead
    public void damage(int amount) {
        health -= amount;
        Audio.playSound("data/sfx/hit.wav");
        if (health <= 0) {
            Audio.playSound("data/sfx/lose.wav");
            destroy();
            line.destroy();
        }
    }

    // Change the equipped gun
    public void equipGun(int index, float zoom, Update update) {
        if (index < inventory.size()) {
            Audio.playSound("data/sfx/gun.wav");
            equipped = index;
            Gun newGun = inventory.get(index);
            damage = newGun.damage;
            useAmmo = newGun.ammo;
            lineRange = newGun.range;
            redrawLine(inventory.get(equipped).spread, zoom);
            redrawGun(newGun, update);
        }
    }

    // Check if the player is dashing
    public void boost() {
        detect.setPosition(getPosition());
        timer++;

        if (timer >= delay) {
            canDash = true;
            timer = delay;
        }
    }

    // Limit player movement speed
    @Override
    public void limitVelocity() {
        super.limitVelocity();
        Vec2 vel = getLinearVelocity();
        float combined = (float) Math.sqrt(Math.abs(vel.x) + Math.abs(vel.y));

        if (combined > 5) {
            dashing = true;
        } else {
            dashing = false;
            canKill = false;
        }
        if (combined < 7) {
            hittable = !canKill;
        } else {
            hittable = false;
        }
    }

    // Check if the player has the sniper equipped
    public boolean sniperEquipped() {
        if (equipped != -1) {
            return inventory.get(equipped).obj.getName().equals("sniper");
        }
        return false;
    }

    // Get the existing fixture for the line and replace it
    public void redrawLine(float width, float zoom) {
        if (!line.getFixtureList().isEmpty()) {
            Fixture fixture = line.getFixtureList().get(0);
            fixture.destroy();
        }
        new GhostlyFixture(line, new BoxShape(width, lineRange / zoom), 0);

        // change line colour based on whether knife equipped or something else
        if (equipped == -1) return;
        if (inventory.get(equipped).obj.getName().equals("knife")) {
            lineColor = new Color(0, 255, 0, 50);
        } else {
            lineColor = new java.awt.Color(0, 0, 0, 0);
        }
        line.setFillColor(lineColor);
    }

    // Create the gun image and rotate it towards the mouse
    public void drawGun(Vec2 mousePos, float off) {
        if (equipped == -1) return;
        Vec2 dir = mousePos.sub(getPosition());
        dir.normalize();
        Vec2 offset = getPosition().add(dir.mul(off + (radius - 2)));
        float angle = (float) Math.atan2(dir.y, dir.x);

        gun.setPosition(offset);
        if (gun.getImages().isEmpty()) return;
        gun.getImages().get(0).setRotation(angle);
    }

    // Update the values to draw the gun in the correct position
    public void redrawGun(Gun g, Update update) {
        String name = g.obj.getName();
        gun.removeAllImages();
        if (name.equals("knife")) return;
        gun.addImage(update.getFirstFrame(name)).setScale(g.size.y / update.getSize(name));
        update.setOffset(name);
    }
}
