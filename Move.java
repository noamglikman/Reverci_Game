public class Move {
    private Position pos;
    private Disc d;

    public Move(Position pos, Disc disc){
        this.pos=pos;
        this.d= disc;
    }

    public Position position() {
        return this.pos;
    }

      public Disc disc() {
        return this.d;
    }

    public void makeMove(Disc[][] board, Position pos, Disc disc) {
        if (pos!= null){
        board[pos.row()][pos.col()] = disc;
    }}
}
