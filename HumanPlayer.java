public class HumanPlayer extends Player{
    public HumanPlayer(boolean isHuman){
        super(isHuman);
    }
    @Override
    boolean isHuman() {
        return true;
    }
}
