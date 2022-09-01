import java.util.*;
/**
 * TicTacToe runs a game of TicTacToe.
 * 
 * The class TicTacToe contains game logic needed for
 * variable sized game of tic-tac-toe.
 * 
 * @author Arttu Knuutinen
 * @version 2018.1214
 */

public class TicTacToe {
    /**
     * Scanner to take user input.
     */
    private static Scanner sc = new Scanner(System.in);
    /**
     * Minimum board size.
     */
    private static int minBoardSize = 3;
    /**
     * Maximum reasonable board size.
     */
    private static int maxReasonableSize = 25;
    /**
     * Board height, chosen by user.
     */
    private static int boardHeight = 0;
    /**
     * Board width, chosen by user.
     */
    private static int boardWidth = 0;
    /**
     * Marks needed in a row to win the game, chosen by user.
     */
    private static int inRowToWin = 0;
    /**
     * Game board in 2-dimensional array.
     */
    private static TicTacToeNode[][] board;
    /**
     * Boolean that keeps track of gamestate.
     */
    private static boolean gameOver = false;
    /**
     * Boolean that decides if it is the player's or the computer's turn.
     */
    private static boolean playerTurn = true;
    /**
     * Counts the turns taken, used to check if game ends in a draw.
     */
    private static int turnsTaken = 0;
    /**
     * Keeps track of the most recently played position.
     */
    private static TicTacToeNode lastUsedNode;

