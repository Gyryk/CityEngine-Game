package game;

import city.cs.engine.BodyImage;
import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import city.cs.engine.World;
import org.jbox2d.common.Vec2;

public class Vanish extends Platform {
    int delay = 4;

    public Vanish(World world, Vec2 size, Vec2 position) {
        super(world, size, position, "data/vanish-platform.png", "vanish");
        delay *= world.getSimulationSettings().getFrameRate();

        StepListener steps = new StepListener() {
            int frames = (int) (Math.random() * delay);
            boolean visible = true;

            @Override
            public void preStep(StepEvent stepEvent) {
                frames++;
            }

            @Override
            public void postStep(StepEvent stepEvent) {
                if (frames % delay == 0 && frames != 0) {
                    visible = !visible;
                    if (visible) {
                        addImage(new BodyImage(imgPath, size.y * 2));
                    } else {
                        removeAllImages();
                    }
                }
            }
        };

        world.addStepListener(steps);

        addDestructionListener(e -> world.removeStepListener(steps));
    }
}
