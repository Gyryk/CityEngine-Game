package game;

import city.cs.engine.Shape;
import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import java.awt.*;

public class Platform extends StaticBody {
    public Vec2 size;
    public Vec2 pos;
    public String imgPath;
    public String type = "static";

    // Constructor
    public Platform(World world, Shape shape, Vec2 position, Color color) {
        super(world, shape);
        setPosition(position);
        setFillColor(color);
        setLineColor(new Color(0, 0, 0, 0));
        setName("ground");
    }

    // Constructor with image
    public Platform(World world, Vec2 size, Vec2 position, String imgPath, String type) {
        super(world, new BoxShape(size.x, size.y));
        setPosition(position);
        setLineColor(new Color(0, 0, 0, 0));
        setFillColor(new Color(0, 0, 0, 0));
        setName("ground");

        BodyImage img = new BodyImage(imgPath, size.y * 2);
        addImage(img);

        this.size = size;
        this.imgPath = imgPath;
        this.pos = position;
        this.type = type;
    }
}
