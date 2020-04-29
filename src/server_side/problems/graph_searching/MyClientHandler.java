package server_side.problems.graph_searching;

import server_side.CacheManager;
import server_side.ClientHandler;
import server_side.FileCacheManager;
import server_side.Solver;
import server_side.problems.graph_searching.search_algorithms.BestFirstSearch;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class MyClientHandler implements ClientHandler {
    private Solver<Searchable<MatrixPosition>, String> solver = new MatrixSearcherSolver(new BestFirstSearch<>());
    private CacheManager<ProblemData, String> cacheManager = new FileCacheManager<>("MyClientHandler_cache");

    @Override
    public void handleClient(InputStream inputStream, OutputStream outputStream) {
        ProblemData problemData;

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));
        try {
            LinkedList<String> lines = new LinkedList<>();
            String line;
            while (!"end".equals(line = inputReader.readLine()))
                lines.add(line);

            double[][] matrix = lines.stream()
                    .map(this::lineToIntStream)
                    .map(IntStream::asDoubleStream)
                    .map(DoubleStream::toArray)
                    .toArray(double[][]::new);

            int[] firstLine = lineToIntStream(inputReader.readLine()).toArray();
            int[] secondLine = lineToIntStream(inputReader.readLine()).toArray();

            problemData = new ProblemData(
                    matrix,
                    firstLine[0], firstLine[1],
                    secondLine[0], secondLine[1]);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

//        String solution = cacheManager.getSolution(problemData);
//        if (solution == null) {
//            Searchable<MatrixPosition> problem = new MatrixSearchable(problemData.matrix, problemData.startRow, problemData.startColumn, problemData.endRow, problemData.endColumn);
//            solution = solver.solve(problem);
//            cacheManager.saveSolution(problemData, solution);
//        }
        Searchable<MatrixPosition> problem = new MatrixSearchable(problemData.matrix, problemData.startRow, problemData.startColumn, problemData.endRow, problemData.endColumn);
        String solution = solver.solve(problem);

        PrintWriter outputWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(outputStream))));
        outputWriter.println(solution);
        outputWriter.flush();

        outputWriter.close();
        try {
            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private IntStream lineToIntStream(String line) {
        return Arrays.stream(line.split(",")).mapToInt(Integer::parseInt);
    }

    private static class ProblemData implements Serializable {
        public final double[][] matrix;
        public final int startRow, startColumn;
        public final int endRow, endColumn;

        public ProblemData(double[][] matrix, int startRow, int startColumn, int endRow, int endColumn) {
            this.matrix = matrix;
            this.startRow = startRow;
            this.startColumn = startColumn;
            this.endRow = endRow;
            this.endColumn = endColumn;
        }
    }
}