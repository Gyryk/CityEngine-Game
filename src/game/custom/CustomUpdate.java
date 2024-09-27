package game.custom;

import city.cs.engine.StepEvent;
import game.Update;
import org.jbox2d.common.Vec2;

import java.awt.geom.Point2D;

import static game.custom.Custom.*;

public class CustomUpdate extends Update {

    @Override
    public void preStep(StepEvent stepEvent) {
        findMouse();
    }

    @Override
    public void postStep(StepEvent stepEvent) {
        findMouse();
    }

    void findMouse() {
        Point2D mousePoint = view.getMousePosition();
        if (mousePoint == null) return;
        mousePos = new Vec2(view.viewToWorld(mousePoint));
        mousePos = clampedVec2(mousePos, -37f, -29f, 37f, 29f);

        cursor.setPosition(mousePos);
    }
}
