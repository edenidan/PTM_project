package server_side.problems.graph_searching;

import java.util.List;

public interface Searcher<TState> {
    List<Node<TState>> search(Searchable<TState> searchable);
    int getNumberOfNodesEvaluated();
}
