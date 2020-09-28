package board;

import snakes.*;
import game.Game;

import javax.swing.JPanel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class represents the Arena with the snakes
 */
public class Board extends JPanel {

    public static int SCALE;
    public static int MAX_X;
    public static int MAX_Y;
    public static int MAX_APPLES_ON_BOARD;

    private Field[][] fields;
    private final List<Field> apples = new ArrayList<>();
    private final List<LinkedList<Field>> snakesLocation = new ArrayList<>();
    private final List<Snake> snakes = new ArrayList<>();
    private final List<Field> barrier = new ArrayList<>();
    private final List<String> deadSnakesInfo = new ArrayList<>();


    private final Game game;
    private int startCounter;


    /**
     * Create Board
     */
    public Board(Game game, Snake[] snakes, int SCALE, int MAX_X, int MAX_Y, int MAX_APPLES_ON_BOARD) {
        this.game = game;
        Board.SCALE = SCALE;
        Board.MAX_X = MAX_X;
        Board.MAX_Y = MAX_Y;
        Board.MAX_APPLES_ON_BOARD = MAX_APPLES_ON_BOARD;

        fields = new Field[Board.MAX_X][Board.MAX_Y];
        // set all Fields an Board
        for (int i = 0; i < Board.MAX_X; i++) {
            for (int j = 0; j < Board.MAX_Y; j++) {
                fields[i][j] = new Field(i, j);

            }
        }

        // set all starpoints of all snakes:
        for (Snake snake : snakes) {
            Field random;

            do {
                random = getRandomField();

            } while (
                !(
                ((random.getPosX() + 2) < Board.MAX_X)
                && fields[random.getPosX()    ][random.getPosY()].isFree()
                && fields[random.getPosX() + 1][random.getPosY()].isFree()
                && fields[random.getPosX() + 2][random.getPosY()].isFree())
            );

            fields[random.getPosX()    ][random.getPosY()].setFree(false);
            fields[random.getPosX() + 1][random.getPosY()].setFree(false);
            fields[random.getPosX() + 2][random.getPosY()].setFree(false);

            var snakeLocation = new LinkedList<Field>();
            snakeLocation.addLast(new Field(random.getPosX()    , random.getPosY()));
            snakeLocation.addLast(new Field(random.getPosX() + 1, random.getPosY()));
            snakeLocation.addLast(new Field(random.getPosX() + 2, random.getPosY()));

            this.snakes.add(snake);
            this.snakesLocation.add(snakeLocation);
            this.startCounter = 2;
        }
    }


    /**
     * paints everything
     */
    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        this.paintNames(g2d);
        this.paintBoard(g2d);
        this.setApple(g2d);
        this.paintBarrier(g2d);

        // Prevent the first two unwant starts
        if (startCounter > 0) {
            this.paintSnakes(g2d);
            startCounter--;
            return;

        }

