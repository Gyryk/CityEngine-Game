package game;

import game.arcade.GameArcade;
import game.arcade.ScoreWriter;
import game.custom.Custom;
import game.master.Audio;
import game.story.GameStory;
import game.survival.GameSurvival;
import game.survival.TimeWriter;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Menu {
    public static final JFrame frame = new JFrame("Bouncing Around with Girik");
    public static Color background;
    static JPanel mainMenuPanel;
    static Clip menuMusic = null;

    public static void createMainMenuPanel() {
        if (menuMusic == null) menuMusic = Audio.loopMusic("data/music/lobby-time.wav", 0.75f);

        // Make a center aligned title
        JLabel gameTitle = new JLabel("Bouncing Around");
        gameTitle.setFont(new Font("Times New Roman", Font.BOLD, 48));
        gameTitle.setForeground(Color.WHITE);
        gameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Make the buttons for the different modes
        JButton arcadeButton = menuButton("Arcade Mode", e -> switchToArcade());
        JButton storyButton = menuButton("Story Mode", e -> switchToStory());
        JButton survivalButton = menuButton("Survival Mode", e -> switchToSurvival());
        JButton customButton = menuButton("Custom Mode", e -> switchToCustom());
        JButton scoreButton = menuButton("High Scores", e -> showScores());
        JButton quitButton = menuButton("Quit", e -> System.exit(0));

        // Create a panel to hold the buttons vertically
        JPanel verticalPanel = new JPanel();
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
        verticalPanel.setBackground(background);

        // Add buttons to grid
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(2, 3, 20, 20));
        gridPanel.setBackground(background);

        gridPanel.add(storyButton);
        gridPanel.add(arcadeButton);
        gridPanel.add(survivalButton);
        gridPanel.add(customButton);
        gridPanel.add(scoreButton);
        gridPanel.add(quitButton);

        // Add the grid to a panel that adds horizontal padding
        JPanel paddedPanel = new JPanel();
        paddedPanel.setLayout(new GridBagLayout());
        paddedPanel.setBackground(background);
        paddedPanel.add(gridPanel); // TODO: Fix this to add padding and align stuff to the center properly

        // Add grid to the vertical panel with spacing
        verticalPanel.add(Box.createVerticalGlue());
        verticalPanel.add(Box.createVerticalGlue());
        verticalPanel.add(Box.createVerticalGlue());

        verticalPanel.add(gameTitle);

        verticalPanel.add(Box.createVerticalGlue());
        verticalPanel.add(Box.createVerticalGlue());

        verticalPanel.add(paddedPanel);

        verticalPanel.add(Box.createVerticalGlue());
        verticalPanel.add(Box.createVerticalGlue());
        verticalPanel.add(Box.createVerticalGlue());

        // Create a panel with BorderLayout to center align the vertical panel
        mainMenuPanel = new JPanel(new BorderLayout());
        mainMenuPanel.add(verticalPanel, BorderLayout.CENTER);

        frame.add(mainMenuPanel);
        frame.revalidate();
        frame.repaint();
    }

    static JButton menuButton(String text, ActionListener e) {
        JButton menuButton = new JButton(text);
        menuButton.setFont(new Font("Roboto", Font.ITALIC, 24));
        menuButton.setPreferredSize(new Dimension(240, 100));
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuButton.addActionListener(e);
        menuButton.setForeground(Color.WHITE);
        menuButton.setOpaque(false);
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);

        return menuButton;
    }

    static void switchToArcade() {
        menuMusic = Audio.stopMusic(menuMusic);
        frame.remove(mainMenuPanel);
        new GameArcade();
        frame.revalidate();
        frame.repaint();
    }

    static void switchToStory() {
        //TODO: Give player option to start new game or load save?
        menuMusic = Audio.stopMusic(menuMusic);
        frame.remove(mainMenuPanel);
        new GameStory();
        frame.revalidate();
        frame.repaint();
    }

    static void switchToCustom() {
        menuMusic = Audio.stopMusic(menuMusic);
        frame.remove(mainMenuPanel);
        new Custom();
        frame.revalidate();
        frame.repaint();
    }

    static void switchToSurvival() {
        menuMusic = Audio.stopMusic(menuMusic);
        frame.remove(mainMenuPanel);
        new GameSurvival();
        frame.revalidate();
        frame.repaint();
    }

    // Show the scores from the file
    static void showScores() {
        frame.remove(mainMenuPanel);
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.X_AXIS));
        scorePanel.setBackground(background);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(background);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("High Scores");
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Times New Roman", Font.BOLD, 48));


        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setFont(new Font("Roboto", Font.ITALIC, 24));
        mainMenuButton.setPreferredSize(new Dimension(200, 72));
        mainMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainMenuButton.addActionListener(e -> {
            frame.remove(scorePanel);
            createMainMenuPanel();
        });

        titlePanel.add(Box.createVerticalGlue());
        titlePanel.add(Box.createVerticalGlue());
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalGlue());
        titlePanel.add(mainMenuButton);
        titlePanel.add(Box.createVerticalGlue());
        titlePanel.add(Box.createVerticalGlue());

        JPanel scores = new JPanel();
        scores.setBackground(background);
        scores.setLayout(new BoxLayout(scores, BoxLayout.Y_AXIS));
        ArrayList<ScoreWriter.ScoreData> scoreDataList = ScoreWriter.readScoresFromFile("data/scores.txt");
        scoreDataList = new ArrayList<>(scoreDataList.subList(0, Math.min(20, scoreDataList.size())));

        for (ScoreWriter.ScoreData scoreData : scoreDataList) {
            JLabel score = new JLabel(scoreData.getPlayerName() + " : " + scoreData.getScore());
            score.setForeground(Color.WHITE);
            score.setFont(new Font("Roboto", Font.PLAIN, 24));
            score.setAlignmentX(Component.RIGHT_ALIGNMENT);
            scores.add(score);
        }

        JPanel times = new JPanel();
        times.setBackground(background);
        times.setLayout(new BoxLayout(times, BoxLayout.Y_AXIS));
        ArrayList<TimeWriter.TimeData> timeDataList = TimeWriter.readTimeFromFile("data/times.txt");
        timeDataList = new ArrayList<>(timeDataList.subList(0, Math.min(20, timeDataList.size())));

        for (TimeWriter.TimeData timeData : timeDataList) {
            JLabel time = new JLabel(timeData.getPlayerName() + " : " + TimeWriter.convert(timeData.getTime()));
            time.setForeground(Color.WHITE);
            time.setFont(new Font("Roboto", Font.PLAIN, 24));
            time.setAlignmentX(Component.RIGHT_ALIGNMENT);
            times.add(time);
        }

        scorePanel.add(Box.createHorizontalGlue());
        scorePanel.add(titlePanel);
        scorePanel.add(Box.createHorizontalGlue());
        scorePanel.add(scores);
        scorePanel.add(Box.createHorizontalGlue());
        scorePanel.add(times);
        scorePanel.add(Box.createHorizontalGlue());

        frame.add(scorePanel);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Run the game.
     */
    public static void main(String[] args) {
        background = new Color(20, 20, 20);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // enable the frame to quit the application
        frame.setLocationByPlatform(true);
        // get the computer screen resolution and put it in the middle
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screenSize.width / 2 - 540, screenSize.height / 2 - 360);
        frame.setPreferredSize(new Dimension(1080, 720));
        frame.setResizable(false); // don't let the frame be resized

        createMainMenuPanel(); // create the main menu panel

        frame.pack(); // size the frame to fit the world view
        frame.setVisible(true); // finally, make the frame visible
    }
}
