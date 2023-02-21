package UhOh;
import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.io.*;

/**
 * shalott tam
 * may - june 4 2022
 * GUI aspect of the maze assignment
 * Works, but it's not the most efficient
 */

public class MazeGUI extends JFrame implements ActionListener {

    public static String userFile;
    
    // top panel components
    static JPanel interactivePan = new JPanel();
    JTextField inField = new JTextField(8);
    JLabel fileName = new JLabel("File name: 8x8.txt");
    JButton button = new JButton("Get maze");

    // the maze
    static JPanel mazePan = new JPanel();
    static JLabel grid[][];

    // bottom panel components
    JPanel bottomPan = new JPanel();
    public static JLabel endMessage = new JLabel(" ");

    public MazeGUI() throws IOException { // constructor

        setTitle("Treasure Hunt");
        setSize(600, 700);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // top panel
        button.addActionListener((ActionListener) this);
        interactivePan.add(inField);
        interactivePan.add(button);

        // middle panel layout is set in makeMaze()

        // bottom panel
        bottomPan.add(endMessage);

        add(interactivePan);
        add(mazePan);
        add(bottomPan);
        setVisible(true);
    }

    // for displaying maze
    static int rowCount;
    static int colCount;

    static String pathList = ""; // stores best path

    // used when iterating through best path (line 116 onwards)
    int start = 0;
    int end = 1;

    //used when "start treasure hunt" is clicked
    Timer timer = new Timer(500, this);
    String command;
    Object source;

    int exitStatus = MazeSolver.exitStatus;

    public void actionPerformed(ActionEvent e) {
        command = e.getActionCommand(); // for buttons
        source = e.getSource(); // for timer

        //when the timer starts and actionPerformed is called, command will be null
        //if statements cant proceed if command is null
        if (command == null) {
            command = "";
        }

        //GET AND DISPLAY MAZE
        if (command.equals("Get maze")) {

            timer.stop();
            endMessage.setText("");
            mazePan.removeAll(); //clear elements in panel to recreate maze
            
            userFile = inField.getText(); // get file name

            try {
                MazeSolver.startHunt();
                rowCount = MazeSolver.rowCount;
                colCount = MazeSolver.colCount;

                mazePan.setLayout(new GridLayout(rowCount, colCount));
                makeMaze(); // creates and makes maze visible to user
                revalidate();
                repaint();

            } catch (IOException e1) {
            }

            button.setText("Start treasure hunt"); // update button name
        }

        //RUN PROGRAM
        if (command.equals("Start treasure hunt")) {
            
            start = 0;
            end = 1;
            timer.restart(); // restart timer every time this is clicked
            button.setText("Get maze"); //so user can get a new file

        } else if (source.equals(timer) && !pathList.substring(end -1).equals("")) {
            
            //pathList is a string
            int r = Integer.valueOf(pathList.substring(start, end));
            start++;
            end++;
            int c = Integer.valueOf(pathList.substring(start, end));
            start++;
            end++;

            imHere(r, c);
            revalidate();
            repaint();
            updateCellInfo(r, c);
            setBgColour(r, c);
            revalidate();
            repaint();

            //when we reach exit
        } else if (source.equals(timer) && pathList.substring(end -1).equals("")) {
           
            timer.stop();
            showEndMessage();
        }
    }

    public void showEndMessage() {
        // display end message
        if (MazeSolver.exitStatus == 1) {
            endMessage.setText("All gold collected!");

        } else if (MazeSolver.exitStatus == 0) {
            endMessage.setText(MazeSolver.mostCollected + " gold collected!");

        } else if (MazeSolver.exitStatus == -1) {
            endMessage.setText("Stuck :(");
        }
        endMessage.setVisible(true);
        repaint();
        revalidate();
    }

    //create maze and determine best path
    public static void makeMaze() throws IOException {
        grid = new JLabel[rowCount][colCount]; // initialize size

        // assign values to JLabel, set alignment, font, colour, bg colour
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                grid[r][c] = new JLabel(String.valueOf(MazeSolver.originalMaze[r][c]), SwingConstants.CENTER);
                grid[r][c].setFont(new Font("Dialog", Font.PLAIN, 20));
                grid[r][c].setForeground(Color.WHITE);
                setBgColour(r, c);

                mazePan.add(grid[r][c]); // add each cell to to panel
            }
        }
        // set start cell text and colour
        grid[MazeSolver.startCell[0]][MazeSolver.startCell[1]].setText("S");
        grid[MazeSolver.startCell[0]][MazeSolver.startCell[1]].setBackground(new Color(51, 153, 255));

        //extract cell coordinates from the best path in maze solver
        pathList = ""; //reinitalize
        for (String s : MazeSolver.bestPath) {
            pathList += (s.charAt(2)); //pathList is initialized in line 59
            pathList += (s.charAt(4));
            
            pathList.concat(s); // 2 each
        }
        System.out.println(pathList);
    }

    //shows current location
    public static void imHere(int r, int c) {
        grid[r][c].setBackground(Color.WHITE);
    }

    public static void updateCellInfo(int r, int c) {
        if (grid[r][c].getText().equals("S")) {
            grid[r][c].setText("G");

        } else if (grid[r][c].getText().equals("G") || grid[r][c].getText().equals("$")) {
            grid[r][c].setText("B");

        } else if (grid[r][c].getText().equals("B")) {
            grid[r][c].setText("*");
        }
    }

    // sets color of each cell
    public static void setBgColour(int r, int c) {
        Color color = Color.GRAY;
        grid[r][c].setOpaque(true);

        if (grid[r][c].getText().equals("B")) {
            color = Color.BLACK;

        } else if (grid[r][c].getText().equals("$")) {
            color = Color.ORANGE;

        } else if (grid[r][c].getText().equals("G")) {
            color = new Color(0, 153, 0); // green

        } else if (grid[r][c].getText().equals("*")) {
            color = Color.GRAY;

        } else if (grid[r][c].getText().equals("E")) {
            color = new Color(255, 51, 51);
        }
        grid[r][c].setBackground(color);
    }

    public static void main(String[] args) throws IOException {
        new MazeGUI();
    }

}
