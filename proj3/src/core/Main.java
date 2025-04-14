package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class Main {
    private static final int WORLD_WIDTH = 90;
    private static final int WORLD_HEIGHT = 50;

    private static long getUserInput() {
        JFrame mainFrame = new JFrame("Dungeon Generator Menu");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 250);
        mainFrame.setLayout(new BorderLayout());

        final long[] seed = new long[1];
        seed[0] = -1;
        CountDownLatch latch = new CountDownLatch(1);

        // Title
        JLabel title = new JLabel("CS61B: BYOW", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        mainFrame.add(title, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton newGameButton = new JButton("(N) New Game");
        JButton loadGameButton = new JButton("(L) Load Game");
        JButton exitButton = new JButton("(Q) Quit Game");

        buttonPanel.add(newGameButton);
        buttonPanel.add(loadGameButton);
        buttonPanel.add(exitButton);

        mainFrame.add(buttonPanel, BorderLayout.CENTER);

        // Button Actions
        newGameButton.addActionListener(e -> {
            JFrame newGameFrame = new JFrame("World Generator Menu");
            newGameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            newGameFrame.setSize(400, 250);
            newGameFrame.setLayout(new BorderLayout());

            // Title
            JLabel newGameTitle = new JLabel("CS61B: BYOW", SwingConstants.CENTER);
            newGameTitle.setFont(new Font("Serif", Font.BOLD, 24));
            newGameFrame.add(newGameTitle, BorderLayout.NORTH);

            JLabel seedLabel = new JLabel("Enter a seed value followed by S:");
            seedLabel.setHorizontalAlignment(SwingConstants.CENTER);
            newGameFrame.add(seedLabel, BorderLayout.NORTH);
            JTextField inputField = new JTextField();
            inputField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyChar() == 's' || e.getKeyChar() == 'S') {
                        try {
                            seed[0] = Long.parseLong(inputField.getText());
                            newGameFrame.dispose();
                            mainFrame.dispose();
                            latch.countDown();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Invalid seed");
                        }
                    }
                }
            });
            newGameFrame.add(inputField, BorderLayout.CENTER);

            // Buttons
            JPanel newGameButtonPanel = new JPanel();
            newGameButtonPanel.setLayout(new GridLayout(3, 1, 10, 10));

            newGameFrame.setVisible(true);
        });

        loadGameButton.addActionListener(e -> {
            new Thread(() -> {
                loadGame(); // run on a background thread
            }).start();
            mainFrame.dispose();
        });

        exitButton.addActionListener(e -> {
            System.exit(0);
        });

        mainFrame.setVisible(true);

        try {
            latch.await(); // Block until user presses S
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return seed[0];
    }

    public static void generateWorld(Dungeon inputDungeon) {
        inputDungeon.splitDungeon();
        inputDungeon.createRoom();
        inputDungeon.createHallways();
        inputDungeon.drawRooms();
        inputDungeon.drawHallways();
        inputDungeon.drawWalls();
    }

    public static void initializeWorld(TETile[][] inputTiles) {
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                inputTiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public static void loadGame() {
        TERenderer ter1 = new TERenderer();
        TETile[][] world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];
        ter1.initialize(WORLD_WIDTH, WORLD_HEIGHT);
        initializeWorld(world);
        Dungeon savedDungeon = GameHandler.loadDungeon();
        savedDungeon.setDungeonTiles(world);
        Point avatarPosition = savedDungeon.getAvatarPosition();
        generateWorld(savedDungeon);
        world[avatarPosition.getX()][avatarPosition.getY()] = Tileset.AVATAR;
        GameHandler myGameHandler = new GameHandler(savedDungeon);
        myGameHandler.playGame(avatarPosition, ter1);
    }
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        TETile[][] world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];

        initializeWorld(world);

        long seed = getUserInput();

       if (seed != -1) {
            ter.initialize(WORLD_WIDTH, WORLD_HEIGHT);
            Point worldPosition = new Point(1, 1);
            Dungeon myDungeon = new Dungeon(WORLD_WIDTH, WORLD_HEIGHT, worldPosition, false, seed);
            myDungeon.setDungeonTiles(world);
            generateWorld(myDungeon);
            myDungeon.placeAvatarRandom();
            Point curAvatarPosition = myDungeon.getAvatarPosition();
            GameHandler myGameHandler = new GameHandler(myDungeon);
            myGameHandler.playGame(curAvatarPosition, ter);
       }

        loadGame();

    }
}
