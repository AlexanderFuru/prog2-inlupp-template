package se.su.inlupp;

public class GraphEdge<T> implements Edge<T> {
    private final T destination;
    private final String name;
    private int weight;

    public GraphEdge(T destination, int weight, String name) {
        this.destination = destination;
        this.weight = weight;
        this.name = name;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public T getDestination() {
        return destination;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " till " + destination + " (Vikt: " + weight + ")";
    }
}

