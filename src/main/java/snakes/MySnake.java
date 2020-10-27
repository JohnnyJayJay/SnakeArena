package snakes;

import board.BoardInfo;
import board.Field;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;


/**
 * This is the snake which has to be filled with lovely intelligence code
 * to become the longest snake of all other snakes.
 *
 * This snake appears 'MySnake' times an board.
 * The value behind 'MySnake' can set in the property file (snake_arena.properties).
 */
public class MySnake extends Snake {

    private int direction = RIGHT;


    public MySnake() {
        this.NAME = "YOUR_KUHL_SNAKE_NAME";         // set your favorite name
        this.COLOR = new Color(150, 100, 255); // set your favorite color
    }

    /**
     * Main function for every intelligence of the snake
     *
     * @param board the whole board with every information necessary
     * @return direction in which the snake should move
     */
    @Override
    public int think(BoardInfo board) {
        long start = System.currentTimeMillis();

        try {
            // TODO
        } catch (Exception e) {
            direction = survive(board);
        }

        System.out.printf("time taken to think: %d\n", System.currentTimeMillis() - start);

        return direction; // or LEFT, or DOWN, or UP
    }


    private int survive(BoardInfo board) {
        if (!board.isNextStepFree(direction)) {
            while (!board.isNextStepFree(direction)) {
                direction = (direction + 1) % 4;
            }
        }
        return direction;
    }


    /*
     * class to store the values of each field
     */
    static class QueueItem {
        public Field pos;
        public int counter;
        boolean initialized;

        public QueueItem(Field pos) {
            this.pos = pos;
            counter = -1;
            initialized = false;
        }

        void initialize(int counter) {
            initialized = true;
            this.counter = counter;
        }
    }
    // TODO edge cases: can't reach any apple,



    private List<QueueItem> findPath(BoardInfo board, Field destination) {
        QueueItem[][] fields = assessField(board, destination);
        Field head = board.getOwnHead();
        QueueItem start = fields[head.getPosX()][head.getPosY()];
        List<QueueItem> path = new ArrayList<>();
        QueueItem current = start;
        while (current.counter != 0) {
            current = getAdjacentFields(board, fields, current).stream()
                    .filter((field) -> field.initialized)
                    .min(Comparator.comparingInt((field) -> field.counter))
                    .orElseThrow(); // TODO handle if field wasn't assessed (no path?)
            path.add(current);
        }
        return path;
    }

    private QueueItem[][] assessField(BoardInfo board, Field destination) {
        QueueItem[][] fields = createMatrix(board);
        List<QueueItem> path = new ArrayList<>();
        QueueItem first = fields[destination.getPosX()][destination.getPosY()];
        first.initialize(0);
        path.add(first);
        for (int i = 0; i < path.size(); i++) {
            QueueItem field = path.get(i);
            initAdjacentFields(board, fields, field);
            List<QueueItem> adjacentFields = getAdjacentFields(board, fields, field);
            for (QueueItem adjacent : adjacentFields) {
                adjacent.counter = field.counter + 1;
            }
            adjacentFields.removeIf((item) -> item.pos.isFree() || item.counter >= field.counter);
            path.addAll(adjacentFields);
        }
        return fields;
    }

    private QueueItem[][] createMatrix(BoardInfo board) {
        QueueItem[][] fields = new QueueItem[board.getSIZE_X()][board.getSIZE_Y()];
        for (int x = 0; x < board.getSIZE_X(); x++) {
            for (int y = 0; y < board.getSIZE_Y(); y++) {
                fields[x][y] = new QueueItem(board.getFields()[x][y]);
            }
        }
        return fields;
    }

    private boolean isInBounds(BoardInfo board, int x, int y) {
        return x <= board.getSIZE_X() && y <= board.getSIZE_Y();
    }

    private List<QueueItem> getAdjacentFields(BoardInfo board, QueueItem[][] fields, QueueItem center) {
        List<QueueItem> list = new ArrayList<>(4);
        forAdjacentFields(board, fields, center, list::add);
        return list;
    }

    private void initAdjacentFields(BoardInfo info, QueueItem[][] fields, QueueItem center) {
        forAdjacentFields(info, fields, center, (field) -> field.initialize(center.counter + 1));
    }

    private void forAdjacentFields(BoardInfo board, QueueItem[][] fields, QueueItem center, Consumer<QueueItem> action) {
        for (int deltaX = -1; deltaX < 2; deltaX++) {
            if (deltaX != 0) {
                if (isInBounds(board, center.pos.getPosX() + deltaX, center.pos.getPosY())) {
                    action.accept(fields[center.pos.getPosX() + deltaX][center.pos.getPosY()]);
                }
            }
        }
        for (int deltaY = -1; deltaY < 2; deltaY++) {
            if (deltaY != 0) {
                if (isInBounds(board, center.pos.getPosX(), center.pos.getPosY() + deltaY)) {
                    action.accept(fields[center.pos.getPosX()][center.pos.getPosY() + deltaY]);
                }
            }
        }
    }
}
