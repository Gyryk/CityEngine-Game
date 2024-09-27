package game;

import city.cs.engine.*;
import game.master.Audio;
import org.jbox2d.common.Vec2;

public class Trampoline extends Platform {
    // Trampoline SensorListener
    final SensorListener trampoline = new SensorListener() {
        @Override
        public void beginContact(SensorEvent sensorEvent) {
            Body b = sensorEvent.getContactBody();

            // Find the dynamic body and bounce upwards (bounce higher if current velocity is low)
            if (b instanceof DynamicBody db) {
                Audio.playSound("data/sfx/bounce.wav");
                if (Math.abs(db.getLinearVelocity().y) <= 15f) {
                    db.applyImpulse(new Vec2(0f, 500f));
                } else {
                    db.applyImpulse(new Vec2(0f, 100f));
                }
            }
        }

        @Override
        public void endContact(SensorEvent sensorEvent) {
            // No more score adding :(
        }
    };


    // Constructor
    public Trampoline(World world, Vec2 size, Vec2 position) {
        super(world, size, position, "data/trampoline.png", "bounce");
        // Adding trampoline to platforms
        BoxShape trampShape = new BoxShape(size.x - 0.1f, size.y - 0.1f);
        StaticBody bod = new StaticBody(world);
        bod.setPosition(new Vec2(position.x, position.y + 0.2f));
        Sensor s = new Sensor(bod, trampShape);
        s.addSensorListener(trampoline);
    }
}
