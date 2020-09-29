package snakes;

import java.awt.*;
import java.util.*;
import java.util.List;

import board.*;

public abstract class Snake {

    public final static int UP = 0;
    public final static int RIGHT = 1;
    public final static int DOWN = 2;
    public final static int LEFT = 3;

    public String NAME;
    public Color COLOR;

    /**
     * Main function for every intelligence of the snake
     *
     * @param board the whole board with every information necessary
     * @return direction in which the snake should move
     */
    public abstract int think(BoardInfo board);

}
