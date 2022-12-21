import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.io.FileReader;
import java.io.BufferedReader;

public class Board extends JPanel {
    int count = 0;
    CountDownTimerExample countDown;

    private class CountDownTimerExample {
        // declare timer t
        int a;
        Timer t;

        // constructor of the class
        public void stopCountDown() {
            t.cancel();
        }

        public CountDownTimerExample(int seconds) {
            a = seconds;
            t = new Timer();
            // schedule the timer
            t.schedule(tick, 0, 1000);
        }

        // sub class that extends TimerTask
        TimerTask tick = new TimerTask() {
            @Override
            public void run() {
                if (count > TIME - a) {
                    count -= 1;
                    repaint();
                    System.out.println("tick... now counter is " + count);
                    statusbar.setText("mines left: " + Integer.toString(minesLeft) + " Time: " + count);
                } else {
                    System.out.println("Seconds you have input is over..!!! ");
                    inGame = false;
                    timeElapsed = true;
                    repaint();
                    t.cancel();
                }

            }
        };

    }

    private final int NUM_IMAGES = 15;
    private final int CELL_SIZE = 15;
    private final int COVER_FOR_CELL = 11;
    private final int ULTRABOMB_CELL = 10;
    private final int MARK_FOR_CELL = 11;
    private final int EMPTY_CELL = 0;
    private final int MINE_CELL = 9;
    private final int UNCOVERED_MINE_FROM_SPECIAL = 100;
    private final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private final int COVERED_ULTRABOMB_CELL = ULTRABOMB_CELL + COVER_FOR_CELL;
    private final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;
    private final int MARKED_ULTRABOMB_CELL = COVERED_ULTRABOMB_CELL + MARK_FOR_CELL;
    private final int DRAW_MINE = 9;
    private final int DRAW_ULTRABOMB = 14;
    private final int DRAW_COVER = 10;
    private final int DRAW_MARK = 11;
    private final int DRAW_WRONG_MARK = 12;
    private final int DRAW_UNCOVERED_MARK = 13;

    private int N_COLS;
    private int N_ROWS;
    private int BOARD_WIDTH;
    private int BOARD_HEIGHT;
    private int N_MINES;
    private boolean specialMove = false;
    private int TIME;
    private boolean ULTRABOMBEXIST = false;
    private boolean timeElapsed = false;
    private int ultrabombPosition;
    private int tryNum;
    private int[] field;
    private boolean inGame;
    private int minesLeft;
    private Image[] img;
    private boolean freshGame = false;

    private int allCells;
    private final JLabel statusbar;

    public Board(JLabel statusbar, int N_MINES, int N_COLS, int N_ROWS, int TIME, int ULTRABOMB) {

        this.N_MINES = N_MINES;
        this.N_COLS = N_COLS;
        this.N_ROWS = N_ROWS;
        this.TIME = TIME;
        if (ULTRABOMB == 1) {
            this.ULTRABOMBEXIST = true;
        } else {
            this.ULTRABOMBEXIST = false;
        }
        this.BOARD_WIDTH = N_COLS * CELL_SIZE + 1;
        this.BOARD_HEIGHT = N_ROWS * CELL_SIZE + 1;
        this.statusbar = statusbar;
        System.out.println("N_MINES: " + N_MINES + "N_COLS: " + N_COLS + "N_ROWS: " + N_ROWS + "TIME: "
                + TIME + "ULTRABOMB: " + ULTRABOMB + "this.N_MINES:" + this.N_MINES + "this.N_COLS:" + this.N_COLS
                + "this.N_ROWS:" + this.N_ROWS + "this.N_ROWS:" + this.N_ROWS + "this.ULTRABOMB:" + this.ULTRABOMB_CELL
                + "this.TIME:" + this.TIME);

        initBoard();

    }

