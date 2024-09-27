package game.master;

import city.cs.engine.*;
import game.Update;
import game.View;
import game.bodies.*;
import org.jbox2d.common.Vec2;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Objects;

public class Mouse extends MouseAdapter {
    private final Body cursor;
    private final Player player;
    private final World world;
    private final View view;
    private final Update update;
    private final CollisionListener bulletListener;
    private final HashMap<String, String> sfx = new HashMap<>();
    private boolean ammoMode = false;

    // Constructor
    public Mouse(Body cursor, Player player, World world, View view, Update update) {
        this.cursor = cursor;
        this.player = player;
        this.world = world;
        this.view = view;
        this.update = update;

        bulletListener = collisionEvent -> {
            Bullet b = (Bullet) collisionEvent.getReportingBody();
            if (collisionEvent.getOtherBody() instanceof Enemy enemy) enemy.damage(b.damage);
            else if (collisionEvent.getOtherBody() instanceof Brute brute) brute.damage(b.damage);
            else if (collisionEvent.getOtherBody() instanceof Boss boss) boss.damage(b.damage);

            if (!Objects.equals(collisionEvent.getOtherBody().getName(), "projectile")) b.destroy();
        };

        sfx.put("knife", "data/sfx/knife.wav");
        sfx.put("pistol", "data/sfx/pistol.wav");
        sfx.put("shotgun", "data/sfx/shotgun.wav");
        sfx.put("sniper", "data/sfx/sniper.wav");
        sfx.put("rifle", "data/sfx/rifle.wav");
    }

    // Mouse pressed events
    @Override
    public void mousePressed(MouseEvent e) {
        // alternate between sniping and not sniping
        if (e.getButton() == MouseEvent.BUTTON3 && player.hasSniper && player.sniperEquipped()) {
            player.sniping = !player.sniping;
            Audio.playSound("data/sfx/scope.wav");
            if (player.sniping) {
                CircleShape cursorShape = new CircleShape(40f / view.getZoom());
                cursor.setFillColor(new Color(0, 0, 0, 0));
                cursor.getFixtureList().get(0).destroy();
                new GhostlyFixture(cursor, cursorShape, 0);

                cursor.addImage(new BodyImage("data/scope.png", 1600 / view.getZoom()));
                player.line.setFillColor(new Color(0, 0, 0, 0));
            } else {
                CircleShape cursorShape = new CircleShape(10f / view.getZoom());
                cursor.setFillColor(new Color(255, 255, 255, 20));
                cursor.getFixtureList().get(0).destroy();
                new GhostlyFixture(cursor, cursorShape, 0);

                cursor.removeAllImages();
                player.line.setFillColor(player.lineColor);
            }
        }

        if (e.getButton() == MouseEvent.BUTTON1) {
            if (player.equipped == -1) return;
            String name = player.inventory.get(player.equipped).obj.getName();
            switch (name) {
                case "pistol", "rifle" -> {
                    if (ammoMode) {
                        if (player.ammo < player.useAmmo) return;
                        player.ammo -= player.useAmmo;
                    }
                    new Bullet(world, view.viewToWorld(e.getPoint()), player.getPosition(), bulletListener, 0.8f * player.inventory.get(player.equipped).range, player.damage);
                    Audio.playSound(sfx.get(name));
                    update.setAnimation(name, 1);
                }
                case "shotgun" -> {
                    if (ammoMode) {
                        if (player.ammo >= player.useAmmo) {
                            player.ammo -= player.useAmmo;
                            shootShotgun(3, e);
                            Audio.playSound(sfx.get("shotgun"));
                            update.setAnimation("shotgun", 1);
                        }
                    } else {
                        shootShotgun(4, e);
                        Audio.playSound(sfx.get("shotgun"));
                        update.setAnimation("shotgun", 1);
                    }
                }
                case "sniper", "knife" -> {
                    if (ammoMode) {
                        if (player.ammo < player.useAmmo) return;
                        player.ammo -= player.useAmmo;
                    }
                    if (name.equals("sniper") && !player.sniping) {
                        shootShotgun(1, e);
                        Audio.playSound(sfx.get("sniper"));
                        update.setAnimation("sniper", 1);
                    } else {
                        attackEnemy();
                        Audio.playSound(sfx.get(name));
                    }
                }
            }
        }

    }

    // check for intersections based on state
    void attackEnemy() {
        Enemy closest;
        if (player.sniping) {
            closest = GameMath.checkIntersections(cursor, world, player);
        } else {
            closest = GameMath.checkIntersections(player.line, world, player);
        }

        // damage the enemy ball
        if (closest != null) {
            closest.damage(player.damage);
        }
    }

    // Calculate spread and shoot shotgun pellets (also used to shoot sniper bullets with spread)
    void shootShotgun(int pellets, MouseEvent e) {
        for (int i = 0; i < pellets; i++) {
            Vec2 dir = GameMath.spread(view.viewToWorld(e.getPoint()), player.getPosition(), player.inventory.get(player.equipped).spread / pellets);

            new Bullet(world, player.getPosition(), bulletListener, 0.8f * player.inventory.get(player.equipped).range, player.damage, dir);
        }
    }

    public void setAmmoMode(boolean mode) {
        ammoMode = mode;
    }
}
