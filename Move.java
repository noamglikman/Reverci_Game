public class Move {
    private Position pos;
    private Disc d;

    public Move(Position pos){
        this.pos=pos;
    }

    public Position position() {
        return null;
    }

      public Disc disc() {
        return null;
    }

    public void makeMove(Disc[][] board, Position pos, Disc disc) {
        board[pos.row()][pos.col()] = disc;
    }
}
