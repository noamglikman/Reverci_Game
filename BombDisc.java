public class BombDisc implements Disc{
    /**
     * Represents a special disc that behaves like a bomb in the game.
     * The { BombDisc} is associated with a player and can have unique effects when played.
     * It is visually represented by a bomb symbol ("💣").
     */
    private Player player;
    public BombDisc(Player player){
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
        return "💣";
    }
}
