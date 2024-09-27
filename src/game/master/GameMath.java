package game.master;

import city.cs.engine.Body;
import city.cs.engine.DynamicBody;
import city.cs.engine.World;
import game.bodies.Enemy;
import game.bodies.Player;
import org.jbox2d.common.Vec2;

public class GameMath {

    // Calculate a value clamped between 1 range as a new value clamped between another range
    public static int mapRange(float value, float low1, float high1, float low2, float high2) {
        value = (value - low1) / (high1 - low1);
        return (int) (low2 + value * (high2 - low2));
    }

    // Formula for distance between 2 points
    public static float calculateDistance(Vec2 pos1, Vec2 pos2) {
        return (float) Math.sqrt(Math.pow(pos2.x - pos1.x, 2) + Math.pow(pos2.y - pos1.y, 2));
    }

    // Calculate and draw a line of fixed length between 2 points
    public static void calculateLine(Vec2 playerPos, Vec2 cursorPos, Body pLine, float pRange, float pOffset, float zoom) {
        // Calculate the angle and the endpoint
        float angle = (float) Math.atan2(cursorPos.y - playerPos.y, cursorPos.x - playerPos.x);
        int fixedLineEndX = (int) (playerPos.x + ((((pRange * 2) + pOffset) / zoom) * Math.cos(angle)));
        int fixedLineEndY = (int) (playerPos.y + ((((pRange * 2) + pOffset) / zoom) * Math.sin(angle)));
        Vec2 linePos = new Vec2((playerPos.x + fixedLineEndX) / 2, (playerPos.y + fixedLineEndY) / 2);
        pLine.setPosition(linePos);
        pLine.setAngle(angle);
        pLine.setAngleDegrees(pLine.getAngleDegrees() + 90);
    }

    public static Vec2 capVelocity(Vec2 vel, float maxSpeed) {
        float combined = (float) Math.sqrt(Math.pow(vel.x, 2) + Math.pow(vel.y, 2));
        if (combined > maxSpeed) {
            vel.x = (vel.x / combined) * maxSpeed;
            vel.y = (vel.y / combined) * maxSpeed;
        }
        return vel;
    }

    // Check if cursor intersects with any of the balls and find the closest one
    public static Enemy checkIntersections(Body intersector, World world, Player player) {
        float minDist = 20000;
        Enemy closest = null;
        for (DynamicBody body : world.getDynamicBodies()) {
            if (intersector.intersects(body)) {
                if (body instanceof Enemy enemy) {
                    float dist = GameMath.calculateDistance(player.getPosition(), body.getPosition());
                    if (dist < minDist) {
                        minDist = dist;
                        closest = enemy;
                    }
                }
            }
        }
        return closest;
    }

    // Return a Vector 2 with randomised spread added
    public static Vec2 spread(Vec2 aim, Vec2 start, float spreadAngle) {
        Vec2 dir = aim.sub(start);
        dir.normalize();
        float angleOffset = (float) (Math.random() - 0.5f) * spreadAngle;
        float cos = (float) Math.cos(angleOffset);
        float sin = (float) Math.sin(angleOffset);
        float x = dir.x * cos - dir.y * sin;
        float y = dir.x * sin + dir.y * cos;
        return new Vec2(x, y);
    }
}
