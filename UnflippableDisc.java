public class UnflippableDisc implements Disc{
    /**
     * Represents a disc that cannot be flipped by opponents.
     * The {UnflippableDisc} is associated with a player and retains its ownership even during gameplay.
     * It is represented by a distinct symbol ("⭕").
     */
    private Player player;

    public UnflippableDisc(Player player){
        this.player=player;
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
        return "⭕" ;
    }
}
