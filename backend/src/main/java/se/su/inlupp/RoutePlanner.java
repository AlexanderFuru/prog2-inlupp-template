package se.su.inlupp;

import java.util.ArrayList;

public class RoutePlanner {

    private final Graph<Station> graph;
    private PathFinder<Station> chosenPathFinder;
    private int transfers;

    //skapar RoutePlanner med vald algoritm
    public RoutePlanner(PathFinder<Station> pathFinder) {
        this.graph = new ListGraph<>();
        this.chosenPathFinder = pathFinder;
        this.transfers = 0;

        System.out.println("Graph contains:");
        System.out.println(graph);
    }
    //lägger till station
    public void addStation(Station station) {
        if (station == null) {
            return;
        }

        graph.add(station);
        System.out.println("Added station: " + station);
    }

    // tar bort station
    public void removeStation(Station station) {
        if (station == null) return;

        for (Station s : new ArrayList<>(graph.getNodes())) {
            graph.disconnect(s, station);
            graph.disconnect(station, s);
        }

        graph.remove(station);
    }

    //kopplar ihop stationer
    public void connectStations(Station from, Station to, String line, int weight) {
        if (from == null || to == null || line == null) {
            return;
        }

        graph.connect(from, to, line, weight);
    }

    // kopplar bort stationer
    public void disconnectStations(Station from, Station to) {
        if (from == null || to == null) {
            return;
        }

        graph.disconnect(from, to);
    }

    //byter algoritm
    public void setPathFinder(PathFinder<Station> pathFinder) {
        if (pathFinder != null) {
            this.chosenPathFinder = pathFinder;
        }
    }

    // hämtar vägen mellan stationer
    public Path<Station> getRoute(Station from, Station to) {
        if (from == null || to == null) {
            return null;
        }

        if (chosenPathFinder == null) {
            return null;
        }

        Path<Station> path = chosenPathFinder.findPath(graph, from, to);
        calculateTransfers(path);

        /*System.out.println("FROM: " + from);
        System.out.println("TO: " + to);
        System.out.println("GRAPH: " + graph);*/

        return path;
    }

    // räknar antal byten
    private void calculateTransfers(Path<Station> path) {
        transfers = 0;

        if (path == null || path.getEdges().isEmpty()) {
            return;
        }

        String currentLine = path.getEdges().get(0).getName();

        for (Edge<Station> edge : path.getEdges()) {
            if (!edge.getName().equals(currentLine)) {
                transfers++;
                currentLine = edge.getName();
            }
        }
    }

    public int getTransfers() {
        return transfers;
    }

    public Graph<Station> getGraph() {
        return graph;
    }

    public PathFinder<Station> getChosenPathFinder() {
        return chosenPathFinder;
    }

    public void setAlgorithm(PathFinder<Station> pf) {
    this.chosenPathFinder = pf;
    }
}