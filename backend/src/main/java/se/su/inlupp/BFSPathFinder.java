package se.su.inlupp;
import java.util.*;

public class BFSPathFinder<T> implements PathFinder<T> {

    @Override
    public Path<T> findPath(Graph<T> graph, T from, T to) {
        if (!graph.hasNode(from) || !graph.hasNode(to)) {
            return null;
        }
        Queue<T> queue = new LinkedList<>();
        Set<T> visited = new HashSet<>();

        Map<T, Edge<T>> previousEdge = new HashMap<>();
        Map<T, T> previousNode = new HashMap<>();
        queue.add(from);
        visited.add(from);

        while (!queue.isEmpty()) {
            T current = queue.poll();
            if (current.equals(to)) {
                break;
            }

            for (Edge<T> edge : graph.getEdgesFrom(current)) {
                T neighbor = edge.getDestination();

                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    previousEdge.put(neighbor, edge);
                    previousNode.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        if (!visited.contains(to)) {
            return null;
        }

        List<Edge<T>> pathEdges = new ArrayList<>();
        T current = to;

        while (!current.equals(from)) {
            Edge<T> edge = previousEdge.get(current);
            pathEdges.add(edge);
            current = previousNode.get(current);
        }

        Collections.reverse(pathEdges);
        return new SimplePath<>(from, pathEdges);
    }

    private static class SimplePath<T> implements Path<T> {
        private final T start;
        private final List<Edge<T>> edges;

        public SimplePath(T start, List<Edge<T>> edges) {
            this.start = start;
            this.edges = new ArrayList<>(edges);
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
            int total = 0;

            for (Edge<T> edge : edges) {
                total += edge.getWeight();
            }
            return total;
        }

        @Override
        public List<Edge<T>> getEdges() {
            return new ArrayList<>(edges);
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
            return "Path: " + getNodes() +
                    ", total weight = " + getTotalWeight();
        }
    }
}

