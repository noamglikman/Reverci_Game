import java.util.*;
/**
 * Represents the core logic of a two-player board game.
 * This class handles the board state, player turns, legal moves, and game mechanics such as flipping discs and handling special discs (like bombs and unflippable discs).
 * The game follows a set of rules to determine valid moves, flipping of opponent discs, and handling the end game conditions.
 *
 * @see PlayableLogic
 */

public class GameLogic implements PlayableLogic {
    // The two players in the game
    private Player player1;
    private Player player2;
    // The game board, a 2D array of discs
    private Disc[][] board;
    // History of moves made during the game
    private List<Move> moveHistory;
    // Tracks positions where special flips (bombs) happen
    private List<Position> haveBeenHere = new ArrayList<>();
    // The current player whose turn it is
    private Player currentPlayer;
    // List of positions that will be flipped during a move
    private List<Position> toFlipFinal;
    // List of positions for bomb flips
    private List<Position> toFlipBomb;
    // Directions for checking adjacent positions (horizontal, vertical, diagonal)
    private static final int[][] DIRECTIONS = {
            {0, 1}, {0, -1}, {1, 0}, {-1, 0},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    /**
     * Initializes a new game board and empty move history.
     */

    public GameLogic() {
        board = new Disc[8][8];
        moveHistory = new Stack<>();
        toFlipBomb = new ArrayList<>();
    }

    /**
     * Places a disc at a given position on the board, updating the game state.
     * The method checks for special conditions based on the disc type (e.g., bomb or unflippable).
     *
     * @param a    the position to place the disc at.
     * @param disc the disc to place at the position.
     * @return {true} if the move was successful, {false} otherwise.
     */
    @Override
    public boolean locate_disc(Position a, Disc disc) {
        // Check if the position is already occupied by a disc
        if (board[a.row()][a.col()] != null) return false;
        // Check if the position is valid for the move
        if (isValidPos(a) > 0) {
            // Ensure the player has enough bombs or unflippable discs to make the move
            if (disc.getType().equals("💣")) {
                if (currentPlayer.number_of_bombs == 0) return false;
            }
            if (disc.getType().equals("⭕")) {
                if (currentPlayer.number_of_unflippedable == 0) return false;
            }
            // Create and make the move
            Move move = new Move(a, disc);
            move.makeMove(board, a, disc);
            if (isFirstPlayerTurn()) {
                System.out.println("Player 1 placed a " + disc.getType() + " in " + "(" + a.row() + "," + a.col() + ")");
            } else {
                System.out.println("Player 2 placed a " + disc.getType() + " in " + "(" + a.row() + "," + a.col() + ")");
            }
            // Update the player's remaining bombs or unflippable discs
            if (disc.getType().equals("💣")) currentPlayer.reduce_bomb();
            if (disc.getType().equals("⭕")) currentPlayer.reduce_unflippedable();
            // Flip the opponent's discs according to the move
            for (Position positions : toFlipFinal) {
                // Skip unflippable discs
                if (!getDiscAtPosition(positions).getType().equals("⭕")) {
                    board[positions.row()][positions.col()].setOwner(currentPlayer);
                }
                // Log the flipped discs
                if (isFirstPlayerTurn()) {
                    System.out.println("Player 1 flipped the " + getDiscAtPosition(positions).getType() + " in " + "(" + positions.row() + "," + positions.col() + ")");
                } else {
                    System.out.println("Player 2 flipped the " + getDiscAtPosition(positions).getType() + " in " + "(" + positions.row() + "," + positions.col() + ")");
                }
            }
            // Add the move to the history and switch the player
            moveHistory.add(move);
            switchPlayer();
            toFlipFinal.clear();
            System.out.println(" ");
            return true;
        }
        return false;
    }

    /**
     * Recursively checks positions around a bomb disc to determine which discs should be flipped.
     *
     * @param position the position of the bomb disc.
     * @return a list of positions that will be flipped due to the bomb.
     */
    public List<Position> bombFlip(Position position) {
        // Add the current position to the list of visited positions to avoid revisiting
        haveBeenHere.add(position);
        // Ensure there is a disc at the specified position
        if (getDiscAtPosition(position) != null) {
            // Iterate over all possible directions to check neighboring positions
            for (int[] direction : DIRECTIONS) {
                int row = position.row() + direction[0];
                int col = position.col() + direction[1];
                // Ensure the new position is within the bounds of the board
                if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                    Position pos = new Position(row, col);
                    // Check if the position contains a disc that is owned by the opponent
                    if (getDiscAtPosition(pos) != null && !getDiscAtPosition(pos).getOwner().equals(currentPlayer)) {
                        // If the disc is a regular disc, add it to the list of discs to flip
                        if (getDiscAtPosition(pos).getType().equals("⬤")) {
                            if (!toFlipBomb.contains(pos)) {
                                toFlipBomb.add(pos);
                            }
                        }
                        // If the disc is another bomb, recursively continue flipping
                        else if (getDiscAtPosition(pos).getType().equals("💣") && (pos.row() != position.row() || pos.col() != position.col())) {
                            if (!toFlipBomb.contains(pos)) {
                                toFlipBomb.add(pos);
                            }
                            // Recursively call bombFlip to handle the new bomb's effect
                            if (!haveBeenHere.contains(pos))
                                bombFlip(pos);
                        }
                    }
                }
            }

        }
        // Return the final list of discs to be flipped due to the bomb's effect
        return toFlipBomb;
    }

