import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic implements PlayableLogic {
    private Player player1;
    private Player player2;
    private Disc[][] board;

    private Stack<Move> moveHistory;

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
            Move move= new Move(a);
            move.makeMove(board,a,disc);
            for(Position positions: toFlipFinal){
                if (getDiscAtPosition(positions).getType().equals("⬤")){
                board[positions.row()][positions.col()].setOwner(currentPlayer);}
                else if(getDiscAtPosition(positions).getType().equals("💣")){
                    bombType(positions);
                }
            }
            moveHistory.push(move);
            switchPlayer();
            toFlipFinal.clear();
            return true;
        }
        return false;
    }
    public void bombType(Position position){
        for(int[] direction: DIRECTIONS){
            int row= position.row()+ direction[0];
            int col= position.col()+ direction[1];
            Position pos= new Position(row,col);
            if (getDiscAtPosition(pos).getOwner() != currentPlayer&& getDiscAtPosition(pos)!=null){
                if(getDiscAtPosition(pos).equals("⬤"))
                board[row][col].setOwner(currentPlayer);
                else if (getDiscAtPosition(pos).equals("💣")){
                    bombType(pos);
                }
            }
        }
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
            toFlip.clear();
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
                    Position discToFilp= new Position(row,col);
                    toFlip.add(discToFilp);
                    foundOpponentDisc = true;
                    counter++;
                }
                // אם מצאנו דיסק של השחקן הנוכחי אחרי שמצאנו דיסקים של היריב
                else if (foundOpponentDisc) {
                    toFlipFinal= toFlip;
                    flips+=counter;
                    return flips;
                    // מצאנו מהלך חוקי
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
        // אם לא מצאנו שום כיוון שבו אפשר להפוך דיסקים
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
            moveHistory.pop();
            switchPlayer();
        }
    }
}
