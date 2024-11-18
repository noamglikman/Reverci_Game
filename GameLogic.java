import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic implements PlayableLogic {
    private Player player1;
    private Player player2;
    private Disc[][] board;

    private List<Move> moveHistory;

    private Player currentPlayer;
    private List<Position> toFlipFinal;
    private static final int[][] DIRECTIONS = {
            {0, 1}, {0, -1}, {1, 0}, {-1, 0},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };
    public GameLogic(){
        board = new Disc[8][8];
        moveHistory= new Stack<>();
    }

    @Override
    public boolean locate_disc(Position a, Disc disc) {
        if(board[a.row()][a.col()]!=null) return false;
        if (isValidPos(a)>0){
            Move move= new Move(a,disc);
            move.makeMove(board,a,disc);
            if (disc.getType().equals("💣")) currentPlayer.reduce_bomb();
            if (disc.getType().equals("⭕")) currentPlayer.reduce_unflippedable();
            for(Position positions: toFlipFinal){
                if (getDiscAtPosition(positions).getType().equals("⬤")){
                board[positions.row()][positions.col()].setOwner(currentPlayer);}
                else if(getDiscAtPosition(positions).getType().equals("💣")){
                    bombType(positions);
                }
            }
            moveHistory.add(move);
            switchPlayer();
            toFlipFinal.clear();
            return true;
        }
        return false;
    }
    public int bombType(Position position){
        int counter=0;
        getDiscAtPosition(position).setOwner(currentPlayer);
        for(int[] direction: DIRECTIONS){
            int row= position.row()+ direction[0];
            int col= position.col()+ direction[1];
            Position pos= new Position(row,col);
            if (getDiscAtPosition(pos)!=null&&!getDiscAtPosition(pos).getOwner().equals(currentPlayer)){
                if(getDiscAtPosition(pos).getType().equals("⬤")){
                    counter++;
                    board[row][col].setOwner(currentPlayer);}
                else if (getDiscAtPosition(pos).getType().equals("💣")){
                    counter++;
                    bombType(pos);
                }
            }
        }
        return 0;
    }

    @Override
    public Disc getDiscAtPosition(Position position) {
        return board[position.row()][position.col()];
    }

    @Override
    public int getBoardSize() {
        return 8;
    }

    //We have 8 optional moves(up, down, right, left, right up, left up, right down, left down)
    //
    @Override
    public List<Position> ValidMoves() {
        List<Position> legalMoves = new ArrayList<>();
        for (int i = 0; i < board.length ; i++) {
            for (int j = 0; j < board.length ; j++) {
                Position position= new Position(i,j);
                if(board[i][j]==null&& isValidPos(position)>0){
                    legalMoves.add(position);
                }
            }
         }

        return legalMoves;
    }
    public int isValidPos(Position position) {
        List<Position> toFlip= new ArrayList<>();
        int flips=0;
        if (board[position.row()][position.col()] != null) {
            return 0;
        }
        for (int[] direction : DIRECTIONS) {
            List<Position> tempFlips = new ArrayList<>();
            int counter=0;
            int row = position.row()+ direction[0];
            int col = position.col()+ direction[1];
            boolean foundOpponentDisc = false;
            while (row >= 0 && row < board.length && col >= 0 && col < board[0].length) {
                // אם הגענו לתא ריק, נצא מהלולאה
                if (board[row][col] == null) {
                    break;
                }
                if (board[row][col].getOwner() != currentPlayer) {
                    tempFlips.add(new Position(row,col));
                    foundOpponentDisc = true;
                    counter++;
                }
                // אם מצאנו דיסק של השחקן הנוכחי אחרי שמצאנו דיסקים של היריב
                else if (foundOpponentDisc) {
                    flips+=counter;
                    toFlip.addAll(tempFlips);
                    break;
                    // מצאנו מהלך חוקי נצא מהלולאה ונבדוק את שאר הכיוונים
                }
                // אם פגשנו דיסק של השחקן הנוכחי מיידית, ללא דיסקים של היריב ביניהם
                else {
                    break;
                }
                // נמשיך בכיוון הנוכחי
                row += direction[0];
                col += direction[1];
            }
        }
        toFlipFinal=new ArrayList<>(toFlip);
        return flips;
    }


    @Override
    public int countFlips(Position a) {
        return isValidPos(a);
    }
    private void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    @Override
    public Player getFirstPlayer() {
        return player1;
    }

    @Override
    public Player getSecondPlayer() {
        return player2;
    }

    @Override
    public void setPlayers(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        currentPlayer= player1;
    }

    @Override
    public boolean isFirstPlayerTurn() {
        return currentPlayer== player1;
    }

    @Override
    public boolean isGameFinished() {
        if (ValidMoves().isEmpty()){
            if (isFirstPlayerTurn()) getSecondPlayer().addWin();
            else getFirstPlayer().addWin();
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        board =new Disc[8][8];
        board[4][4]= new SimpleDisc(getFirstPlayer());
        board[3][3]= new SimpleDisc(getFirstPlayer());
        board[3][4]= new SimpleDisc(getSecondPlayer());
        board[4][3]= new SimpleDisc(getSecondPlayer());
        currentPlayer= player1;
        moveHistory.clear();
    }

    @Override
    public void undoLastMove() {
        if (!moveHistory.isEmpty()){
            moveHistory.removeLast();
            board =new Disc[8][8];
            board[4][4]= new SimpleDisc(getFirstPlayer());
            board[3][3]= new SimpleDisc(getFirstPlayer());
            board[3][4]= new SimpleDisc(getSecondPlayer());
            board[4][3]= new SimpleDisc(getSecondPlayer());
            for (Move move: moveHistory){
                move.makeMove(this.board,move.position(),move.disc());
            }
            switchPlayer();
        }
    }
}