    /**
     * The Main method, runs at program start.
     * 
     * @param args command line arguments, not used.
     */
    public static void main(String[] args) {
        startGame();
        takeTurns();
        endGame();
    }
    /**
     * Runs necessary tasks for starting the game.
     */
    private static void startGame(){
        String intro = "You're playing TicTacToe against the computer.\n" +
                        "You're using \"X\"\n" +
                        "Computer uses \"O\"\n" +
                        "Please select the size of the board" +
                        " and amount of marks needed to win.";
        System.out.println(intro);
        createBoard();
        printBoard();
    }
    /**
     * Creates the board with user input.
     */
    private static void createBoard(){
        while(boardHeight < minBoardSize){
            System.out.print("Enter board height: ");
            boardHeight = getUserInputInt(sc.nextLine());
            if(boardHeight > maxReasonableSize){
                System.out.println("That's pretty big!");
                if(!getUserConfirmation()){
                    boardHeight = 0;
                }
            }
        }
        while(boardWidth < minBoardSize){
            System.out.print("Enter board width: ");
            boardWidth = getUserInputInt(sc.nextLine());
            if(boardWidth > maxReasonableSize){
                System.out.println("That's pretty big!");
                if(!getUserConfirmation()){
                    boardWidth = 0;
                }
            }
        }
        int minMax = Math.min(boardWidth, boardHeight);
        int minWinCond = minMax >= 10 ? 5 : 3;
        while(inRowToWin < minBoardSize || inRowToWin > minMax) {
            System.out.print("Enter amount of marks needed in a row to win"
                            + "(" + minWinCond + "-" + minMax + "): ");
            inRowToWin = getUserInputInt(sc.nextLine());
        }
        board = new TicTacToeNode[boardHeight][boardWidth];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = new TicTacToeNode(j, i);
            }
        }
    }
    /**
     * Alternates turns between the player and the computer.
     */
    private static void takeTurns(){
        while(!gameOver){
            if(playerTurn){
                playerTurn();
            } else {
                aiTurn();
            }
            printBoard();
            gameOver = wasWinningMove(lastUsedNode);
            if(!gameOver){
                turnsTaken++;
                if(turnsTaken == boardWidth * boardHeight){
                    gameOver = true;
                }
                playerTurn = !playerTurn;
            }
        }
    }
    /**
     * Handles the player's turn.
     */
    private static void playerTurn(){
        String input = "";
        int x = -1;
        int y = -1;
        boolean turnComplete = false;
        String prompt = "Select coordinates \"x, y\" where to play your mark";
        String coordinateError = "Please enter coordinates as requested.";
        while(!turnComplete) {
            while(x < 0 || x >= boardWidth || y < 0 || y >= boardHeight){
                System.out.println(prompt);
                System.out.print("Enter coordinates: ");
                input = sc.nextLine();
                input = input.replaceAll(" ", "");
                String[] temp = input.split("\\D");
                if(temp.length == 2){
                    if(temp[0].length() > 0){
                        x = Integer.parseInt(temp[0]) - 1;
                    }
                    if(temp[1].length() > 0){
                        y = Integer.parseInt(temp[1]) - 1;
                    }
                } else {
                    System.out.println(coordinateError);
                }
            }
            if(!board[y][x].isUsed()){
                board[y][x].playMark('X');
                turnComplete = true;
            } else {
                System.out.println("You cannot place a mark over another one");
                x = -1;
                y = -1;
            }
        }
        lastUsedNode = board[y][x];
    }
    /**
     * Handles the computer's turn.
     * 
     * Tries to play its move somewhere around the player's last position.
     * If there are no empty nodes around the position, plays on a random node.
     */
    private static void aiTurn(){
        int y = 0;
        int x = 0;
        int playerLastX = lastUsedNode.getX();
        int playerLastY = lastUsedNode.getY();

        // Finds empty nodes around the last played node
        ArrayList<TicTacToeNode> freeNodes = new ArrayList<TicTacToeNode>();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                int tempX = playerLastX + j;
                int tempY = playerLastY + i;
                if(isBetween(tempX, 0, boardWidth -1)
                        && isBetween(tempY, 0, boardHeight -1)) {
                    if(!board[tempY][tempX].isUsed()){
                        freeNodes.add(board[tempY][tempX]);
                    }
                }
            }
        }
        // Plays on a random free node if any are found
        if(freeNodes.size() > 0){
            int i = (int)(Math.random() * freeNodes.size());
            x = freeNodes.get(i).getX(); 
            y = freeNodes.get(i).getY(); 
        } else {
            do {
                y = (int)(Math.random() * boardHeight);
                x = (int)(Math.random() * boardWidth);    
            } while (board[y][x].isUsed());        
        }
        board[y][x].playMark('O');
        lastUsedNode = board[y][x];
        System.out.println("Computer played on \"x:" + (x +1)
                            + ", y:" + (y +1) + "\"");
    }
    /**
     * Reports the game result.
     */
    private static void endGame(){
        String endText = "";
        if(turnsTaken == boardWidth * boardHeight){
            endText = "It's a draw.";
        } else {
            endText = (playerTurn ? "You win " : "Computer wins " )
                    + "from position \"x:" + (lastUsedNode.getX() + 1)
                    + ", y:" + (lastUsedNode.getY() + 1) + "\"!";
        }
        System.out.println(endText);
    }
    /**
     * Returns parsed integer from user input string.
     * 
     * @param input String from user.
     * @return -1 if user didn't give valid input.
     */
    private static int getUserInputInt(String input) {
        int output = -1;
        try {
            output = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Enter a valid number");
        }
        return output;
    }
    /**
     * Prints board coordinate numbers for X - axis.
     */
    private static void printXCoordinates(){
        String line = "   X";
        for (int i = 1; i <= boardWidth; i++) {
            line += String.format("% 3d", i) + " ";
        }
        System.out.println(line);
    }
    /**
     * Prints a line of ascii graphic for the board.
     * 
     * @param printCoordinate determines if to print Y - axis identifier.
     */
    private static void printAsciiLine(boolean printCoordinate){
        String line = printCoordinate ? " Y " : "   ";
        for (int i = 0; i < boardWidth; i++) {
            line += "+---";
        }
        System.out.println(line + "+");
    }
    /**
     * Prints marks from the board from given index.
     * 
     * @param i the board index to print.
     */
    private static void printBoardMarks(int i){
        for (int j = 0; j < board[i].length; j++) {
            if(j == 0){
                System.out.print(String.format("% 3d", i + 1)
                                + "| " + board[i][j].getMark());
            } else if (j == boardWidth - 1){
                System.out.print(" | " + board[i][j].getMark() + " |");
            } else {
                System.out.print(" | " + board[i][j].getMark());
            }
        }
        System.out.println();
    }
    /**
     * Prints the game board.
     */
    private static void printBoard(){
        printXCoordinates();
        for (int i = 0; i < board.length; i++) {
            printAsciiLine(i == 0);
            printBoardMarks(i);
        }
        printAsciiLine(false);
        System.out.println();
    }
    /**
     * Determines if the node forms a winning combination.
     * 
     * @param node the node to check for victory.
     * @return true if node forms a combination of required length.
     */
    private static boolean wasWinningMove(TicTacToeNode node){
        char c = node.getMark();
        int inRowCount = 0;
        boolean victory = false;
        int xPos = node.getX();
        int yPos = node.getY();
        
        // Horizontal check
        for (int i = -(inRowToWin -1); i <= (inRowToWin -1) && !victory; i++) {
            if(isBetween(xPos + i, 0, boardWidth -1)) {
                if(board[yPos][xPos + i].getMark() == c){
                    inRowCount++;
                    if(inRowCount == inRowToWin){
                        victory = true;
                        break;
                    }
                } else {
                    inRowCount = 0;
                }    
            }
        }

        // Vertical check
        inRowCount = 0;
        for (int i = -(inRowToWin -1); i <= (inRowToWin -1) && !victory; i++) {
            if(isBetween(yPos + i, 0, boardHeight -1)) {
                if(board[yPos + i][xPos].getMark() == c){
                    inRowCount++;
                    if(inRowCount == inRowToWin){
                        victory = true;
                        break;
                    }
                } else {
                    inRowCount = 0;
                }    
            }
        }

        // Diagonals

        // North-west to south-east
        inRowCount = 0;
        for (int i = -(inRowToWin -1); i <= (inRowToWin -1) && !victory; i++) {
            if(isBetween(xPos + i, 0, boardWidth -1) 
                    && isBetween(yPos + i, 0, boardHeight -1)) {
                if(board[yPos + i][xPos + i].getMark() == c){
                    inRowCount++;
                    if(inRowCount == inRowToWin){
                        victory = true;
                        break;
                    }
                } else {
                    inRowCount = 0;
                }
            }
        }

        // South-west to north-east
        inRowCount = 0;
        for (int i = -(inRowToWin -1); i <= (inRowToWin -1) && !victory; i++) {
            if(isBetween(xPos + i, 0, boardWidth -1) 
                    && isBetween(yPos - i, 0, boardHeight -1)) {
                if(board[yPos - i][xPos + i].getMark() == c){
                    inRowCount++;
                    if(inRowCount == inRowToWin){
                        victory = true;
                        break;
                    }
                } else {
                    inRowCount = 0;
                }
            }
        }
        return victory;
    }
    /**
     * Checks if x is between min and max.
     * 
     * @param x value to test.
     * @param min the low value.
     * @param max the high value.
     * 
     * @return true if value of x is between min and max
     */
    private static boolean isBetween(int x, int min, int max){
        return (x >= min && x <= max);
    }
    /**
     * Asks user to confirm a decision.
     * 
     * @return true if user input starts with 'y'
     */
    private static boolean getUserConfirmation(){
        String input = "";
        boolean success = false;
        char c = ' ';
        do {
            try {
                System.out.println("Are you sure you want to do that? (y/n)");
                input = sc.nextLine().toLowerCase();
                c = input.charAt(0);
                if(c == 'y' || c == 'n'){
                    success = true;
                }
            } catch (Exception e) {

            }
        } while (!success);
        return c == 'y';
    }
}

