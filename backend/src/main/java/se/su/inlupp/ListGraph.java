package se.su.inlupp;

/*import java.util.Collection;
import java.util.Iterator;
import java.util.Set;*/
import java.util.*;

public class ListGraph<T> implements Graph<T> {

    private final Map<T, List<Edge<T>>> adjList = new HashMap<>();

    @Override
    public void add(T node) {

        adjList.putIfAbsent(node, new ArrayList<>());
    }

    @Override
    public void remove(T node) {

        if (!adjList.containsKey(node)) {
            throw new NoSuchElementException();
        }

        for (Edge<T> edge : new ArrayList<>(adjList.get(node))) {

            T destination = edge.getDestination();

            adjList.get(destination)
                    .removeIf(e -> e.getDestination().equals(node));
        }

        adjList.remove(node);
    }

    @Override
    public boolean hasNode(T node) {

        return adjList.containsKey(node);
    }

    @Override
    public void connect(T node1, T node2, String name, int weight) {

        if (!hasNode(node1) || !hasNode(node2)) {
            throw new NoSuchElementException();
        }

        if (weight < 0) {
            throw new IllegalArgumentException();
        }

        if (getEdgeBetween(node1, node2) != null) {
            throw new IllegalStateException();
        }

        Edge<T> edge1 = new ListEdge(node2, name, weight);
        Edge<T> edge2 = new ListEdge(node1, name, weight);

        adjList.get(node1).add(edge1);
        adjList.get(node2).add(edge2);
    }

    @Override
    public void disconnect(T node1, T node2) {

        if (!hasNode(node1) || !hasNode(node2)) {
            throw new NoSuchElementException();
        }

        Edge<T> edge = getEdgeBetween(node1, node2);

        if (edge == null) {
            throw new IllegalStateException();
        }

        adjList.get(node1)
                .removeIf(e -> e.getDestination().equals(node2));

        adjList.get(node2)
                .removeIf(e -> e.getDestination().equals(node1));
    }

    @Override
    public void setConnectionWeight(T node1, T node2, int weight) {

        if (weight < 0) {
            throw new IllegalArgumentException();
        }

        Edge<T> edge1 = getEdgeBetween(node1, node2);
        Edge<T> edge2 = getEdgeBetween(node2, node1);

        if (edge1 == null || edge2 == null) {
            throw new NoSuchElementException();
        }

        edge1.setWeight(weight);
        edge2.setWeight(weight);
    }

    @Override
    public Set<T> getNodes() {

        return new HashSet<>(adjList.keySet());
    }

    @Override
    public Collection<Edge<T>> getEdgesFrom(T node) {

        if (!hasNode(node)) {
            throw new NoSuchElementException();
        }

        return new ArrayList<>(adjList.get(node));
    }

    @Override
    public Edge<T> getEdgeBetween(T node1, T node2) {

        if (!hasNode(node1) || !hasNode(node2)) {
            throw new NoSuchElementException();
        }

        for (Edge<T> edge : adjList.get(node1)) {

            if (edge.getDestination().equals(node2)) {
                return edge;
            }
        }

        return null;
    }

    @Override
    public Iterator<T> iterator() {

        return adjList.keySet().iterator();
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for (T node : adjList.keySet()) {

            sb.append(node).append(": ");

            for (Edge<T> edge : adjList.get(node)) {

                sb.append(edge).append(" ");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    private class ListEdge implements Edge<T> {

        private final T destination;
        private final String name;
        private int weight;

        public ListEdge(T destination, String name, int weight) {

            this.destination = destination;
            this.name = name;
            this.weight = weight;
        }

        @Override
        public T getDestination() {

            return destination;
        }

        @Override
        public int getWeight() {

            return weight;
        }

        @Override
        public void setWeight(int weight) {

            if (weight < 0) {
                throw new IllegalArgumentException();
            }

            this.weight = weight;
        }

        @Override
        public String getName() {

            return name;
        }

        @Override
        public String toString() {

            return String.format(
                    "till %s med %s tar %d",
                    destination,
                    name,
                    weight
            );
        }
    }
}

