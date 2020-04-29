package server_side.problems.graph_searching;

public class MatrixPosition {
    private int row;
    private int column;

    public MatrixPosition(int row, int column) {
        if (row < 0 || column < 0)
            throw new IndexOutOfBoundsException("The specified row and column indices must be non-negative");
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MatrixPosition that = (MatrixPosition) obj;
        return row == that.row && column == that.column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
