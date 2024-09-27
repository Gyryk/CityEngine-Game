package game;

import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import city.cs.engine.World;
import org.jbox2d.common.Vec2;

public class Rotate extends Platform {
    public Rotate(World world, Vec2 size, Vec2 position) {
        super(world, size, position, "data/turn-platform.png", "turn");

        StepListener steps = new StepListener() {
            int angle = (int) (Math.random() * 360);

            @Override
            public void preStep(StepEvent stepEvent) {
                angle++;
            }

            @Override
            public void postStep(StepEvent stepEvent) {
                setAngleDegrees(angle % 360);
            }
        };

        world.addStepListener(steps);

        addDestructionListener(e -> world.removeStepListener(steps));
    }
}
