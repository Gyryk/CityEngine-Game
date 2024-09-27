package game.survival;

import city.cs.engine.StepEvent;
import game.Update;
import game.master.GameMath;

import static game.survival.GameSurvival.*;

public class SurvivalUpdate extends Update {
    private final int waveFrames;
    public int frames;

    // Constructor
    public SurvivalUpdate() {
        frames = 0;
        waveFrames = world.getSimulationSettings().getFrameRate() * 30;
    }

    // physics updates
    @Override
    public void preStep(StepEvent stepEvent) {
        boostEnemy(enemies, player, view);
        velocityCap(enemies, player);
        updateWave();

        player.drawGun(cursor.getPosition(), off);
        if (currAnim != null) {
            shootGun(currAnim, player);
        }
    }

    // drawing updates
    @Override
    public void postStep(StepEvent stepEvent) {
        player.boost();
        GameMath.calculateLine(player.getPosition(), cursor.getPosition(), player.line, player.lineRange,
                player.offset, view.getZoom());
        calculateEnemyLines(enemies, player, view);
        frames++;
        view.millis = (int) ((float) frames / world.getSimulationSettings().getFrameRate() * 1000);

        player.drawGun(cursor.getPosition(), off);
    }

    // spawn a new wave every minute
    private void updateWave() {
        if (frames % waveFrames == 0) {
            spawnWave();
        }
    }
}
