package io;

import board.Board;
import game.Game;
import snakes.DebugSnake;
import snakes.MySnake;
import snakes.Snake;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The game.Window Class represents the full window on the screen.
 * It renders all appearances.
 */
public class Window extends JFrame {

    private final JPanel boardPanel;

    public Window(Game game, String title, boolean debug, int SCALE, int MAX_X, int MAX_Y, int MAX_APPLES_ON_BOARD) {
        super(title);

        // Create Window frame
        this.setSize(SCALE * SCALE + 500,SCALE * SCALE + 40);
        this.setLayout(new BorderLayout());

        // add snakes to board (TODO: make automatic) -----
        Snake mySnake = new MySnake();
        Snake mySnake2 = new MySnake();
        Snake mySnake3 = new MySnake();
        DebugSnake debugSnake = new DebugSnake();

        Snake[] mySnakes;
        mySnakes = (debug) ? new Snake[]{mySnake, mySnake2, mySnake3, debugSnake} : new Snake[]{mySnake, mySnake2, mySnake3};
        // ----------

        // create Arena (BoardLayout)
        this.boardPanel = new Board(game, mySnakes, SCALE, MAX_X, MAX_Y, MAX_APPLES_ON_BOARD);

        // Adds Arena Panel to game.Window frame
        this.add(this.boardPanel, BorderLayout.CENTER);

        // register key listener
        InputListener listener = new InputListener(game, debugSnake);
        this.addKeyListener(listener);
        this.setFocusable(true);

        // set visibility and stuff
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void update(){
        this.boardPanel.repaint();
    }
}

