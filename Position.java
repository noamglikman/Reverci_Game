public class Position {
    private int row;
    private int column;

    public Position(int rows, int columns){
        this.row= rows;
        this.column=columns;
    }
    void setRows(int rows){
        this.row=rows;
    }
    void setColumns(int columns){
        this.column= columns;
    }

    public int col() {
        return column;
    }

    public int row() {
        return row;
    }
}
