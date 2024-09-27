package game.story;

import city.cs.engine.StepEvent;
import game.Update;
import game.master.GameMath;

import javax.swing.*;

import static game.story.GameStory.*;

public class StoryUpdate extends Update {
    private final GameStory gameStory;
    boolean swapped;

    // Constructor
    public StoryUpdate(GameStory gameStory) {
        super();
        this.gameStory = gameStory;
        swapped = false;
    }

    // physics update
    @Override
    public void preStep(StepEvent stepEvent) {
        checkCollect(collectibles, player, view);
        boostEnemy(enemies, player, view);
        velocityCap(enemies, player);

        player.drawGun(cursor.getPosition(), off);
        if (currAnim != null) {
            shootGun(currAnim, player);
        }
    }

    // drawing updates
    @Override
    public void postStep(StepEvent stepEvent) {
        player.boost();
        GameMath.calculateLine(player.getPosition(), cursor.getPosition(), player.line, player.lineRange, player.offset, view.getZoom());
        calculateEnemyLines(enemies, player, view);
        updateLevel();

        player.drawGun(cursor.getPosition(), off);
    }

    // Check if player has finished a level and update current level accordingly
    public void updateLevel() {
        if (currLevel + 1 < levels.length) {
            if (killed == enemies.size()) {
                // Change level after a 2-second delay
                if (!swapped) {
                    swapped = true;
                    Timer timer = new Timer(2000, e -> {
                        currLevel++;
                        gameStory.changeLevel();
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        } else {
            if (bossKilled) {
                currLevel++;
                gameStory.gameWon();
            }
        }
    }
}


