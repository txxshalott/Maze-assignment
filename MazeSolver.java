package UhOh;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * shalott tam
 * may - june 4 2022
 * logic part of the maze assignment
 */

public class MazeSolver {
    static Scanner scan = new Scanner(System.in);

    // maze info
    public static int rowCount = 0;
    public static int colCount = 0;
    public static char[][] originalMaze; //will not be modified
    static int[] startCell = new int[2]; // coordinates of the S cell
    static int totalGold = 0; //total gold

    //private static Object endMessage;

    //to find the best path
    static ArrayList<String> originalPath = new ArrayList<String>();
    static int mostCollected = 0;
    static int exitStatus = -1; //1, 0, -1

    public static ArrayList<String> bestPath = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
    }

    //called by MazeGUI, finds best path in the maze
    public static void startHunt() throws IOException {
        //bestPath.clear();
        scanMaze(MazeGUI.userFile);
        System.out.println(startCell[0] + startCell[1]);
        findGold(startCell[0], startCell[1], originalMaze, 0, originalPath);
    }
    static int temp = 0;
    //recursion pt1
    public static void findGold(int row, int col, char[][] maze, int collected, ArrayList<String> path) {
        //if current cell is an exit
        if (maze[row][col] == 'E') {
            findBestPath(path, collected);
            temp ++;
            System.out.println(temp);
            return;
        }

        path = iAmHere(row, col, maze, path); //add current cell to path record

        if (maze[row][col] == '$') collected++; //collect gold if current cell has any

        updateCell(row, col, maze); //update current cell
        movingOn(row, col, maze, collected, path); //next cell!
    }

    //recursion pt2
    public static void movingOn(int row, int col, char[][] maze, int collected, ArrayList<String> path) {

        //adjacent cells to the current cell
        int[][] adjCells = {{row +1, col}, {row, col +1}, {row -1, col}, {row, col -1}};

        for (int[] i : adjCells) { //i stores coordinates of each adjacent cell
            if (checkValid(i[0], i[1], maze)) {

                /**
                 * each adjacent cell is given individual copies of the maze, # of collected gold and path up to now
                 * this is so changes made to maze, collected and path done by one adj cell doesnt affect the other
                 */
                char[][] freshMaze = cloneArray(maze);
                ArrayList<String> freshPath = (ArrayList<String>) path.clone();
                findGold(i[0], i[1], freshMaze, collected, freshPath);
            }
        }
    }
    
    //adds current cell location to the path ive taken up to now
    public static ArrayList<String> iAmHere(int r, int c, char[][] maze, ArrayList<String> path) {
        String cell = maze[r][c] + " " + Integer.toString(r) + " " + Integer.toString(c);
        //the above stores something like "$ 1 2"
        path.add(cell);
    
        return path;
    }

    public static ArrayList<String> findBestPath(ArrayList<String> path, int collected) {
        //find path with most gold
        if (collected > mostCollected) {
            mostCollected = collected;
            bestPath = path; //corresponding best path update

            //if both paths have the same amount of gold, choose the shorter path
        } else if (collected == mostCollected && bestPath.size() > path.size()) {
            bestPath = path;
        } 

        //however, the best path may not contain ALL gold
        if (mostCollected == totalGold) exitStatus = 1;
        else exitStatus = 0;

        return bestPath;
    }

    //checks if a cell is valid
    public static boolean checkValid(int row, int col, char[][] maze) {
        //if the cell is a * or it is out of bounds, do not proceed
        if (row > maze.length-1 || col > maze[0].length-1 || row < 0 || col < 0) return false;
        else if (maze[row][col] == '*') return false;

        return true;
    }

    //S -> G, B -> *, G -> B, $ -> B
    public static void updateCell(int row, int col, char[][] maze) { //pass in PREVIOUS row and col

        if (maze[row][col] == 'S') {
            maze[row][col] = 'G'; 

        } else if (maze[row][col] == 'B') {
            maze[row][col] = '*';

        } else if (maze[row][col] == 'G' || maze[row][col] == '$') {
            maze[row][col] = 'B';
        }
    }

    //clones maze, to be passed to each adjacent cell
    public static char[][] cloneArray(char[][] original) {
        char[][] clone = new char[original.length][original[0].length];

        for (int r = 0; r < original.length; r++) {
            for (int c = 0; c < original[r].length; c++) {
                clone[r][c] = original[r][c];
            }
        }
        return clone;
    }

    //scans maze from file
    public static void scanMaze(String fileName) throws IOException{
        
        File file = new File(fileName);
        try {
            Scanner fs = new Scanner(file);
            //first 2 lines in file indicate # of rows and columns
            rowCount = fs.nextInt();
            colCount = fs.nextInt();

            originalMaze = new char[rowCount][colCount]; //initialize 2d array

            for (int r = 0; r < rowCount; r++) {
                for (int c = 0; c < colCount; c++) {
                    originalMaze[r][c] = fs.next().charAt(0); //method to scan chars

                    if (originalMaze[r][c] == '$') { //calculate total gold amount
                        totalGold++;

                    } else if (originalMaze[r][c] == 'S') { //store starting cell coordinates
                        startCell[0] = r; startCell[1] = c;
                    }
                }
            }
            fs.close();

        } catch (FileNotFoundException e) {
            MazeGUI.endMessage.setText("File not found, please try again");
        }
    }
}
