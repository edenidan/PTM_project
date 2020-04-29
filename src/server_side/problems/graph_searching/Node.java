package server_side.problems.graph_searching;

import java.util.Objects;

public class Node<TState> {
    private final TState state;
    private double cost;
    private Node<TState> cameFrom;

    public Node(TState state, double cost, Node<TState> cameFrom) {
        this.state = state;
        this.cost = cost;
        this.cameFrom = cameFrom;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return Objects.equals(state, ((Node<?>) obj).state);
    }

    public TState getState() {
        return state;
    }

    public double getCost() {
        return cost;
    }

    public double getCostComingFrom(Node<TState> node) {
        return cost - cameFrom.cost + (node != null ? node.cost : 0);
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Node<TState> getCameFrom() {
        return cameFrom;
    }

    public void setCameFrom(Node<TState> cameFrom) {
        this.cameFrom = cameFrom;
    }
}
