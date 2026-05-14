package se.su.inlupp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GraphPath<T> implements Path<T> {
    private final T start;
    private final List<Edge<T>> edges;

    public GraphPath(T start, List<Edge<T>> edges) {
        this.start = start;
        this.edges = new ArrayList<>(edges);
    }

    public GraphPath(T start) {
        this(start, new ArrayList<>());
    }

    @Override
    public T getStart() {
        return start;
    }

    @Override
    public T getEnd() {
        if (edges.isEmpty()) {
            return start;
        }
        return edges.get(edges.size() - 1).getDestination();
    }

    @Override
    public int getTotalWeight() {
        int sum = 0;
        for (Edge<T> edge : edges) {
            sum += edge.getWeight();
        }
        return sum;
    }

    @Override
    public List<Edge<T>> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    @Override
    public List<T> getNodes() {
        List<T> nodes = new ArrayList<>();
        nodes.add(start);
        for (Edge<T> edge : edges) {
            nodes.add(edge.getDestination());
        }
        return nodes;
    }

    @Override
    public Iterator<Edge<T>> iterator() {
        return edges.iterator();
    }

    @Override
    public String toString() {
        return "Path från " + getStart() + " till " + getEnd() + 
               " (Noder: " + getNodes().size() + ", Vikt: " + getTotalWeight() + ")";
    }
}
