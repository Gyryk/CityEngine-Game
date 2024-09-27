package game.master;

import city.cs.engine.Body;
import city.cs.engine.UserView;
import city.cs.engine.World;
import game.Update;
import game.bodies.Player;
import org.jbox2d.common.Vec2;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keys implements KeyListener {
    private final Player player;
    private final World world;
    private final UserView view;
    private final Update update;

    // Constructor
    public Keys(Player player, World world, UserView view, Update update) {
        this.player = player;
        this.world = world;
        this.view = view;
        this.update = update;
    }

    // This method is called when a key is typed (pressed and released)
    @Override
    public void keyTyped(KeyEvent e) {
        char keyChar = e.getKeyChar();

        // check if the key pressed is a number and if it is, equip that weapon
        if (Character.isDigit(keyChar) && keyChar != '0') {
            int inx = Integer.parseInt(String.valueOf(keyChar)) - 1;
            player.equipGun(inx, view.getZoom(), update);
        }
    }

    // This method is called when a key is pressed
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // move the player
        if (keyCode == KeyEvent.VK_A) {
            Vec2 vel = player.getLinearVelocity();
            vel.x = Math.abs(vel.x) > 10 ? -Math.abs(vel.x) : -10;
            player.setLinearVelocity(vel);
            player.moving = true;
        }
        if (keyCode == KeyEvent.VK_D) {
            Vec2 vel = player.getLinearVelocity();
            vel.x = Math.abs(vel.x) > 10 ? Math.abs(vel.x) : 10;
            player.setLinearVelocity(vel);
            player.moving = true;
        }
        // jump
        if (keyCode == KeyEvent.VK_SPACE) {
            if (nearGround()) {
                Audio.playSound("data/sfx/jump.wav");
                player.applyImpulse(new Vec2(0, 500));
            }
        }
        // ground pound
        if (keyCode == KeyEvent.VK_CONTROL) {
            if (!nearGround()) {
                Audio.playSound("data/sfx/jump.wav");
                Vec2 vel = player.getLinearVelocity();
                vel.y = Math.abs(vel.y) > 15 ? -Math.abs(vel.y) : -15;
                player.setLinearVelocity(vel);
                player.applyImpulse(new Vec2(0, -500));
            }
        }
        // boost/ram
        if (keyCode == KeyEvent.VK_SHIFT) {
            if (player.canDash) {
                Audio.playSound("data/sfx/jump.wav");
                player.canDash = false;
                player.timer = 0;
                player.canKill = true;

                // calculate velocity based on current velocity, inversely proportional
                float boostVel = 20000 / player.getLinearVelocity().x;
                if (boostVel < 0) {
                    boostVel = Math.max(-1000, boostVel);
                } else {
                    boostVel = Math.min(1000, boostVel);
                }

                player.applyImpulse(new Vec2(boostVel, 0));
            }
        }
    }

    // This method is called when a key is released
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_D) {
            player.moving = false;
        }
    }

    // Check if the player is near any platforms or walls
    boolean nearGround() {
        for (Body body : world.getStaticBodies()) {
            if (body.getName() != null) {
                if (player.detect.intersects(body) && body.getName().startsWith("ground")) {
                    return true;
                }
            }
        }
        return false;
    }
}
