package snakes;

import board.*;
import java.awt.*;


/**
 * This is the snake which has to be filled with lovely intelligence code
 * to become the longest snake of all other snakes.
 *
 * This snake appears 'MySnake' times an board.
 * The value behind 'MySnake' can set in the property file (snake_arena.properties).
 */
public class MySnake extends Snake {

    private int direction = RIGHT;
    Field[][] fields;


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
        this.fields = board.getFields();



        try {
            direction = chosePath(board, createQueues(board)[0]);
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

        public QueueItem(Field pos, int counter) {
            this.pos = pos;
            this.counter = counter;
        }
    }


    /*
     * Function to create a Matrix of all fields and their distance of the way to the destination
     */
    QueueItem[][] addItem(QueueItem[][] queue, QueueItem item, int counter, BoardInfo board) {
        queue[item.pos.getPosX()][item.pos.getPosY()] = item;

        QueueItem newItem;

        /*
         * add item on the left
         */
        try {
            newItem = new QueueItem(this.fields[item.pos.getPosX() - 1][item.pos.getPosY()], counter + 1);
            if (newItem.pos.isFree()) {
                if (queue[newItem.pos.getPosX()][newItem.pos.getPosY()] == null) {
                    queue = addItem(queue, newItem, counter + 1, board);
                } else if (queue[newItem.pos.getPosX()][newItem.pos.getPosY()].counter > newItem.counter) {
                    queue = addItem(queue, newItem, counter + 1, board);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {}

        /*
         * add item on the right
         */
        try {
            newItem = new QueueItem(this.fields[item.pos.getPosX() + 1][item.pos.getPosY()], counter + 1);
            if (newItem.pos.isFree()) {
                if (queue[newItem.pos.getPosX()][newItem.pos.getPosY()] == null) {
                    queue = addItem(queue, newItem, counter + 1, board);
                } else if (queue[newItem.pos.getPosX()][newItem.pos.getPosY()].counter > newItem.counter) {
                    queue = addItem(queue, newItem, counter + 1, board);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {}

        /*
         * add item below
         */
        try {
            newItem = new QueueItem(this.fields[item.pos.getPosX()][item.pos.getPosY() - 1], counter + 1);
            if (newItem.pos.isFree()) {
                if (queue[newItem.pos.getPosX()][newItem.pos.getPosY()] == null) {
                    queue = addItem(queue, newItem, counter + 1, board);
                } else if (queue[newItem.pos.getPosX()][newItem.pos.getPosY()].counter > newItem.counter) {
                    queue = addItem(queue, newItem, counter + 1, board);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {}

        /*
         * add item above
         */
        try {
            newItem = new QueueItem(this.fields[item.pos.getPosX()][item.pos.getPosY() + 1], counter + 1);
            if (newItem.pos.isFree()) {
                if (queue[newItem.pos.getPosX()][newItem.pos.getPosY()] == null) {
                    queue = addItem(queue, newItem, counter + 1, board);
                } else if (queue[newItem.pos.getPosX()][newItem.pos.getPosY()].counter > newItem.counter) {
                    queue = addItem(queue, newItem, counter + 1, board);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {}

        return queue;
    }


    /*
     * Algorithm to find a path to the nearest apple
     * https://en.wikipedia.org/wiki/Pathfinding#:~:text=%20Algorithms%20used%20in%20pathfinding%20%201%20A,not%20restricted%20to%20move%20along%20the...%20More%20
     */
    private QueueItem[][][] createQueues(BoardInfo board) {
        Field[][] fields = board.getFields();

        Field[] applePos = board.getApples().toArray(new Field[0]);

        QueueItem[][][] queues = new QueueItem[applePos.length][board.getMAX_X()][board.getMAX_Y()];

        for (int i = 0; i < applePos.length; i++) {
            queues[i] = new QueueItem[fields.length][fields[0].length];
            QueueItem destination = new QueueItem(fields[applePos[i].getPosX()][applePos[i].getPosY()], 0);
            queues[i][destination.pos.getPosX()][destination.pos.getPosY()] = destination;
            queues[i] = addItem(queues[i], destination, 0, board);
        }



//        for (QueueItem[] column : queueMtrx) {
//            for (QueueItem item: column) {
//                if (item == null) {
//                    System.out.printf("[ XX ]");
//                } else {
//                    System.out.printf("[ %s%d ]", (item.counter < 10) ? " " : "", item.counter);
//                }
//            }
//            System.out.println();
//        }


        return queues;
    }

    /**
     * chose the path based on the Matrix
     *
     * @param board     the whole board with every information necessary
     * @param queueMtrx a Matrix of the fields filled with the needed amount of steps to the next apple
     * @return direction in which the snake should move
     */
    private int chosePath(BoardInfo board, QueueItem[][] queueMtrx) {
//        int direction;
        int xMaxIndex = fields.length - 1;
        int yMaxIndex = fields[0].length - 1;

        Field headPos = board.getOwnHead();

        int upCounter = queueMtrx.length * queueMtrx[0].length;
        int downCounter = queueMtrx.length * queueMtrx[0].length;
        int rightCounter = queueMtrx.length * queueMtrx[0].length;
        int leftCounter = queueMtrx.length * queueMtrx[0].length;


        if (headPos.getPosY() < yMaxIndex) {
            if (fields[headPos.getPosX()][headPos.getPosY() + 1].isFree()) {
                downCounter = queueMtrx[headPos.getPosX()][headPos.getPosY() + 1].counter;
            }
        }
        if (headPos.getPosY() > 0) {
            if (fields[headPos.getPosX()][headPos.getPosY() - 1].isFree()) {
                upCounter = queueMtrx[headPos.getPosX()][headPos.getPosY() - 1].counter;
            }
        }
        if (headPos.getPosX() < xMaxIndex) {
            if (fields[headPos.getPosX() + 1][headPos.getPosY()].isFree()) {
                rightCounter = queueMtrx[headPos.getPosX() + 1][headPos.getPosY()].counter;
            }
        }
        if (headPos.getPosX() > 0) {
            if (fields[headPos.getPosX() - 1][headPos.getPosY()].isFree()) {
                leftCounter = queueMtrx[headPos.getPosX() - 1][headPos.getPosY()].counter;
            }
        }

//        System.out.printf("x: %d, y: %d\n", headPos.getPosX(), headPos.getPosY());
//        System.out.printf("upCounter:\t\t%d\ndownCounter:\t%d\nrightCounter:\t%d\nleftCounter:\t%d\n-----------------\n", upCounter, downCounter, rightCounter, leftCounter);


        if (upCounter <= downCounter && upCounter <= rightCounter && upCounter <= leftCounter) {
            return UP;
        } else if (downCounter <= upCounter && downCounter <= rightCounter && downCounter <= leftCounter) {
            return DOWN;
        } else if (rightCounter <= upCounter && rightCounter <= downCounter && rightCounter <= leftCounter) {
            return RIGHT;
//        } else if (leftCounter <= upCounter && leftCounter <= downCounter && leftCounter <= rightCounter) {
//            return LEFT;
        } else {
            return LEFT;
        }
    }
}
