package se.su.inlupp;
import java.util.*;

public class DFSPathFinder<T> implements PathFinder<T> {

    @Override
    public Path<T> findPath(Graph<T> graph, T from, T to) {
        if (!graph.hasNode(from) || !graph.hasNode(to)) {
            return null;
        }

        Set<T> visited = new HashSet<>();
        List<Edge<T>> path = new ArrayList<>();

        boolean found = dfs(graph, from, to, visited, path);
        if (!found) {
            return null;
        }
        return new SimplePath<>(from, path);
    }

    private boolean dfs(Graph<T> graph, T current, T goal, Set<T> visited, List<Edge<T>> path) {
        if (current.equals(goal)) {
            return true;
        }
        visited.add(current);

        for (Edge<T> edge : graph.getEdgesFrom(current)) {
            T next = edge.getDestination();
            if (!visited.contains(next)) {
                path.add(edge);

                if (dfs(graph, next, goal, visited, path)) {
                    return true;
                }
                path.remove(path.size() - 1);
            }
        }

        return false;
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
            return "Path: " + getNodes() + ", total weight = " + getTotalWeight();
        }
    }
}