    private void specialMove() {

        int ultrabombCol = ultrabombPosition % N_COLS;
        int ultrabombRow = (int) ultrabombPosition / N_COLS;
        for (int i = 0; i < N_COLS; i++) {
            System.out.print("field[i+N_COLS*ultrabombRow] was: " + field[i + N_COLS * ultrabombRow]);
            if (i + N_COLS * ultrabombRow != ultrabombPosition) {
                if (field[i + N_COLS * ultrabombRow] == COVERED_MINE_CELL) {
                    field[i + N_COLS * ultrabombRow] = UNCOVERED_MINE_FROM_SPECIAL;
                } else if (field[i + N_COLS * ultrabombRow] > ULTRABOMB_CELL
                        && field[i + N_COLS * ultrabombRow] <= COVERED_ULTRABOMB_CELL) {

                    field[i + N_COLS * ultrabombRow] -= COVER_FOR_CELL;

                }
                System.out.print(" field[i+N_COLS*ultrabombRow] now is: " + field[i + N_COLS * ultrabombRow] + "\n");
            } else {
                System.out.print(" no changes" + "\n");
            }
        }
        for (int i = 0; i < N_ROWS; i++) {
            System.out.print("field[i*N_COLS+ultrabombCol] was: " + field[i * N_COLS + ultrabombCol]);
            if (i * N_COLS + ultrabombCol != ultrabombPosition) {
                if (field[i * N_COLS + ultrabombCol] == COVERED_MINE_CELL) {
                    field[i * N_COLS + ultrabombCol] = UNCOVERED_MINE_FROM_SPECIAL;
                } else if (field[i * N_COLS + ultrabombCol] > ULTRABOMB_CELL
                        && field[i * N_COLS + ultrabombCol] <= COVERED_ULTRABOMB_CELL) {

                    field[i * N_COLS + ultrabombCol] -= COVER_FOR_CELL;

                }

                System.out.print("  field[i*N_COLS+ultrabombCol] now is: " + field[i * N_COLS + ultrabombCol] + "\n");
            } else {
                System.out.print(" no changes" + "\n");
            }
        }
        for (int i = 0; i < N_ROWS; i++) {

            for (int j = 0; j < N_COLS; j++) {

                int cell = field[(i * N_COLS) + j];
                System.out.print(cell + "|");
            }
            System.out.print("\n|");
        }

    }

