package server_side.problems.graph_searching;

import java.util.List;

public interface Searchable<TState> {
    Node<TState> getInitialNode();
    boolean isGoalState(TState state);
    List<Node<TState>> getAllPossibleNodes(Node<TState> node);
}
