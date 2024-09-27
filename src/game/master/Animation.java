package game.master;

import city.cs.engine.BodyImage;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class Animation {
    private final BodyImage[] anim;
    public int animInx;
    public float size;
    private int framesAllowed;

    // Constructor that creates array of body images from buffered images
    public Animation(String path, int width, int height, int row, int col, float size) {
        BufferedImage[] images = GameImage.loadSprites(path, width, height, row, col);
        anim = new BodyImage[Objects.requireNonNull(images).length];
        for (int i = 0; i < Objects.requireNonNull(images).length; i++) {
            anim[i] = new GameImage(images[i], i, 10);
        }

        framesAllowed = 0;
        animInx = 0;
        this.size = size;
    }

    public void addLoops(int mul) {
        animInx = 0;
        this.framesAllowed = mul * anim.length;
    }

    public int getFramesAllowed() {
        return framesAllowed;
    }

    public BodyImage get() {
        return anim[animInx % anim.length];
    }

}
