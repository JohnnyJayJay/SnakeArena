package snakes;

import board.BoardInfo;
import board.Field;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;


/**
 * This is the snake which has to be filled with lovely intelligence code
 * to become the longest snake of all other snakes.
 * <p>
 * This snake appears 'MySnake' times an board.
 * The value behind 'MySnake' can set in the property file (snake_arena.properties).
 */
public class MySnake extends Snake {

    public MySnake() {
        this.NAME = "Python";         // set your favorite name
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
        int direction = chooseDirection(board);

        //System.out.printf("time taken to think: %d\n", System.currentTimeMillis() - start);

        return direction; // or LEFT, or DOWN, or UP
    }


    private int survive(BoardInfo board) {
        //System.out.println("SURVIVE");
        int direction = UP;
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
        Field pos;
        int counter;
        boolean initialized;

        QueueItem(Field pos) {
            this.pos = pos;
            counter = -1;
            initialized = false;
        }

        void setCounter(int counter) {
            initialized = true;
            this.counter = counter;
        }

        @Override
        public String toString() {
            return "(" + pos.getPosX() + "," + pos.getPosY() + "," + counter + ")";
        }
    }

    // FIXME apparently always survive
    private int chooseDirection(BoardInfo board) {
        return board.getApples().stream()
                .map((apple) -> findPath(board, apple))
                .filter(Objects::nonNull)
                .min(Comparator.comparingInt(List::size))
                .map((path) -> getDirection(board, board.getOwnHead(), path.get(0).pos))
                .orElse(survive(board));
    }

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
                    .orElse(null);
            if (current == null) {
                return null;
            }
            path.add(current);
        }
        return path;
    }

    private QueueItem[][] assessField(BoardInfo board, Field destination) {
        QueueItem[][] fields = createMatrix(board);
        List<QueueItem> path = new ArrayList<>();
        QueueItem first = fields[destination.getPosX()][destination.getPosY()];
        first.setCounter(0);
        path.add(first);
        for (int i = 0; i < path.size(); i++) {
            QueueItem field = path.get(i);
            initAdjacentFields(board, fields, field);
            List<QueueItem> adjacentFields = getAdjacentFields(board, fields, field);
            adjacentFields.removeIf((item) -> item.pos.isFree() || path.contains(item));
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
        return x >= 0 && x < board.getSIZE_X() && y >= 0 && y < board.getSIZE_Y();
    }

    private List<QueueItem> getAdjacentFields(BoardInfo board, QueueItem[][] fields, QueueItem center) {
        List<QueueItem> list = new ArrayList<>(4);
        forAdjacentFields(board, fields, center, list::add);
        return list;
    }

    private void initAdjacentFields(BoardInfo info, QueueItem[][] fields, QueueItem center) {
        forAdjacentFields(info, fields, center, (field) -> {
            if (!field.initialized || center.counter + 1 < field.counter) {
                field.setCounter(center.counter + 1);
            }
        });
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


    private int getDirection(BoardInfo board, Field start, Field destination) {
        if (start.getPosX() + 1 == destination.getPosX()) {
            return RIGHT;
        } else if (start.getPosX() - 1 == destination.getPosX()) {
            return LEFT;
        } else if (start.getPosY() - 1 == destination.getPosY()) {
            return UP;
        } else if (start.getPosY() + 1 == destination.getPosX()) {
            return DOWN;
        } else {
            System.out.print("\n***************************\nFields are not next to each other\n***************************\n");
            return survive(board);
        }
    }
}
