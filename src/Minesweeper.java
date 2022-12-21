
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.*;
import javax.swing.*;
import static javax.swing.JOptionPane.showMessageDialog;
import java.util.Scanner;
import java.io.File;
import java.io.IOException; // Import the IOException class to handle errors
import java.io.FileWriter;
import java.io.FileNotFoundException;

public class Minesweeper extends JFrame implements ActionListener {

    private JLabel statusbar;
    private JMenuBar menubar;
    private JMenu Application;
    private JMenu Details;
    private JMenuItem Create;
    private JMenuItem Load;
    private JMenuItem Start;
    private JMenuItem Rounds;
    private JMenuItem Solution;
    private JMenuItem Exit;

    private Board gameInstance;
    private int N_MINES;
    private int N_COLS;
    private int N_ROWS;
    private int TIME;
    private int ULTRABOMB = 0;
    private int DIFFICULTY;

    public Minesweeper() {

        initUI();
    }

    /*
     * public Minesweeper(Board a)
     * {
     * gameInstance = a;
     * }
     * 
     * void I_AM_a_button_press() // function
     * {
     * gameInstance.setIngameFalse();
     * }
     */
    private void initUI() {

        menubar = new JMenuBar();
        Application = new JMenu("Application");
        Create = new JMenuItem("Create");
        Application.add(Create);
        Create.addActionListener(this);
        Load = new JMenuItem("Load");
        Application.add(Load);
        Load.addActionListener(this);
        Start = new JMenuItem("Start");
        Start.setEnabled(false);
        Application.add(Start);
        Start.addActionListener(this);
        Exit = new JMenuItem("Exit");
        Application.add(Exit);
        Exit.addActionListener(this);
        menubar.add(Application);
        Details = new JMenu("Details");
        Rounds = new JMenuItem("Rounds");
        Details.add(Rounds);
        Rounds.addActionListener(this);
        Solution = new JMenuItem("Solution");
        Details.add(Solution);
        Solution.addActionListener(this);
        Solution.setEnabled(false);
        menubar.add(Details);
        setJMenuBar(menubar);
        statusbar = new JLabel("Plz set up game..");
        add(statusbar, BorderLayout.SOUTH);

        setResizable(true);
        setSize(240, 240);
        /* pack(); */
        setTitle("MediaLab Minesweeper");

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* System.out.print(); */
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            var ex = new Minesweeper();
            ex.setVisible(true);
        });
    }

    public void onCreate() {
        String[] difficultyOptions = { "Easy", "Hard" };
        /* JComboBox difficultyComboBox; */
        JComboBox<String> difficultyComboBox;
        JTextField time = new JTextField();
        JTextField bombs = new JTextField();
        JCheckBox checkBox = new JCheckBox("Add Ultrabomb?", false);
        JPanel myDifPanel = new JPanel();
        myDifPanel.setLayout(new BoxLayout(myDifPanel, BoxLayout.Y_AXIS));
        myDifPanel.add(Box.createVerticalStrut(10)); // a spacer
        myDifPanel.add(new JLabel("Difficulty:"));
        difficultyComboBox = new JComboBox<>(difficultyOptions);
        myDifPanel.add(difficultyComboBox);
        int result1 = JOptionPane.showConfirmDialog(null, myDifPanel,
                "Please select the difficulty", JOptionPane.OK_CANCEL_OPTION);

        if (result1 == JOptionPane.OK_OPTION) {
            String response = difficultyComboBox.getSelectedItem().toString();

            switch (response) {
                case "Easy":
                    DIFFICULTY = 1;
                    N_COLS = 9;
                    N_ROWS = 9;
                    /* bombComboBox= new JComboBox(bombOptions1); */
                    break;
                case "Hard":
                    DIFFICULTY = 2;
                    N_COLS = 16;
                    N_ROWS = 16;
                    /* bombComboBox= new JComboBox(bombOptions2); */

            }
            JPanel mySetupPanel = new JPanel();
            String bombPrompt = "";
            String timePrompt = "";
            Boolean showUltrabomb = false;

            switch (DIFFICULTY) {
                case 1:
                    bombPrompt = "bomb number (9-11)";
                    timePrompt = "time (120-180) ";

                    break;

                case 2:
                    bombPrompt = "bomb number (35-45)";
                    timePrompt = "time (240-360) ";
                    showUltrabomb = true;

            }
            mySetupPanel.setLayout(new BoxLayout(mySetupPanel, BoxLayout.Y_AXIS));
            mySetupPanel.setLayout(new BoxLayout(mySetupPanel, BoxLayout.Y_AXIS));
            mySetupPanel.add(Box.createVerticalStrut(10)); // a spacer
            mySetupPanel.add(new JLabel(bombPrompt));
            mySetupPanel.add(bombs);
            mySetupPanel.add(Box.createVerticalStrut(10)); // a spacer
            mySetupPanel.add(new JLabel(timePrompt));
            mySetupPanel.add(time);
            mySetupPanel.add(Box.createVerticalStrut(10)); // a spacer
            if (showUltrabomb) {
                mySetupPanel.add(checkBox);
            }
            int result2 = JOptionPane.showConfirmDialog(null, mySetupPanel, "Now select the Configuration",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result2 == JOptionPane.OK_OPTION) {

                TIME = Integer.parseInt(time.getText());
                N_MINES = Integer.parseInt(bombs.getText());
                if (checkBox.isSelected()) {
                    ULTRABOMB = 1;
                }
                try {
                    FileWriter myWriter = new FileWriter("src/resources/medialab/SCENARIO-ID.txt");
                    myWriter.write(DIFFICULTY + "\n" + N_MINES + "\n" + TIME + "\n" + ULTRABOMB);
                    myWriter.close();
                    System.out.println("Successfully wrote to the file.");

                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        } else if (result1 == JOptionPane.CANCEL_OPTION) {
            this.remove(myDifPanel);
        }

    }

    public void onLoad() {
        try {
            JFileChooser fc = new JFileChooser("src/resources/medialab");
            int i = fc.showOpenDialog(this);
            if (i == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                Scanner scan = new Scanner(file);
                System.out.print("importing File... \n");
                int ans[] = dataImport(scan);
                int difficulty = ans[0];
                int bombs = ans[1];
                int time = ans[2];
                int ultraBomb = ans[3];
                System.out.println("difficulty is: " + difficulty);
                System.out.println("Got this far ");
                if (difficulty == 1) {
                    this.N_COLS = 9;
                    this.N_ROWS = 9;
                } else if (difficulty == 2) {
                    this.N_ROWS = 16;
                    this.N_COLS = 16;
                }

                inputChecker(bombs, difficulty, "bombs");

                inputChecker(time, difficulty, "time");

                inputChecker(ultraBomb, difficulty, "ultrabomb");
                System.out.println("N_COLS= " + this.N_COLS);
                System.out.println("N_ROWS= " + this.N_ROWS);
                Start.setEnabled(true);
                scan.close();
            }
        } catch (InvalidValueException e) {
            showMessageDialog(null, e);
            System.exit(1);
        }

        catch (FileNotFoundException e) {
            showMessageDialog(null, e);
            System.exit(1);
        } catch (InvalidDescriptionException e) {
            showMessageDialog(null, e);
            System.exit(1);
        } catch (Exception e) {
            showMessageDialog(null, e);
            System.exit(1);
        }
    }

    public void onStart() {

        gameInstance = new Board(statusbar, N_MINES, N_COLS, N_ROWS, TIME, ULTRABOMB);

        add(gameInstance);
        Solution.setEnabled(true);
        pack();

    }

    public void onExit() {
        showMessageDialog(null, "onExit");
        System.exit(1);
    }

    public void onRounds() {
        String data = "";
        try {
            File myObj = new File("src/resources/medialab/resultLog.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data += myReader.nextLine() + "\n";
                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        showMessageDialog(null, data);
    }

    public void onSolution() {

        gameInstance.setIngameFalse();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Create) {
            onCreate();
        }
        if (e.getSource() == Load) {
            onLoad();
        }
        if (e.getSource() == Start) {
            onStart();
        }
        if (e.getSource() == Exit) {
            onExit();
        }
        if (e.getSource() == Rounds) {
            onRounds();
        }
        if (e.getSource() == Solution) {
            onSolution();
        }

    }

    void inputChecker(int value, int difficulty, String attribute) throws InvalidValueException {
        if (difficulty == 1) {
            switch (attribute) {
                case "bombs":
                    System.out.println("Now checking bombs ");

                    if (value < 9 || value > 11) {
                        throw new InvalidValueException("Check bombs value (9-11)");
                    } else {
                        this.N_MINES = value;
                    }
                    break;
                case "time":
                    System.out.println("Now checking time ");
                    if (value < 120 || value > 180) {
                        throw new InvalidValueException("Check time value (120-180)");
                    } else {
                        this.TIME = value;
                    }
                    break;
                case "ultrabomb":
                    System.out.println("Now checking ultrabomb ");
                    if (value < 0 || value > 0) {
                        throw new InvalidValueException("Check ultrabomb value (0)");
                    } else {
                        this.ULTRABOMB = value;
                    }
            }
        } else if (difficulty == 2) {
            switch (attribute) {
                case "bombs":
                    System.out.println("Now checking bombs ");
                    if (value < 34 || value > 45) {
                        throw new InvalidValueException("Check bombs value (35-45)");
                    } else {
                        this.N_MINES = value;
                    }
                    break;
                case "time":
                    System.out.println("Now checking time ");
                    if (value < 240 || value > 360) {
                        throw new InvalidValueException("Check time value (240-360)");
                    } else {
                        this.TIME = value;
                    }
                    break;
                case "ultrabomb":
                    System.out.println("Now checking ultrabomb ");
                    if (value < 0 || value > 1) {
                        throw new InvalidValueException("Check ultrabomb value (0-1)");
                    } else {
                        this.ULTRABOMB = value;
                    }
            }
        } else {
            throw new InvalidValueException("Check difficulty value (1-2)");
        }
    }

    static int[] dataImport(Scanner scan) throws InvalidDescriptionException {
        int[] ans = new int[4];
        int difficulty = Integer.parseInt(scan.nextLine());
        int bombs = Integer.parseInt(scan.nextLine());
        int time = Integer.parseInt(scan.nextLine());
        int ultraBomb = Integer.parseInt(scan.nextLine());
        if (scan.hasNextLine() == true) {
            throw new InvalidDescriptionException("There are more than 4 lines");
        }
        ans[0] = difficulty;
        ans[1] = bombs;
        ans[2] = time;
        ans[3] = ultraBomb;
        return ans;
    }
}
