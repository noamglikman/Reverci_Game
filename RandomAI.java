import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Represents an AI player that makes random moves.
 * The {RandomAI} selects a valid move at random and randomly chooses a disc type for that move.
 * This AI does not follow a particular strategy but instead relies on chance.
 *
 * <p>This class extends {AIPlayer} and overrides the {makeMove} method to implement this behavior.</p>
 *
 * @see AIPlayer
 */

public  class RandomAI extends AIPlayer{
    /**
     * Constructs a {RandomAI} player.
     *
     * @param isPlayerOne a boolean indicating if this AI is the first player.
     */
public RandomAI(boolean isPlayerOne){
    super(isPlayerOne);
}
    /**
     * Selects a random move from the list of valid moves and assigns a random disc type.
     * The disc types include {SimpleDisc}, {UnflippableDisc}, and {BombDisc}.
     *
     * @param gameStatus the current state of the game, including valid moves and board status.
     * @return a randomly selected {Move} with a random disc type.
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
    List<Position> A= gameStatus.ValidMoves();
    Random random= new Random();
    // Select a random position from the valid moves
    Position randomPos= A.get(random.nextInt(A.size()));
    // Create different types of discs
    Disc s1= new SimpleDisc(this);
    Disc s2= new UnflippableDisc(this);
    Disc s3= new BombDisc(this);
    // Add discs to a list
    List<Disc> discType= new ArrayList<>();
    discType.add(s1);
    discType.add(s2);
    discType.add(s3);
    // Select a random disc type
    Random random1= new Random();
    Disc randomDisc= discType.get(random1.nextInt(discType.size()));
    // Return the randomly generated move
    Move moveAI= new Move(randomPos,randomDisc);
            return moveAI;
    }
}
