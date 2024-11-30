import java.util.Comparator;
import java.util.List;
/**
 * Represents a greedy AI player for a board game.
 * The GreedyAI chooses its moves based on maximizing the number of opponent discs flipped.
 * In case of ties, it selects the move based on column and row order.
 *
 * This class extends {AIPlayer} and overrides the {makeMove} method to implement the greedy strategy.
 *
 * @see AIPlayer
 */

public class GreedyAI extends AIPlayer{
    /**
     * Constructs a {GreedyAI} player.
     *
     * @param isPlayerOne a boolean indicating if this AI is the first player.
     */
    public GreedyAI(boolean isPlayerOne){
        super(isPlayerOne);
    }
    /**
     * Determines the best move for this AI using a greedy strategy.
     * It selects the move that maximizes the number of opponent discs flipped.
     * If multiple moves flip the same number of discs, it chooses the one with the lowest column,
     * and if columns are the same, the lowest row.
     *
     * @param gameStatus the current state of the game, including valid moves and board status.
     * @return the selected {@code Move} based on the greedy algorithm.
     * @throws IllegalStateException if no valid moves are available.
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        // Get the list of valid moves from the game
        List<Position> validMoves = gameStatus.ValidMoves();
        // Define a comparator to compare moves based on the number of flipped discs
        // In case of a tie, compare by column, and then by row
        Comparator<Position> comparator = Comparator
                .comparingInt(gameStatus::countFlips)
                .thenComparingInt(Position::col)
                .thenComparingInt(Position::row);
        // Find the best move using the comparator
        Position bestMove = validMoves.stream().max(comparator).orElseThrow(() -> new IllegalStateException("No valid moves available"));
        // Create a simple disc for the chosen move
        Disc disc = new SimpleDisc(this);
        // Return the chosen move with the associated disc
        return new Move(bestMove, disc);
    }
}
