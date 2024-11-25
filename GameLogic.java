import java.util.*;

public class GameLogic implements PlayableLogic {
    private Player player1;
    private Player player2;
    private Disc[][] board;

    private List<Move> moveHistory;
    private List<Position> haveBeenHere;
    private Player currentPlayer;
    private List<Position> toFlipFinal;
    private List<Position> toFlipBomb;
    private static final int[][] DIRECTIONS = {
            {0, 1}, {0, -1}, {1, 0}, {-1, 0},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    public GameLogic() {
        board = new Disc[8][8];
        moveHistory = new Stack<>();
        toFlipBomb= new ArrayList<>();
    }

    @Override
    public boolean locate_disc(Position a, Disc disc) {
                if (board[a.row()][a.col()] != null) return false;
        if (isValidPos(a) > 0) {
            Move move = new Move(a, disc);
            move.makeMove(board, a, disc);
            if (disc.getType().equals("💣")) currentPlayer.reduce_bomb();
            if (disc.getType().equals("⭕")) currentPlayer.reduce_unflippedable();

            for (Position positions : toFlipFinal) {
                board[positions.row()][positions.col()].setOwner(currentPlayer);
//                if (getDiscAtPosition(positions).getType().equals("⬤")) {
//                    board[positions.row()][positions.col()].setOwner(currentPlayer);
//                } else if (getDiscAtPosition(positions).getType().equals("💣")) {
//                    bombType(positions);
//                }
            }
            moveHistory.add(move);
            switchPlayer();
            toFlipFinal.clear();
            return true;
        }
        return false;
    }


    public List<Position> bombFlip(Position position) {
        List<Position> localToFlipBomb = new ArrayList<>();
        if (haveBeenHere!=null){
        haveBeenHere.add(position);}
        if (getDiscAtPosition(position) != null) {
            for (int[] direction : DIRECTIONS) {
                int row = position.row() + direction[0];
                int col = position.col() + direction[1];
                if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                    Position pos = new Position(row, col);

                    if (getDiscAtPosition(pos) != null && !getDiscAtPosition(pos).getOwner().equals(currentPlayer)) {
                        if (getDiscAtPosition(pos).getType().equals("⬤")) {
                            localToFlipBomb.add(pos);
                        } else if (getDiscAtPosition(pos).getType().equals("💣") && (pos.row() != position.row() || pos.col() != position.col())) {
                            localToFlipBomb.add(pos);
                            toFlipBomb.addAll(localToFlipBomb);
                            if (!haveBeenHere.contains(pos))
                                bombFlip(pos);
                        }
                    }
                }
            }
            List<Position> newPos = new ArrayList<>();
            boolean[][] booleanBoard = new boolean[8][8];
            if (toFlipBomb != null) {
                for (Position position1 : toFlipBomb) {
                    if (getDiscAtPosition(position1) != null) {
                        booleanBoard[position1.row()][position1.col()] = true;
                    }
                    // if (toFlipBomb.isEmpty()) toFlipBomb.add(position1);
                    // for (Position position2: toFlipBomb){
                    //  if (position1.row()!=position2.row()|| position1.col()!=position2.col()){
                }
                for (int i = 0; i < localToFlipBomb.size(); i++) {
                    if (!booleanBoard[localToFlipBomb.get(i).row()][localToFlipBomb.get(i).col()]) {
                        //booleanBoard[localToFlipBomb.get(i).row()][localToFlipBomb.get(i).col()]=true;
                        newPos.add(localToFlipBomb.get(i));
                    }
                }

                toFlipBomb.addAll(newPos);
                localToFlipBomb.clear();
            }
        }

        return toFlipBomb;
    }

