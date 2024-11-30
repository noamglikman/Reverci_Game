public class SimpleDisc implements Disc{
    /**
     * Represents a basic disc used in the game.
     * The { SimpleDisc} is associated with a player and can change ownership during gameplay.
     * It displays a simple disc symbol ("⬤") as its type.
     */
    private Player player;

    public SimpleDisc(Player player){
        this.player= player;
    }
    @Override
    public Player getOwner() {
        return this.player;
    }

    @Override
    public void setOwner(Player player) {
    this.player=player;
    }

    @Override
    public String getType() {
        return "⬤";
    }
}
