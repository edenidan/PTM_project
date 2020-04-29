package server_side.problems.graph_searching;

import java.util.*;

public abstract class CommonSearcher<TState> implements Searcher<TState> {
    private int evaluatedNodes = 0;
    protected PriorityQueue<Node<TState>> openList = new PriorityQueue<>(Comparator.comparingDouble(Node::getCost));

    final protected Node<TState> popOpenList() {
        evaluatedNodes++;
        return openList.poll();
    }

    final protected List<Node<TState>> backtrace(Node<TState> node) {
        LinkedList<Node<TState>> path = new LinkedList<>();
        while (node != null) {
            path.addFirst(node);
            node = node.getCameFrom();
        }
        return path;
    }

    @Override
    public int getNumberOfNodesEvaluated() {
        return evaluatedNodes;
    }
}