        public void bombType(Position position) {
        if (getDiscAtPosition(position) != null) {
            getDiscAtPosition(position).setOwner(currentPlayer);
            for (int[] direction : DIRECTIONS) {
                int row = position.row() + direction[0];
                int col = position.col() + direction[1];
                Position pos = new Position(row, col);
                if (getDiscAtPosition(pos) != null && !getDiscAtPosition(pos).getOwner().equals(currentPlayer)) {
//                    boolean flag1 = false;
//                    boolean flag2 = false;
//                    for (int i = 0; i < localToFlipBomb.size(); i++) {
//                        if (localToFlipBomb.get(i).row() == pos.row()
//                                && localToFlipBomb.get(i).col() == pos.col()) {
//                            flag1 = true;
//                        }
//                    }
//                    for (int j = 0; j < toFlipFinal.size(); j++) {
//                        if (toFlipFinal.get(j).row() != pos.row() && toFlipFinal.get(j).col() == pos.col()) {
//                            flag2 = true;
//                        }
//                    }
//                    if (!flag1 && !flag2) {
//                        localToFlipBomb.add(pos);
                    if (getDiscAtPosition(pos).getType().equals("⬤")) {
                        board[row][col].setOwner(currentPlayer);}
                    else if (getDiscAtPosition(pos).getType().equals("💣")&&pos.row()!=position.row()&&pos.col()!=position.col()) {
                        bombType(pos);}
                }
//                toFlipBomb.addAll(localToFlipBomb);
            }
        }
//        for (int i = 0; i <toFlipBomb.size() ; i++) {
//            for (int j = 0; j <toFlipFinal.size() ; j++) {
//                if (toFlipBomb.get(i).row()!=toFlipFinal.get(j).row()&&toFlipBomb.get(i).col()!=toFlipFinal.get(i).col()){
//                    toFlipFinal.add(toFlipBomb.get(i));
//                }
//            }
//        }

//        int size=toFlipFinal.size();
//        for (Position bombPos :localToFlipBomb) {
//            boolean flag=true;
//            for (int i = 0; i < size && flag; i++) {
//                if(toFlipFinal.get(i).row()==bombPos.row() && toFlipFinal.get(i).col()==bombPos.col())
//                    flag=false;
//            }
//            if(flag)
//                toFlipFinal.add(bombPos);
//        }

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
        toFlipFinal = new ArrayList<>();
        List<Position> toFlip = new ArrayList<>();
        int flips = 0;
        if (board[position.row()][position.col()] != null) {
            return 0;
        }
        for (int[] direction : DIRECTIONS) {
            toFlip.clear();
            List<Position> tempFlips = new ArrayList<>();
            int counter = 0;
            int row = position.row() + direction[0];
            int col = position.col() + direction[1];
            if(row ==2 && col ==4 )
                System.out.println("hh");
            boolean foundOpponentDisc = false;
            while (row >= 0 && row < board.length && col >= 0 && col < board[0].length) {
                // אם הגענו לתא ריק, נצא מהלולאה
                if (board[row][col] == null) {
                    break;
                }
                if (board[row][col].getOwner() != currentPlayer) {
                    tempFlips.add(new Position(row, col));
                    foundOpponentDisc = true;
                    counter++;
                }
                // אם מצאנו דיסק של השחקן הנוכחי אחרי שמצאנו דיסקים של היריב
                else if (foundOpponentDisc) {
                    flips += counter;
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
            toFlipFinal.addAll(toFlip);
        }
        boolean[][] booleanBoard= new boolean[8][8];
        List<Position> newPos= new ArrayList<>();
        for (Position position1 : toFlipFinal) {
            if (getDiscAtPosition(position1)!= null){
                booleanBoard[position1.row()][position1.col()]=true;
            }
        }
        for (Position position1 : toFlipFinal) {
            if (getDiscAtPosition(position1)!= null && getDiscAtPosition(position1).getType().equals("💣")) {
                toFlipBomb.clear();
                List<Position> toFlipBombs = bombFlip(position1);
                if (toFlipBombs != null && toFlipFinal != null&& !toFlipBombs.isEmpty()) {
                    for (int i = 0; i < toFlipBombs.size(); i++) {
//                        for (int j = 0; j < toFlipFinal.size(); j++) {
//                            if (toFlipBombs.get(i).row() != toFlipFinal.get(j).row() || toFlipBombs.get(i).col() != toFlipFinal.get(j).col()) {
                                if (!booleanBoard[toFlipBombs.get(i).row()][toFlipBombs.get(i).col()]
                                        &&!board[toFlipBombs.get(i).row()][toFlipBombs.get(i).col()].getOwner().equals(currentPlayer))
                                        newPos.add(toFlipBombs.get(i));
                            }
                        //}
                    //}
                }
            }
        }
//        List<Position> finalList= new ArrayList<>();
        toFlipFinal.addAll(newPos);
//        for (Position position1: toFlipFinal){
//            for (Position position2: toFlipFinal){
//                if (position1.row()!=position2.row()|| position1.col()!=position2.col()){

//                }
//            }
//        }
        return toFlipFinal.size();
    }
    public int getNeighbor(Position position){
        return 0;
    }


    @Override
    public int countFlips(Position a) {
        int ret=isValidPos(a);
        return ret;
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
        toFlipBomb.clear();
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
                locate_disc(move.position(),move.disc());
            }
            switchPlayer();
        }
    }
}