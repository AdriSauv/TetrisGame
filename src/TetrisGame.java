import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class TetrisGame extends JPanel {
    private static final long serialVersionUID = 1L;

    private final Point[][][] Tetraminos = {
            // I piece
            {
                {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)},
                {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)}
            },
            // J piece
            {
                {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2)},
                {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2)}
            },
            // L piece
            {
                {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2)},
                {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0)}
            },
            // O piece
            {
                {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)}
            },
            // S piece
            {
                {new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
                {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
                {new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
                {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)}
            },
            // T piece
            {
                {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)},
                {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
                {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2)},
                {new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2)}
            },
            // Z piece
            {
                {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
                {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)},
                {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
                {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)}
            }
    };

    private final Color[] tetraminoColors = {
        Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.pink, Color.red
    };

    private Point pieceOrigin;
    private int currentPiece;
    private int rotation;
    private ArrayList<Integer> nextPieces = new ArrayList<Integer>();

    private long score;
    private Color[][] well;

    // Creates a border around the well and initializes the dropping piece
    public void newPiece() {
        pieceOrigin = new Point(5, 2);
        rotation = 0;
        if (nextPieces.isEmpty()) {
            for (int i = 0; i < 100; i++) {
                nextPieces.add((int)(Math.random() * 7));
            }
        }
        currentPiece = nextPieces.remove(0);
        nextPieces.add((int)(Math.random() * 7));
    }

    // Collision test for the dropping piece
    private boolean collidesAt(int x, int y, int rotation) {
        for (Point p : Tetraminos[currentPiece][rotation]) {
            if (well[p.x + x][p.y + y] != null) {
                return true;
            }
        }
        return false;
    }

    // Rotate the piece clockwise or counterclockwise
    public void rotate(int i) {
        int newRotation = (rotation + i) % 4;
        if (newRotation < 0) {
            newRotation = 3;
        }
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) {
            rotation = newRotation;
        }
        repaint();
    }

    // Move the piece left or right
    public void move(int i) {
        if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
            pieceOrigin.x += i;
        }
        repaint();
    }

    // Drops the piece one line or fixes it to the well if it can't drop
    public void dropDown() {
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
            pieceOrigin.y += 1;
        } else {
            fixToWell();
        }
        repaint();
    }

    // Make the dropping piece part of the well, so it is drawn out
    public void fixToWell() {
        for (Point p : Tetraminos[currentPiece][rotation]) {
            well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[currentPiece];
        }
        clearRows();
        newPiece();
    }

    public void deleteRow(int row) {
        for (int j = row-1; j > 0; j--) {
            for (int i = 1; i < 11; i++) {
                well[i][j+1] = well[i][j];
            }
        }
    }

    // Clear completed rows from the field and award score according to
    // the number of simultaneously cleared rows
    public void clearRows() {
        boolean gap;
        int numClears = 0;

        for (int j = 21; j > 0; j--) {
            gap = false;
            for (int i = 1; i < 11; i++) {
                if (well[i][j] == null) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {
                deleteRow(j);
                j += 1;
                numClears += 1;
            }
        }

        switch (numClears) {
            case 1:
                score += 100;
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 500;
                break;
            case 4:
                score += 800;
                break;
        }
    }

    // Draw the falling piece
    private void drawPiece(Graphics g) {
        g.setColor(tetraminoColors[currentPiece]);
        for (Point p : Tetraminos[currentPiece][rotation]) {
            g.fillRect((p.x + pieceOrigin.x) * 26,
                       (p.y + pieceOrigin.y) * 26,
                       25, 25);
        }
    }

    // Initialize the well
    private void init() {
        well = new Color[12][24];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                if (i == 0 || i == 11 || j == 22) {
                    well[i][j] = Color.GRAY;
                } else {
                    well[i][j] = null;
                }
            }
        }
        newPiece();
    }

    // Draw the well
    private void drawWell(Graphics g) {
        // Draw the well
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                g.setColor(well[i][j]);
                g.fillRect(26*i, 26*j, 25, 25);
            }
        }

        // Draw the well border
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 26*12, 26*23);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        // Paint the well
        drawWell(g);
        // Paint the falling piece
        drawPiece(g);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Tetris");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(12*26+10, 26*23+25);
        f.setVisible(true);

        final TetrisGame game = new TetrisGame();
        game.init();
        f.add(game);

        // Keyboard controls
        f.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        game.rotate(-1);
                        break;
                    case KeyEvent.VK_DOWN:
                        game.rotate(1);
                        break;
                    case KeyEvent.VK_LEFT:
                        game.move(-1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        game.move(1);
                        break;
                    case KeyEvent.VK_SPACE:
                        game.dropDown();
                        game.score += 1;
                        break;
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        // Make the falling piece drop every second
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        game.dropDown();
                    } catch ( InterruptedException e ) {}
                }
            }
        }.start();
    }

}
