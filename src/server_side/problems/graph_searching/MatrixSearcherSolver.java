package server_side.problems.graph_searching;

import server_side.Solver;

import java.util.stream.Collectors;

public class MatrixSearcherSolver implements Solver<Searchable<MatrixPosition>, String> {
    private Searcher<MatrixPosition> searcher;

    public MatrixSearcherSolver(Searcher<MatrixPosition> searcher) {
        this.searcher = searcher;
    }

    @Override
    public String solve(Searchable<MatrixPosition> problem) {
        return searcher.search(problem).stream()
                .skip(1)
                .map(node -> calcDirection(node.getCameFrom().getState(), node.getState()))
                .collect(Collectors.joining(","));
    }

    private String calcDirection(MatrixPosition from, MatrixPosition to) {
        int fromRow = from.getRow(), fromColumn = from.getColumn();
        int toRow = to.getRow(), toColumn = to.getColumn();

        if (fromRow + 1 == toRow)
            return "Down";
        else if (fromRow - 1 == toRow)
            return "Up";
        else if (fromColumn + 1 == toColumn)
            return "Right";
        else if (fromColumn - 1 == toColumn)
            return "Left";
        else
            throw new IllegalArgumentException("The specified `to` position must be 1 row above or below, or 1 column to left or right of the specified `from` position");
    }
}
