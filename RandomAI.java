import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public  class RandomAI extends AIPlayer{
public RandomAI(boolean isPlayerOne){
    super(isPlayerOne);
}

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
    List<Position> A= gameStatus.ValidMoves();
    Random random= new Random();
    Position randomPos= A.get(random.nextInt(A.size()));
    Move moveAI= new Move(randomPos);
        return moveAI;
    }
}
