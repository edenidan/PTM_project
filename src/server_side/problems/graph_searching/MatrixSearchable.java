package server_side.problems.graph_searching;

import java.util.ArrayList;
import java.util.List;

public class MatrixSearchable implements Searchable<MatrixPosition> {
    private final double[][] matrix;
    private final MatrixPosition initialState;
    private final MatrixPosition goalState;

    public MatrixSearchable(double[][] matrix, int startRow, int startColumn, int endRow, int endColumn) {
        this.matrix = matrix;
        initialState = new MatrixPosition(startRow, startColumn);
        goalState = new MatrixPosition(endRow, endColumn);
    }

    @Override
    public Node<MatrixPosition> getInitialNode() {
        return createNode(initialState, null);
    }

    @Override
    public boolean isGoalState(MatrixPosition state) {
        return state.equals(goalState);
    }

    @Override
    public MatrixPosition getGoalState() {
        return goalState;
    }

    @Override
    public List<Node<MatrixPosition>> getAllPossibleNodes(Node<MatrixPosition> node) {
        List<Node<MatrixPosition>> allPossibleNodes = new ArrayList<>(4);

        int row = node.getState().getRow();
        int column = node.getState().getColumn();
        if (row != 0) allPossibleNodes.add(createNode(row - 1, column, node));
        if (column != 0) allPossibleNodes.add(createNode(row, column - 1, node));
        if (row != matrix.length - 1) allPossibleNodes.add(createNode(row + 1, column, node));
        if (column != matrix[0].length - 1) allPossibleNodes.add(createNode(row, column + 1, node));

        return allPossibleNodes;
    }

    private Node<MatrixPosition> createNode(int row, int column, Node<MatrixPosition> cameFrom) {
        return new Node<>(new MatrixPosition(row, column), matrix[row][column] + (cameFrom != null ? cameFrom.getCost() : 0), cameFrom);
    }

    private Node<MatrixPosition> createNode(MatrixPosition position, Node<MatrixPosition> cameFrom) {
        return createNode(position.getRow(), position.getColumn(), cameFrom);
    }
}