    private void initBoard() {

        /* System.out.print("focus came through here"); */
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        img = new Image[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {

            var path = "src/resources/img/" + i + ".png";
            img[i] = (new ImageIcon(path)).getImage();
        }

        addMouseListener(new MinesAdapter());
        newGame();
    }

    public void newGame() {
        freshGame = true;
        tryNum = 0;
        count = TIME + 1;
        countDown = new CountDownTimerExample(TIME);
        /*
         * t = new CountDownTimer();
         * t.CountDownTimer(10);
         */
        int cell;

        var random = new Random();
        inGame = true;
        minesLeft = N_MINES;

        allCells = N_ROWS * N_COLS;
        field = new int[allCells];

        for (int i = 0; i < allCells; i++) {

            field[i] = COVER_FOR_CELL;
        }

        int minetype = 0;
        int i = 0;
        try {
            FileWriter myWriter = new FileWriter("src/resources/medialab/mines.txt");
            while (i < N_MINES) {

                int position = (int) (allCells * random.nextDouble());
                if ((position < allCells)
                        && (field[position] != COVERED_MINE_CELL)) {
                    int current_col = position % N_COLS;

                    if (ULTRABOMBEXIST == true && i == N_MINES - 1) {

                        minetype = 1;
                        ultrabombPosition = position;
                        field[ultrabombPosition] = COVERED_ULTRABOMB_CELL;

                    } else {
                        field[position] = COVERED_MINE_CELL;
                    }

                    i++;
                    myWriter.write((int) position / N_ROWS + "," + current_col + "," + minetype + "\n");
                    if (current_col > 0) {
                        cell = position - 1 - N_COLS;
                        if (cell >= 0) {
                            if (field[cell] != COVERED_MINE_CELL) {
                                field[cell] += 1;
                            }
                        }
                        cell = position - 1;
                        if (cell >= 0) {
                            if (field[cell] != COVERED_MINE_CELL) {
                                field[cell] += 1;
                            }
                        }
                        cell = position + N_COLS - 1;
                        if (cell < allCells) {
                            if (field[cell] != COVERED_MINE_CELL) {
                                field[cell] += 1;
                            }
                        }
                    }
                    cell = position - N_COLS;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + N_COLS;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    if (current_col < (N_COLS - 1)) {
                        cell = position - N_COLS + 1;
                        if (cell >= 0) {
                            if (field[cell] != COVERED_MINE_CELL) {
                                field[cell] += 1;
                            }
                        }
                        cell = position + N_COLS + 1;
                        if (cell < allCells) {
                            if (field[cell] != COVERED_MINE_CELL) {
                                field[cell] += 1;
                            }
                        }
                        cell = position + 1;
                        if (cell < allCells) {
                            if (field[cell] != COVERED_MINE_CELL) {
                                field[cell] += 1;
                            }
                        }
                    }
                }
            }

            myWriter.close();
            System.out.println("Successfully loged the mines in medialab.txt");

        } catch (IOException e) {
            System.out.println("An error occurred while logging the mines in medialab.txt.");
            e.printStackTrace();
        }

    }

    public void setIngameFalse() {
        inGame = false;
        countDown.stopCountDown();
        repaint();
    }

    private void find_empty_cells(int j) {

        int current_col = j % N_COLS;
        int cell;

        if (current_col > 0) {
            cell = j - N_COLS - 1;
            if (cell >= 0) {
                if (field[cell] > ULTRABOMB_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j - 1;
            if (cell >= 0) {
                if (field[cell] > ULTRABOMB_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + N_COLS - 1;
            if (cell < allCells) {
                if (field[cell] > ULTRABOMB_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }
        }

        cell = j - N_COLS;
        if (cell >= 0) {
            if (field[cell] > ULTRABOMB_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL) {
                    find_empty_cells(cell);
                }
            }
        }

        cell = j + N_COLS;
        if (cell < allCells) {
            if (field[cell] > ULTRABOMB_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL) {
                    find_empty_cells(cell);
                }
            }
        }

        if (current_col < (N_COLS - 1)) {
            cell = j - N_COLS + 1;
            if (cell >= 0) {
                if (field[cell] > ULTRABOMB_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + N_COLS + 1;
            if (cell < allCells) {
                if (field[cell] > ULTRABOMB_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + 1;
            if (cell < allCells) {
                if (field[cell] > ULTRABOMB_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        System.out.print("\n" + "Starting paintComponent. field looks like this: " + "\n");
        int uncover = 0;

        for (int i = 0; i < N_ROWS; i++) {

            for (int j = 0; j < N_COLS; j++) {

                int cell = field[(i * N_COLS) + j];
                System.out.print(cell + "|");
                if (inGame && cell == MINE_CELL || cell == ULTRABOMB_CELL) {

                    inGame = false;

                }

                if (!inGame) {

                    if (cell == COVERED_MINE_CELL || cell == UNCOVERED_MINE_FROM_SPECIAL) {
                        cell = DRAW_MINE;
                    } else if (cell == COVERED_ULTRABOMB_CELL) {
                        cell = DRAW_ULTRABOMB;
                    } else if (cell == MARKED_MINE_CELL || cell == MARKED_ULTRABOMB_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_WRONG_MARK;
                    } else if (cell > ULTRABOMB_CELL) {
                        cell = DRAW_COVER;
                    }

                } else {
                    if (cell == UNCOVERED_MINE_FROM_SPECIAL) {
                        cell = DRAW_UNCOVERED_MARK;
                    } else if (cell > COVERED_ULTRABOMB_CELL) {
                        cell = DRAW_MARK;

                    } else if (cell > ULTRABOMB_CELL) {
                        cell = DRAW_COVER;
                        uncover++;
                    }
                }

                g.drawImage(img[cell], (j * CELL_SIZE),
                        (i * CELL_SIZE), this);
            }
            System.out.print("\n");
        }

        int x = TIME - count;
        if (uncover == 0 && inGame && freshGame) {
            freshGame = false;
            setIngameFalse();
            statusbar.setText("Game won");
            resultLog(x, true);

        } else if (!inGame && freshGame) {
            freshGame = false;
            setIngameFalse();
            if (timeElapsed == true) {
                statusbar.setText("Game lost (time elapsed)" + " total time: " + x);
            } else {
                statusbar.setText("Game lost (kaboom)" + " total time: " + x);
            }
            resultLog(x, false);
        }

    }

    private void resultLog(int x, boolean won) {
        int linesNo = 0;
        String data = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/medialab/resultLog.txt"))) {
            data = reader.readLine();
            while (data != null) {
                data += "\n";
                linesNo++;
                System.out.println(data);
                if (linesNo == 4)
                    break;
                data += reader.readLine();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("adding trynum: " + tryNum);
            FileWriter myWriter = new FileWriter("src/resources/medialab/resultLog.txt");

            myWriter.write("Mines: " + N_MINES + " click number: " + tryNum + " time elapsed: " + x + " Won: " + won
                    + "\n" + data);

            myWriter.close();
            System.out.println("Successfully loged game history in resultLog.txt");

        } catch (IOException e) {
            System.out.println("An error occurred while logging the mines in medialab.txt.");
            e.printStackTrace();
        }
        if (linesNo < 5) {

        }

    }

    private class MinesAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {

            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            boolean doRepaint = false;

            if (!inGame) {

                newGame();
                revalidate();
                repaint();

            }

            if ((x < N_COLS * CELL_SIZE) && (y < N_ROWS * CELL_SIZE)) {

                if (e.getButton() == MouseEvent.BUTTON3) {

                    if (field[(cRow * N_COLS) + cCol] > ULTRABOMB_CELL) {
                        tryNum += 1;
                        System.out.println("trynum incremented 1. now is: " + tryNum);
                        doRepaint = true;

                        if (field[(cRow * N_COLS) + cCol] <= COVERED_ULTRABOMB_CELL) {

                            if (minesLeft > 0) {
                                if (tryNum <= 4 && field[(cRow * N_COLS) + cCol] == COVERED_ULTRABOMB_CELL) {
                                    System.out.print("field[(cRow * N_COLS) + cCol]= " + field[(cRow * N_COLS) + cCol]
                                            + "COVERED_ULTRABOMB_CELL= " + COVERED_ULTRABOMB_CELL + "tryNum= " + tryNum
                                            + "\n");
                                    specialMove();
                                }
                                field[(cRow * N_COLS) + cCol] += MARK_FOR_CELL;
                                minesLeft--;
                                statusbar.setText("mines left: " + Integer.toString(minesLeft) + " Time: " + count);

                            } else {
                                statusbar.setText("No marks left");
                            }
                        } else {

                            field[(cRow * N_COLS) + cCol] -= MARK_FOR_CELL;
                            minesLeft++;
                            statusbar.setText("mines left: " + Integer.toString(minesLeft) + " Time: " + count);
                        }
                    }

                } else {

                    tryNum += 1;
                    System.out.println("trynum incremented 2. now is: " + tryNum);

                    if (field[(cRow * N_COLS) + cCol] == UNCOVERED_MINE_FROM_SPECIAL) {
                        field[(cRow * N_COLS) + cCol] = MINE_CELL;
                        doRepaint = true;
                        inGame = false;

                        countDown.stopCountDown();

                    } else if (field[(cRow * N_COLS) + cCol] > COVERED_ULTRABOMB_CELL) {

                        return;
                    } else if ((field[(cRow * N_COLS) + cCol] > ULTRABOMB_CELL)) {

                        field[(cRow * N_COLS) + cCol] -= COVER_FOR_CELL;
                        doRepaint = true;

                        if (field[(cRow * N_COLS) + cCol] == MINE_CELL
                                || field[(cRow * N_COLS) + cCol] == ULTRABOMB_CELL && !specialMove) {
                            System.out.print("field[(cRow * N_COLS) + cCol]= " + field[(cRow * N_COLS) + cCol]
                                    + "MINE_CELL= " + MINE_CELL + "ULTRABOMB_CELL= " + ULTRABOMB_CELL + "specialMove= "
                                    + specialMove + "\n");
                            inGame = false;

                            countDown.stopCountDown();

                        }

                        if (field[(cRow * N_COLS) + cCol] == EMPTY_CELL) {
                            find_empty_cells((cRow * N_COLS) + cCol);
                        }
                    }
                }

                if (doRepaint) {

                    repaint();

                }

            }
        }
    }
}