    /**
     * Gets the disc at a specific position on the board.
     *
     * @param position the position to check.
     * @return the disc at the given position, or {null} if no disc is there.
     */
    @Override
    public Disc getDiscAtPosition(Position position) {
        return board[position.row()][position.col()];
    }

    /**
     * Returns the size of the board (assumed to be 8x8).
     *
     * @return the board size (8).
     */

    @Override
    public int getBoardSize() {
        return 8;
    }

    /**
     * Returns a list of valid positions where a move can be made.
     *
     * @return a list of valid positions for the current player.
     */
    @Override
    public List<Position> ValidMoves() {
        List<Position> legalMoves = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                Position position = new Position(i, j);
                if (board[i][j] == null && isValidPos(position) > 0) {
                    legalMoves.add(position);
                }
            }
        }

        return legalMoves;
    }

    /**
     * Checks if a given position is valid for a move.
     *
     * @param position the position to check.
     * @return a positive number if valid, 0 if not.
     */

    public int isValidPos(Position position) {
        // Initialize the list to hold discs that should be flipped due to the move
        toFlipFinal = new ArrayList<>();
        List<Position> toFlip = new ArrayList<>();
        // Check if the position is already occupied by a disc
        if (board[position.row()][position.col()] != null) {
            return 0;// The move is invalid if the position is already occupied
        }
        // Iterate over all directions to check if any opponent's discs are in between
        for (int[] direction : DIRECTIONS) {
            toFlip.clear();// Clear the temporary list of positions to flip
            List<Position> tempFlips = new ArrayList<>();
            int row = position.row() + direction[0];// Calculate the row in the given direction
            int col = position.col() + direction[1];// Calculate the column in the given direction
            boolean foundOpponentDisc = false;
            // Check positions in the current direction
            while (row >= 0 && row < board.length && col >= 0 && col < board[0].length) {
                // If the position is empty, stop checking further in this direction
                if (board[row][col] == null) {
                    break;
                }
                // If the disc belongs to the opponent, add it to the list of discs to flip
                if ((board[row][col].getOwner() != currentPlayer)) {
                    if (!board[row][col].getType().equals("⭕")) {
                        tempFlips.add(new Position(row, col));
                        foundOpponentDisc = true;
                    }
                }
                // If a disc of the current player is found after opponent's discs, the move is valid and we add the opponent's discs to flip
                else if (foundOpponentDisc) {
                    toFlip.addAll(tempFlips);
                    break;
                    // Valid move found, exit the loop and check the other directions
                }
                // If a disc of the current player is found immediately without any opponent discs in between, break the loop
                else {
                    break;
                }
                // Move to the next position in the current direction

                row += direction[0];
                col += direction[1];
            }
            // Add the found discs to flip in the current direction to the final list
            toFlipFinal.addAll(toFlip);
        }
        // Boolean board to track which positions have been visited during the bomb flip process
        boolean[][] booleanBoard = new boolean[8][8];
        List<Position> newPos = new ArrayList<>();
        // Mark the positions to be flipped on the boolean board
        for (Position position1 : toFlipFinal) {
            if (getDiscAtPosition(position1) != null) {
                booleanBoard[position1.row()][position1.col()] = true;
            }
        }
        // Check for bomb discs and handle their effect
        for (Position position1 : toFlipFinal) {
            if (getDiscAtPosition(position1) != null && getDiscAtPosition(position1).getType().equals("💣")) {
                toFlipBomb.clear();// Clear any previously identified bombs
                haveBeenHere.clear();// Clear visited positions for bomb flip
                // Perform the bomb flip and add resulting flips to the list
                List<Position> toFlipBombs = bombFlip(position1);
                if (toFlipBombs != null && toFlipFinal != null && !toFlipBombs.isEmpty()) {

                    for (int i = 0; i < toFlipBombs.size(); i++) {
                        // If the bomb's flip results in a valid position, add it to the final flip list
                        if (!booleanBoard[toFlipBombs.get(i).row()][toFlipBombs.get(i).col()]
                                && !board[toFlipBombs.get(i).row()][toFlipBombs.get(i).col()].getOwner().equals(currentPlayer)) {
                            newPos.add(toFlipBombs.get(i));
                            booleanBoard[toFlipBombs.get(i).row()][toFlipBombs.get(i).col()] = true;
                        }
                    }
                }
            }
        }
        // Add the bomb flip results to the final list of positions to flip
        toFlipFinal.addAll(newPos);
        // Return the total number of discs to be flipped
        return toFlipFinal.size();
    }

    /**
     * Counts the number of opponent discs that would be flipped if a move is made at the given position.
     * <p>
     * The method checks whether the move at the specified position is valid by calling the {@link #isValidPos(Position)} method.
     * If the move is valid, it returns the number of opponent discs that would be flipped. Otherwise, it returns 0.
     *
     * @param a the position where the move would be made
     * @return the number of discs that would be flipped if the move is made at the specified position,
     * or 0 if the move is not valid (i.e., no discs would be flipped).
     */
    @Override
    public int countFlips(Position a) {
        int ret = isValidPos(a);
        return ret;
    }

    /**
     * Switches the current player to the other player.
     */
    private void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    /**
     * Gets the first player in the game.
     *
     * @return the first player.
     */
    @Override
    public Player getFirstPlayer() {
        return player1;
    }

    /**
     * Gets the second player in the game.
     *
     * @return the second player.
     */
    @Override
    public Player getSecondPlayer() {
        return player2;
    }

    /**
     * Sets the players for the game.
     *
     * @param player1 the first player.
     * @param player2 the second player.
     */
    @Override
    public void setPlayers(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        currentPlayer = player1;
    }

    /**
     * Checks if it's the first player's turn.
     *
     * @return {@code true} if it's player 1's turn, {@code false} otherwise.
     */
    @Override
    public boolean isFirstPlayerTurn() {
        return currentPlayer == player1;
    }

    /**
     * Checks if the game is finished (no valid moves left).
     *
     * @return {@code true} if the game is finished, {@code false} otherwise.
     */
    @Override
    public boolean isGameFinished() {
        int count1 = 0;
        int count2 = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] != null) {
                    if (board[i][j].getOwner().isPlayerOne) {
                        count1++;
                    } else if (!board[i][j].getOwner().isPlayerOne) {
                        count2++;
                    }
                }
            }
        }
        if (ValidMoves().isEmpty()) {
            if (isFirstPlayerTurn()) {
                getSecondPlayer().addWin();
                System.out.println("Player 2 wins with " + count2 + " discs! Player 1 had " + count1 + " discs.");
            } else {
                getFirstPlayer().addWin();
                System.out.println("Player 1 wins with " + count1 + " discs! Player 2 had " + count2 + " discs.");
            }
            return true;
        }
        return false;
    }

    /**
     * Resets the game board to the initial state.
     */

    @Override
    public void reset() {
        board = new Disc[8][8];
        board[4][4] = new SimpleDisc(getFirstPlayer());
        board[3][3] = new SimpleDisc(getFirstPlayer());
        board[3][4] = new SimpleDisc(getSecondPlayer());
        board[4][3] = new SimpleDisc(getSecondPlayer());
        currentPlayer = player1;
        player1.reset_bombs_and_unflippedable();
        player2.reset_bombs_and_unflippedable();
        moveHistory.clear();
        toFlipBomb.clear();
    }

    /**
     * Undoes the last move made by the current player.
     * This method reverts the board state to what it was before the most recent move
     * by undoing the placement of the last disc and any flips that were made.
     * It also restores the count of bombs or unflippable discs for the current player.
     * After undoing the move, the method switches the player to the previous one and updates the board accordingly.
     * <p>
     * The process includes:
     * 1. Reverting the position of the last disc placed.
     * 2. Returning any bomb or unflippable discs back to the current player's inventory.
     * 3. Reverting any flipped discs that were flipped as a result of the last move.
     * 4. Restoring the game state by replaying the previous moves.
     * 5. If no move exists to undo, a message will indicate that no previous move is available.
     */
    @Override
    public void undoLastMove() {
        System.out.println("Undoing last move");
        // Check if there are any moves in history to undo
        if (!moveHistory.isEmpty()) {
            // Switch player before undoing the move
            switchPlayer();
            // If the last disc placed was a bomb, increase the number of bombs for the current player
            if (moveHistory.getLast().disc().getType().equals("💣")) {
                currentPlayer.number_of_bombs++;
            }
            // If the last disc placed was unflippable, increase the number of unflippable discs for the current player
            if (moveHistory.getLast().disc().getType().equals("⭕")) {
                currentPlayer.number_of_unflippedable++;
            }
            // Print the details of the last move being undone
            System.out.println("\tUndo: removing " + moveHistory.getLast().disc().getType() +
                    " from (" + moveHistory.getLast().position().row() + "," + moveHistory.getLast().position().col() + ")");
            // Retrieve the last move and remove it from the history
            Move move1 = moveHistory.getLast();
            moveHistory.removeLast();
            // Reset the board to the initial state before any moves were made
            board = new Disc[8][8];
            board[4][4] = new SimpleDisc(getFirstPlayer());
            board[3][3] = new SimpleDisc(getFirstPlayer());
            board[3][4] = new SimpleDisc(getSecondPlayer());
            board[4][3] = new SimpleDisc(getSecondPlayer());
            // Set the current player to player1
            currentPlayer = player1;
            // Replay all previous moves, placing discs and flipping accordingly
            for (Move move : moveHistory) {
                isValidPos(move.position());  // Check if the position of each move is valid
                // Set the owner of the disc and place it on the board
                move.disc().setOwner(currentPlayer);
                move.makeMove(board, move.position(), move.disc());
                // Flip any discs as a result of the move
                for (Position position : toFlipFinal) {
                    board[position.row()][position.col()].setOwner(currentPlayer);
                }
                // Switch the player after each move
                switchPlayer();
            }
            // Check the validity of the last move's position again
            isValidPos(move1.position());
            // Print out the discs that were flipped back as part of undoing the last move
            for (Position position : toFlipFinal) {
                System.out.println("\tUndo: flipping back " + getDiscAtPosition(position).getType() + " in (" + position.row() + "," + position.col() + ")");
            }
        } else {
            // If no previous move exists to undo, print a message indicating this
            System.out.println("\tNo previous move available to undo.");
        }
    }
}