        // check if game is finish
        if (snakes.size() <= 1) {
            game.end();

        } else {
            this.moveSnakes(g2d);
        }
        this.paintSnakes(g2d);

    }


    /**
     * Draws the grid board
     *
     * @param g2d the Graphics2D, which is needed for painting
     */
    private void paintBoard(Graphics2D g2d) {

        for (int x = 0; x < MAX_X; x++) {
            for (int y = 0; y < MAX_Y; y++) {
                g2d.drawRect(x * MAX_X, y * MAX_Y, SCALE, SCALE);
            }
        }
        g2d.drawRect(MAX_X, MAX_Y, SCALE, SCALE);
    }


    /**
     * Draw names of living snakes on the right side of the board
     *
     * @param g2d the Graphics2D, which is needed for painting
     */
    private void paintNames(Graphics2D g2d) {
        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 14f));

        g2d.setColor(Color.BLACK);
        g2d.drawString("Living Snakes:", SCALE * SCALE + 100,50);

        for (int i = 0; i < this.snakes.size(); i++) {
            g2d.setColor(snakes.get(i).COLOR);
            g2d.drawString(snakes.get(i).NAME + " (" + snakesLocation.get(i).size() + ")",
                    SCALE * SCALE + 100,(50 * (i + 1)) + 50);

        }

        g2d.setColor(Color.BLACK);
        g2d.drawString("Dead Snakes:", SCALE * SCALE + 300,50);

        g2d.setColor(Color.DARK_GRAY);
        for (int i = 0; i < this.deadSnakesInfo.size(); i++) {
            g2d.drawString(this.deadSnakesInfo.get(i), SCALE * SCALE + 300,(50 * (i + 1)) + 50);
        }


    }


    /**
     * Draw an apple on board
     *
     * @param g2d the Graphics2D, which is needed for painting
     * @param x x-coordinate of the to drawn apple
     * @param y y-coordinate of the to drawn apple
     */
    private void paintApple(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.red);

        g2d.fillOval(x * MAX_X, y * MAX_Y, SCALE, SCALE);
    }


    /**
     * Draws the current snake positions
     *
     * @param g2d the Graphics2D, which is needed for painting
     */
    private void paintSnakes(Graphics2D g2d) {

        for (int i = 0; i < this.snakesLocation.size(); i++) {
            g2d.setColor(this.snakes.get(i).COLOR);

            for (int j = 0; j < this.snakesLocation.get(i).size(); j++) {
                g2d.fillOval(this.snakesLocation.get(i).get(j).getPosX() * MAX_X,
                        this.snakesLocation.get(i).get(j).getPosY() * MAX_Y, SCALE, SCALE);
            }
        }
    }


    /**
     * Paint dead snakes as barrier forever (╯▔皿▔)╯
     *
     * @param g2d the Graphics2D, which is needed for painting
     */
    private void paintBarrier(Graphics2D g2d) {
        g2d.setColor(Color.DARK_GRAY);

        for (Field barrier: this.barrier) {
            g2d.fillRect(barrier.getPosX() * MAX_X,
                    barrier.getPosY() * MAX_Y, SCALE, SCALE);
        }
    }


    /**
     * Moves the snakes over the board
     *
     * @param g2d the Graphics2D, which is needed for painting
     */
    private void moveSnakes(Graphics2D g2d) {
        int direction;
        int newX;
        int newY;

        for (int i = 0; i < this.snakes.size(); i++) {
            direction = this.snakes.get(i).think(new BoardInfo(this, i));
            newX = this.snakesLocation.get(i).getLast().getPosX();
            newY = this.snakesLocation.get(i).getLast().getPosY();

            // check if snake is allowed to move in this direction
            if ((direction == Snake.LEFT) && (newX > 0)) {
                --newX;

            } else if ((direction == Snake.UP) && (newY > 0)) {
                --newY;

            } else if ((direction == Snake.RIGHT) && (newX < (MAX_X - 1))) {
                ++newX;

            } else if ((direction == Snake.DOWN) && (newY < (MAX_Y - 1))) {
                ++newY;

            } else {
                System.out.println("snakes.Snake " + this.snakes.get(i).NAME + " returns no correct direction " +
                        "or drive in a border");
                killSnake(g2d, i);
                i--;
                continue;
            }

            // check if snake run in another snake/barrier or in an apple
            boolean ate = false;
            if (!this.fields[newX][newY].isFree()) {

                if (this.fields[newX][newY].isApple()) {
                    removeApple(g2d, newX, newY);
                    ate = true;

                } else {
                    killSnake(g2d, i);
                    i--;
                    continue;

                }
            }


            // set Fields -> TODO: make better
            int oldX = this.snakesLocation.get(i).getFirst().getPosX();
            int oldY = this.snakesLocation.get(i).getFirst().getPosY();

            this.fields[newX][newY].setFree(false);
            this.fields[oldX][oldY].setFree(true);

            // move snake
            this.snakesLocation.get(i).addLast(new Field(newX, newY));

            if (!ate) {
                this.snakesLocation.get(i).removeFirst();
            }
        }
    }


    /**
     * Use epic kame hame ha power to kill a snake and turn it to a barrier
     *
     * @param g2d the Graphics2D, which is needed for painting
     * @param snakeIndex snake to be killed
     */
    private void killSnake(Graphics2D g2d, int snakeIndex) {
        barrier.addAll(this.snakesLocation.get(snakeIndex));
        deadSnakesInfo.add(snakes.get(snakeIndex).NAME + " (" + snakesLocation.get(snakeIndex).size() + ")");

        this.paintBarrier(g2d);

        this.snakesLocation.remove(snakeIndex);
        this.snakes.remove(snakeIndex);

    }


    /**
     * remove an apple on the board and place another
     *
     * @param g2d the Graphics2D, which is needed for painting
     * @param x x-Coordinate of apple
     * @param y y-Coordinate of apple
     * @return if an apple was removed
     */
    private boolean removeApple(Graphics2D g2d, int x, int y) {
        for (Field appleField : apples) {
            if ((appleField.getPosX() == x) && (appleField.getPosY() == y)) {

                // no .setFree here, because a snake will be on this field
                fields[appleField.getPosX()][appleField.getPosY()].setApple(false);
                apples.remove(appleField);
                this.setApple(g2d);

                return true;
            }
        }

        return false;
    }


    /**
     * place an apple on the board
     *
     * @param g2d the Graphics2D, which is needed for painting
     */
    private void setApple(Graphics2D g2d) {
        while (apples.size() < MAX_APPLES_ON_BOARD) {
            Field appleField;

            do {
                appleField = getRandomField();

            } while (!fields[appleField.getPosX()][appleField.getPosY()].isFree());

            fields[appleField.getPosX()][appleField.getPosY()].setFree(false);
            fields[appleField.getPosX()][appleField.getPosY()].setApple(true);
            apples.add(appleField);
        }


        // paint apples
        for (Field apple : apples) {
            this.paintApple(g2d, apple.getPosX(), apple.getPosY());
        }
    }


    /**
     * Returns a random Board.Field on the Board.Board
     *
     * @return a random Board.Field on the Board.Board
     */
    protected Field getRandomField() {
        int x = (int) (Math.random() * MAX_X);
        int y = (int) (Math.random() * MAX_Y);

        return new Field(x, y);
    }

    protected Field[][] getFields() {
        return fields;
    }

    protected List<Field> getApples() {
        return apples;
    }

    protected List<LinkedList<Field>> getSnakesLocation() {
        return snakesLocation;
    }

    protected List<Snake> getSnakes() {
        return snakes;
    }

    protected List<Field> getBarrier() {
        return barrier;
    }
}

