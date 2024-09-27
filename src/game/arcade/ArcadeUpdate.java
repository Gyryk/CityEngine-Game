package game.arcade;

import city.cs.engine.StepEvent;
import game.Gun;
import game.Update;
import game.master.GameMath;
import org.jbox2d.common.Vec2;

import java.util.Timer;
import java.util.TimerTask;

import static game.arcade.GameArcade.*;

public class ArcadeUpdate extends Update {
    private final boolean[] spawned;
    private final float delay;
    private final int[] targets;
    private final int[] probabilities;
    public Timer timer;

    // Constructor
    public ArcadeUpdate(float delay, int[] targets, int[] probabilities) {
        this.delay = delay;
        this.probabilities = probabilities;
        this.targets = targets;
        spawned = new boolean[]{false, false, false, false, false};
        spawnEnemies();
    }

    // physics updates
    @Override
    public void preStep(StepEvent stepEvent) {
        checkCollect(collectibles, player, view);
        boostEnemy(enemies, player, view);
        velocityCap(enemies, player);
        spawnWeapons();

        player.drawGun(cursor.getPosition(), off);

        if (currAnim != null) {
            shootGun(currAnim, player);
        }
    }

    // drawing updates
    @Override
    public void postStep(StepEvent stepEvent) {
        player.boost();
        GameMath.calculateLine(player.getPosition(), cursor.getPosition(), player.line, player.lineRange,
                player.offset, view.getZoom());
        calculateEnemyLines(enemies, player, view);

        player.drawGun(cursor.getPosition(), off);
    }

    // Create collectible weapon objects based on how many enemies have been killed
    public void spawnWeapons() {
        if (killed >= targets[0] && !spawned[0]) {
            // Make a collectible pistol
            createGun("pistol", 0, new Vec2(20, -10));
        }
        if (killed >= targets[1] && !spawned[1]) {
            // make a collectible knife
            createGun("knife", 1, new Vec2(-20, -5));
        }
        if (killed >= targets[2] && !spawned[2]) {
            // make a collectible sniper
            createGun("sniper", 2, new Vec2(0, 5));
        }
        if (killed >= targets[3] && !spawned[3]) {
            // make a collectible rifle
            createGun("rifle", 3, new Vec2(-30, -25));
        }
        if (killed >= targets[4] && !spawned[4]) {
            // make a collectible shotgun
            createGun("shotgun", 4, new Vec2(30, -25));
        }
    }

    // Find values for the gun and create the gun object
    void createGun(String name, int inx, Vec2 pos) {
        float[] val = gunTypes.get(name);
        String[] img = gunImages.get(name);
        Gun c = new Gun(world, new Vec2(val[0], val[1]), (int) val[2], val[3], val[4],
                pos, img[0], (int) val[5], name, img[1]);
        // Add a destruction listener to the collectible
        c.obj.addDestructionListener(ev -> player.redrawGun(c, update));
        collectibles.add(c);
        spawned[inx] = true;
    }

    // Spawn new enemies every x frames, type decided randomly
    void spawnEnemies() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (enemies.size() - killed >= 30) {
                    return;
                }
                int type = (int) (Math.random() * 100);
                if (type < probabilities[0]) {
                    spawnEnemy("tank", world);
                } else if (type < probabilities[1] + probabilities[0]) {
                    spawnEnemy("fast", world);
                } else if (type < probabilities[2] + probabilities[1] + probabilities[0]) {
                    spawnEnemy("big", world);
                } else if (type < probabilities[3] + probabilities[2] + probabilities[1] + probabilities[0]) {
                    spawnEnemy("small", world);
                } else {
                    spawnEnemy("normal", world);
                }
            }
        }, (int) delay, (int) delay);
    }
}
