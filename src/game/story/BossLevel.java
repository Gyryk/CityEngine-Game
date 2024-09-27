package game.story;

import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import game.Collect;
import game.bodies.Boss;
import game.master.Level;
import org.jbox2d.common.Vec2;

import static game.story.GameStory.bossKilled;

public class BossLevel extends Level {
    private final Boss boss;

    // Constructor
    public BossLevel(String path, int zoom, String text) {
        super(path, zoom, text);

        boss = new Boss(this, new Vec2(32, 32), true);
        boss.addDestructionListener(ev -> bossKilled = true);

        // Drop health whenever boss takes enough damage
        addStepListener(new StepListener() {
            @Override
            public void preStep(StepEvent stepEvent) {

            }

            @Override
            public void postStep(StepEvent stepEvent) {
                if (boss.dropNow) {
                    boss.dropNow = false;
                    collectibles.add(new Collect(boss.getWorld(), new Vec2(1, 1), boss.getPosition(), Collect.Type.HEALTH, 10));
                }
            }
        });
    }

    // Getters
    public Boss getBoss() {
        return boss;
    }
}
