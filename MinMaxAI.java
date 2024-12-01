import java.util.List;
/**
 * AIPlayer implementation that uses the Min-Max algorithm to determine the best move.
 * The AI evaluates moves based on the maximum number of flips and average flips.
 */

public class MinMaxAI extends AIPlayer {
    /**
     * Constructor for MinMaxAI.
     *
     * @param isPlayerOne A boolean indicating whether this AI is Player 1.
     */

    public MinMaxAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * Calculates the maximum number of flips that can be achieved from a list of valid positions.
     *
     * @param gameStatus The current game state.
     * @param pos        A list of valid positions to play.
     * @return The maximum number of flips that can be achieved.
     */
    private int maxFlip(PlayableLogic gameStatus, List<Position> pos) {
        if (pos == null || pos.isEmpty())
            return 0;
        int max = gameStatus.countFlips(pos.getFirst());
        for (int i = 1; i < pos.size(); i++) {
            if (max < gameStatus.countFlips(pos.get(i)))
                max = gameStatus.countFlips(pos.get(i));
        }
        return max;
    }

    /**
     * Calculates the average number of flips that can be achieved from a list of valid positions.
     *
     * @param gameStatus The current game state.
     * @param pos        A list of valid positions to play.
     * @return The average number of flips that can be achieved.
     */
    private double avgFlip(PlayableLogic gameStatus, List<Position> pos) {
        if (pos == null || pos.isEmpty())
            return 0;
        double count = gameStatus.countFlips(pos.getFirst());
        for (int i = 1; i < pos.size(); i++) {
            count += gameStatus.countFlips(pos.get(i));
        }
        count /= pos.size();
        return count;
    }

    /**
     * Makes the best move for the AI player based on the Min-Max strategy.
     * The move is evaluated by the maximum number of flips and average flips for all valid positions.
     *
     * @param gameStatus The current game state.
     * @return A Move object representing the best move for the AI.
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> pos = gameStatus.ValidMoves();
        Position max = pos.getFirst();
        GameLogic temp = (GameLogic) gameStatus;
        double maxD = gameStatus.countFlips(max);
        temp.locate_disc(max, new SimpleDisc(((GameLogic) gameStatus).getCurrentPlayer()));
        maxD -= maxFlip(temp, temp.ValidMoves());
        maxD -= avgFlip(temp, temp.ValidMoves());
        temp.undoLastMove();
        for (Position p : pos) {
            double t = gameStatus.countFlips(p);
            if ((p.col() == 0 || p.row() == 0 || p.col() == 7 || p.row() == 7) && this.getNumber_of_unflippedable() > 0)
                temp.locate_disc(p, new UnflippableDisc(this));
            temp.locate_disc(p, new SimpleDisc(this));
            t -= maxFlip(temp, temp.ValidMoves());
            t -= avgFlip(temp, temp.ValidMoves());
            if (t > maxD) {
                maxD = t;
                max = p;
            }
            temp.undoLastMove();
        }
        if ((max.col() == 0 || max.row() == 0 || max.col() == 7 || max.row() == 7) && this.getNumber_of_unflippedable() > 0)
            return new Move(max, new UnflippableDisc(this));
        return new Move(max, new SimpleDisc(this));
    }
}