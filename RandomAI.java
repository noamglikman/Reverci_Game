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
    Disc s1= new SimpleDisc(this);
    Disc s2= new UnflippableDisc(this);
    Disc s3= new BombDisc(this);
    List<Disc> discType= new ArrayList<>();
    discType.add(s1);
    discType.add(s2);
    discType.add(s3);
    Random random1= new Random();
    Disc randomDisc= discType.get(random1.nextInt(discType.size()));
    Move moveAI= new Move(randomPos,randomDisc);
            return moveAI;
    }
}
