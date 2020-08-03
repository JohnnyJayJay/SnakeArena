import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This class represents the Arena with the snakes
 *
 * TODO: Snake move
 */
public class Board extends JPanel {

    private static final int MAX_X = 20;
    private static final int MAX_Y = 20;
    private static final int MAX_APPLES_ON_BOARD = 2;

    private final Field[][] fields = new Field[MAX_X][MAX_Y];
    private final LinkedList<Field> apples = new LinkedList<>();
    private ArrayList<LinkedList<Field>> snakes = new ArrayList<>();

    private Game game;


    /**
     * Create Board
     */
    public Board(Game game, Snake[] snakes) {
        this.game = game;

        // declare Board
        for (int i = 0; i < MAX_X; i++) {
            for (int j = 0; j < MAX_Y; j++) {
                fields[i][j] = new Field(i, j);

            }
        }

        // snakes beginn:
        for (int i = 0; i < snakes.length; i++) {
            Field random;

            do {
                random = getRandomField();

            } while (
                    !(
                    ((random.getPosX() + 2) < MAX_X)
                    && fields[random.getPosX() + 0][random.getPosY()].isFree()
                    && fields[random.getPosX() + 1][random.getPosY()].isFree()
                    && fields[random.getPosX() + 2][random.getPosY()].isFree())
            );

            fields[random.getPosX() + 0][random.getPosY()].setFree(false);
            fields[random.getPosX() + 1][random.getPosY()].setFree(false);
            fields[random.getPosX() + 2][random.getPosY()].setFree(false);

            LinkedList<Field> snake = new LinkedList<>();
            snake.add(new Field(random.getPosX() + 0, random.getPosY()));
            snake.add(new Field(random.getPosX() + 1, random.getPosY()));
            snake.add(new Field(random.getPosX() + 2, random.getPosY()));

            this.snakes.add(snake);

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

        this.paintBoard(g2d);

        this.moveSnakes();

        this.paintSnakes(g2d);
    }


    /**
     * Draws the grid board
     *
     * @param g
     */
    private void paintBoard(Graphics2D g2d) {

        for (int x = 0; x < MAX_X; x++) {
            for (int y = 0; y < MAX_Y; y++) {
                g2d.drawRect(x * 20, y * 20, 20, 20);
            }
        }
        g2d.drawRect(20, 20, 20, 20);
    }


    /**
     * Draws the current snake position
     *
     * @param g2d
     */
    private void paintSnakes(Graphics2D g2d) {
        g2d.setColor(Color.blue); // test

        for (int i = 0; i < snakes.size(); i++) {
            for (int j = 0; j < snakes.get(i).size(); j++) {
                g2d.fillOval(snakes.get(i).get(j).getPosX() * 20,
                        snakes.get(i).get(j).getPosY() * 20, 20, 20);
            }
        }
    }


    /**
     * Moves the snakes over the board
     */
    private void moveSnakes() {
        
    }


    /**
     * remove an apple on the board and place another
     *
     * @param x x-Coordinate of apple
     * @param y y-Coordinate of apple
     * @return if an apple was removed
     */
    private boolean removeApple(int x, int y) {
        for (Field appleField : apples) {
            if ((appleField.getPosX() == x) && (appleField.getPosY() == y)) {
                fields[appleField.getPosX()][appleField.getPosY()].setApple(false);
                apples.remove(appleField);
                setApple();

                return true;
            }
        }

        return false;
    }


    /**
     * place an apple on the board
     *
     * @return if an apple was set on board
     */
    private boolean setApple() {
        if (apples.size() < MAX_APPLES_ON_BOARD) {
            Field appleField;

            do {
                appleField = getRandomField();

            } while (!fields[appleField.getPosX()][appleField.getPosY()].isFree());

            fields[appleField.getPosX()][appleField.getPosY()].setFree(false);
            fields[appleField.getPosX()][appleField.getPosY()].setApple(true);
            apples.add(appleField);
            return true;

        } else {
            return false;

        }
    }


    /**
     * Returns a random Field on the Board
     *
     * @return a random Field on the Board
     */
    private Field getRandomField() {
        int x = (int) (Math.random() * MAX_X);
        int y = (int) (Math.random() * MAX_Y);

        return new Field(x, y);
    }
}

