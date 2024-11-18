import java.util.List;

public class GreedyAI extends AIPlayer{

    public GreedyAI(boolean isPlayerOne){
        super(isPlayerOne);
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> A= gameStatus.ValidMoves();
        Position max= A.getFirst();
        for (Position position: A){
            if (gameStatus.countFlips(position)>gameStatus.countFlips(max)){
                max=position;
            }
            if (gameStatus.countFlips(position)==gameStatus.countFlips(max)){
                if ( position.col()>max.col()){
                    max =position;
                }
                if (position.col()==max.col()){
                    if(position.row()>max.row()){
                        max=position;
                    }
                }
            }
        }
        Disc disc= new SimpleDisc(this);
        Move move= new Move(max,disc);
        return move;
    }
}
