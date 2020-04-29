package server_side.problems.graph_searching.search_algorithms;

import server_side.problems.graph_searching.CommonSearcher;
import server_side.problems.graph_searching.Node;
import server_side.problems.graph_searching.Searchable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BestFirstSearch<TState> extends CommonSearcher<TState> {
    @Override
    public List<Node<TState>> search(Searchable<TState> searchable) {
        openList.clear();
        openList.add(searchable.getInitialNode());
        Set<Node<TState>> closed = new HashSet<>();

        while (!openList.isEmpty()) {
            Node<TState> node = popOpenList();
            closed.add(node);
            if (searchable.isGoalState(node.getState()))
                return backtrace(node);
            for (Node<TState> successor : searchable.getAllPossibleNodes(node)) {
                boolean inOpen = openList.contains(successor);
                double alternativeCost = successor.getCostComingFrom(node);
                if (!closed.contains(successor) && !inOpen) {
                    successor.setCameFrom(node);
                    openList.add(successor);
                } else if (alternativeCost < successor.getCost()) {
                    if (inOpen)
                        openList.remove(successor);
                    successor.setCameFrom(node);
                    successor.setCost(alternativeCost);
                    openList.add(successor);
                }
            }
        }

        return new LinkedList<>();
    }
}
