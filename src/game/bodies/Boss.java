package game.bodies;

import city.cs.engine.*;
import game.Gun;
import game.View;
import game.master.GameMath;
import game.story.GameStory;
import game.story.StoryView;
import game.survival.GameSurvival;
import game.survival.SurvivalView;
import org.jbox2d.common.Vec2;

import java.awt.*;

import static game.Game.*;
import static game.bodies.Projectile.calculateDir;
import static game.story.GameStory.collectibles;

public class Boss extends Ball {
    private final World world;
    public boolean dropNow;
    boolean dropsOn;
    boolean angry;
    boolean shooting;
    CollisionListener bullet = collisionEvent -> {
        if (collisionEvent.getOtherBody() instanceof Player p) {
            p.damage(damage);
        }
        collisionEvent.getReportingBody().destroy();
    };
    private View view;
    private Player player;
    private int damaged = 0;
    private int gap = 180;
    private int delay = 15;
    private int shot;

    // Constructor
    public Boss(World world, Vec2 pos, boolean dropsOn) {
        super(world, 10, 999, 10, 30, pos);
        this.dropsOn = dropsOn;
        this.world = world;
        dropNow = false;
        shot = 0;

        setName("enemy");
        setLineColor(new Color(0, 0, 0, 0));
        setFillColor(new Color(0, 0, 0, 0));
        addImage(new BodyImage("data/boss.png", 20));

        SolidFixture fix = new SolidFixture(this, new CircleShape(radius));
        fix.setRestitution(1.01f);

        addCollisionListener(collisionEvent -> {
            if (collisionEvent.getOtherBody() instanceof Player p) {
                p.damage(1);
            }
        });

        StepListener steps = new StepListener() {
            int frames = 0;

            @Override
            public void preStep(StepEvent stepEvent) {
                Vec2 dir = calculateDir(getPosition(), player.getPosition());
                Vec2 offset = getPosition().add(dir.mul(11));
                frames += 1;
                if (frames % gap == 0) {
                    shooting = true;
                }
                if (shooting && frames % delay == 0) {
                    if (!angry) shoot(dir, offset);
                    else spawnEnemy(offset);
                }
            }

            @Override
            public void postStep(StepEvent stepEvent) {
                float speed = 8;
                if (angry) {
                    speed = 16;
                }
                if (player.getPosition().x > getPosition().x) {
                    setLinearVelocity(new Vec2(speed, getLinearVelocity().y));
                } else {
                    setLinearVelocity(new Vec2(-speed, getLinearVelocity().y));
                }
            }
        };

        world.addStepListener(steps);

        addDestructionListener(e -> {
            world.removeStepListener(steps);
            if (GameMath.calculateDistance(getPosition(), player.getPosition()) < 20 && !dropsOn) player.damage(10);
        });
    }

    // Damage the enemy and kill if health below 0
    @Override
    public void damage(int amount) {
        super.damage(amount);
        view.setBossHealth(health);
        damaged += amount;
        int damageTarget = 100;
        if (damaged >= damageTarget && dropsOn) {
            damaged -= damageTarget;
            dropNow = true;
        }
        if (health <= 500) {
            if (dropsOn && !angry) {
                float[] gf = gunTypes.get("rifle");
                String[] gs = gunImages.get("rifle");
                collectibles.add(new Gun(world, new Vec2(gf[0], gf[1]), (int) gf[2], gf[3], gf[4], getPosition(), gs[0],
                        (int) gf[5], "rifle", gs[1]));
            }
            angry = true;
            delay = 30;
            gap = 360;
            removeAllImages();
            addImage(new BodyImage("data/boss-angry.png", 20));
        }
    }

    // Create new enemies
    public void spawnEnemy(Vec2 pos) {
        if (shot >= 3) {
            shot = 0;
            shooting = false;
            return;
        }

        float random = (float) Math.random();
        if (random > 0.5) {
            new Brute(4f, 8, 64, pos, world, view);
            shot++;
        } else {
            int inx = (int) random * 10;
            String name = switch (inx) {
                case 0 -> "small";
                case 2 -> "big";
                case 3 -> "fast";
                case 4 -> "tank";
                default -> "normal";
            };
            float[] e = enemyTypes.get(name);
            Enemy enemy = new Enemy(name, e[0], e[1], e[2], (int) e[3], (int) e[4], e[5], e[6], world, pos);
            enemy.setView(view);
            enemy.addDestructionListener(d -> enemy.line.destroy());
            if (view instanceof SurvivalView) {
                ((SurvivalView) view).addEnemy(enemy);
                GameSurvival.enemies.add(enemy);
            } else {
                ((StoryView) view).addEnemy(enemy);
                GameStory.enemies.add(enemy);
            }
            shot++;
        }
    }

    // Shoot the player
    public void shoot(Vec2 dir, Vec2 offset) {
        if (shot >= 6) {
            shot = 0;
            shooting = false;
            return;
        }
        shot++;
        new Bullet(world, bullet, 0.5f, 0, 50f, damage, dir, offset);
    }

    // Setters
    public void setView(View view) {
        this.view = view;
        this.player = view.getPlayer();
    }
}