/**
 * TicTacToeNode stores useful data for TicTacToe game-logic.
 * 
 * @author Arttu Knuutinen
 * @version 2018.1214
 */
class TicTacToeNode {
    /**
     * Boolean used to see if a mark has already been placed on the node.
     */
    private boolean isUsed = false;
    /**
     * Stores the mark that's placed on the node.
     */
    private char mark = ' ';
    /**
     * The node's X - index on the board.
     */
    private int xPos;
    /**
     * The node's Y - index on the board.
     */
    private int yPos;

    /**
     * Get method for the X - index.
     * 
     * @return xPos
     */
    public int getX(){
        return xPos;
    }
    /**
     * Get method for the Y - index.
     * 
     * @return yPos
     */
    public int getY(){
        return yPos;
    }
    /**
     * Get method for the stored mark.
     * 
     * @return the "mark" char.
     */
    public char getMark(){
        return mark;
    }
    /**
     * Retuns the value of isUsed boolean.
     * 
     * @return true if a mark has been played on the node.
     */
    public boolean isUsed(){
        return isUsed;
    }
    /**
     * Sets the mark variable to be the given parameter.
     * 
     * @param mark char to set on the node.
     */
    private void setMark(char mark){
        this.mark = mark;
    }
    /**
     * Used to play a mark on the node, sets isUsed to true.
     * 
     * @param mark the char to play on the node.
     */
    public void playMark(char mark){
        setMark(mark);
        isUsed = true;
    }
    /**
     * Constructor
     * 
     * @param xPos the X - index of the node in 2D-array
     * @param yPos the Y - index of the node in 2D-array
     */
    public TicTacToeNode(int xPos, int yPos){
        this.xPos = xPos;
        this.yPos = yPos;
    }
}