package game;

import city.cs.engine.World;
import org.jbox2d.common.Vec2;

public class Gun extends Collect {
    public final int damage;
    public final float range;
    public final float spread;
    public final int ammo;

    public final Vec2 size;
    public final String imgPath;
    public final String name;

    public Gun(World world, Vec2 size, int damage, float range, float spread, Vec2 position, String img, int ammo, String name, String icon) {
        super(world, size, position, img);
        obj.setName(name);
        this.damage = damage;
        this.range = range;
        this.spread = spread;
        this.ammo = ammo;

        this.size = size;
        this.imgPath = img;
        this.name = name;
        setIcon(icon);
    }
}
