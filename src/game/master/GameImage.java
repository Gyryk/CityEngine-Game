package game.master;

import city.cs.engine.BodyImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GameImage extends BodyImage {

    // Constructor
    GameImage(String fileName, float size) {
        super(fileName, size);
    }

    // Constructor with BufferedImage
    public GameImage(BufferedImage image, int inx, float size) {
        this(saveBufferedImageToTempFile(image, inx), size);
    }

    // Creating temporary files of relevant images in runtime
    private static String saveBufferedImageToTempFile(BufferedImage image, int inx) {
        try {
            File tempFile = File.createTempFile("image" + inx, ".png");
            ImageIO.write(image, "png", tempFile);
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Loading individual sprites from a sprite-sheet and returning them as an array of buffered images
    public static BufferedImage[] loadSprites(String path, int spriteWidth, int spriteHeight, int rows, int columns) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(path));
            BufferedImage[] sprites = new BufferedImage[rows * columns];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    int x = j * spriteWidth;
                    int y = i * spriteHeight;

                    sprites[(i * columns) + j] = spriteSheet.getSubimage(x, y, spriteWidth, spriteHeight);
                }
            }

            return sprites;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
