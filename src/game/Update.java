package game;

import city.cs.engine.BodyImage;
import city.cs.engine.DynamicBody;
import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import game.arcade.ArcadeView;
import game.bodies.Enemy;
import game.bodies.Player;
import game.master.Animation;
import game.master.Audio;
import game.master.GameMath;
import org.jbox2d.common.Vec2;

import java.util.ArrayList;
import java.util.HashMap;

import static game.Game.*;

public class Update implements StepListener {
    private final HashMap<String, Integer> offset = new HashMap<>();
    private final HashMap<String, Animation> guns = new HashMap<>();
    public int off = 4;
    public Animation currAnim = null;
    int frames = 0;

    // Constructor
    public Update() {
        guns.put("pistol", new Animation("data/guns/pistol_sprites.png", 64, 32, 1, 12, 4f));
        guns.put("shotgun", new Animation("data/guns/shotgun_sprites.png", 160, 32, 1, 14, 4f));
        guns.put("sniper", new Animation("data/guns/sniper_sprites.png", 160, 32, 28, 1, 4f));
        guns.put("rifle", new Animation("data/guns/rifle_sprites.png", 128, 48, 1, 16, 4f));
        offset.put("pistol", 5);
        offset.put("shotgun", 7);
        offset.put("sniper", 7);
        offset.put("rifle", 5);
    }

    // physics updates
    @Override
    public void preStep(StepEvent stepEvent) {
        checkCollect(collectibles, player, view);
        velocityCap(enemies, player);
        boostEnemy(enemies, player, view);
    }

    // drawing updates
    @Override
    public void postStep(StepEvent stepEvent) {
        player.boost();
        GameMath.calculateLine(player.getPosition(), cursor.getPosition(), player.line, player.lineRange, player.offset, view.getZoom());
        calculateEnemyLines(enemies, player, view);
    }

    /**
     * Check if the player has collected any collectibles and add them to inventory if it's a gun
     *
     * @param collectibles the guns and powers to be collected
     * @param player       the player object
     * @param view         the view object of the level
     */
    public void checkCollect(ArrayList<Collect> collectibles, Player player, View view) {
        for (Collect collect : collectibles) {
            collect.bob(1.5f, 300);

            if (collect.obj.intersects(player)) {
                // Check which collectible and execute code accordingly
                switch (collect.obj.getName()) {
                    case "pistol", "shotgun", "rifle" -> collectGun((Gun) collect, player, view);
                    case "sniper" -> {
                        player.hasSniper = true;
                        collectGun((Gun) collect, player, view);
                    }
                    case "knife" -> {
                        player.meleeDamage *= 2;
                        collectGun((Gun) collect, player, view);
                    }
                    case "pickup" -> {
                        collect.collected(player);
                        Audio.playSound("data/sfx/pickup.wav");
                    }
                }
            }
        }
    }

    /**
     * Calculate the aim lines for enemies going from their circumference to the player
     *
     * @param enemies the enemies in the game
     * @param player  the player object to aim towards
     * @param view    the view object of the level
     */
    public void calculateEnemyLines(ArrayList<Enemy> enemies, Player player, View view) {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy == null) {
                if (view instanceof ArcadeView) {
                    enemies.remove(null);
                }
                continue;
            }
            GameMath.calculateLine(enemy.getPosition(), player.getPosition(), enemy.line, enemy.range * view.getZoom(),
                    enemy.offset * view.getZoom(), view.getZoom());
            enemy.timer++;

            // check intersections for player and if present add a delayed attack
            if (enemy.line.intersects(player) && player.hittable) {
                if (enemy.timer >= enemy.delay) {
                    enemy.timer = 0;
                    player.damage(enemy.damage);
                }
            }
        }
    }

    /**
     * Add a limit to the velocity of the player and enemies and slow the player down organically
     *
     * @param enemies the enemies in the game
     * @param player  the player object
     */
    public void velocityCap(ArrayList<Enemy> enemies, Player player) {
        for (Enemy enemy : enemies) {
            enemy.limitVelocity();
        }

        player.limitVelocity();
        if (!player.moving) {
            if (player.getLinearVelocity().x < 0) {
                player.applyImpulse(new Vec2(1, 0));
            } else if (player.getLinearVelocity().x > 0) {
                player.applyImpulse(new Vec2(-1, 0));
            }
        }
    }

    /**
     * Make sure the player is only attacking 1 enemy per boost
     *
     * @param enemies the enemies in the game
     * @param player  the player object
     */
    public void boostEnemy(ArrayList<Enemy> enemies, Player player, View view) {
        if (player.canKill && player.dashing) {
            for (Enemy enemy : enemies) {
                if (player.detect.intersects(enemy)) {
                    enemy.damage(player.meleeDamage);
                    Audio.playSound("data/sfx/knife.wav");
                    player.canKill = false;
                    if (view instanceof ArcadeView) ((ArcadeView) view).addScore(2);
                }
            }
        }
    }

    /**
     * Draw the gun specific shooting animation
     *
     * @param anim   the animation object of the gun to be drawn
     * @param player the player object
     */
    public void shootGun(Animation anim, Player player) {
        if (anim.animInx < anim.getFramesAllowed()) {
            frames++;

            if (frames % 2 == 0) {
                anim.animInx++;
                if (player.equipped == -1) return;
                DynamicBody g = player.gun;
                g.removeAllImages();
                g.addImage(anim.get());
                g.getImages().get(0).setScale(player.inventory.get(player.equipped).size.y / anim.size);
            }
        }
    }

    // Getters and Setters
    public void setOffset(String key) {
        off = offset.get(key);
    }

    public void setAnimation(String key, int loops) {
        currAnim = guns.get(key);
        currAnim.addLoops(loops);
    }

    public BodyImage getFirstFrame(String key) {
        currAnim = guns.get(key);
        return currAnim.get();
    }

    public float getSize(String key) {
        currAnim = guns.get(key);
        return currAnim.size;
    }
}
