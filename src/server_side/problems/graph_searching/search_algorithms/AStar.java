package server_side.problems.graph_searching.search_algorithms;

import server_side.problems.graph_searching.CommonSearcher;
import server_side.problems.graph_searching.Node;
import server_side.problems.graph_searching.Searchable;

import java.util.*;
import java.util.function.ToDoubleFunction;

public class AStar<TState> extends CommonSearcher<TState> {
    private final ToDoubleFunction<TState> heuristic;

    public AStar(ToDoubleFunction<TState> heuristic) {
        this.heuristic = heuristic;
    }


    @Override
    public List<Node<TState>> search(Searchable<TState> searchable) {

        PriorityQueue<Node<TState>> _openList = new PriorityQueue<>(Comparator.comparingDouble((node) -> node.getCost() + heuristic.applyAsDouble(node.getState())));

        _openList.clear();
        _openList.add(searchable.getInitialNode());

        while (!_openList.isEmpty()) {
            Node<TState> node = _openList.poll();
            if (searchable.isGoalState(node.getState()))
                return backtrace(node);

            //node.setCost(node.getCost() + heuristic.applyAsDouble(node.getState()));
            _openList.remove(node);
            for (Node<TState> successor : searchable.getAllPossibleNodes(node)) {
                //successor.setCost(successor.getCost() + heuristic.applyAsDouble(successor.getState()));
                boolean inOpen = _openList.contains(successor);
                double alternativeCost = successor.getCostComingFrom(node);
                if (alternativeCost < successor.getCost()) {
                    //_openList.remove(successor);

                    successor.setCameFrom(node);
                    successor.setCost(alternativeCost);
                    if (!_openList.contains(successor))
                        _openList.add(successor);
                }
            }
        }
        return new LinkedList<>();
    }
}
