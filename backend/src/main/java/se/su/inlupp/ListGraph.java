package se.su.inlupp;

import java.util.*;

public class ListGraph<T> implements Graph<T> {

  private final Map<T, List<Edge<T>>> nodes = new HashMap <>();

  @Override
  public void add(T node) {
    nodes.putIfAbsent(node, new ArrayList<>());
  }

  @Override
  public void remove(T node) {
    throw new UnsupportedOperationException("Unimplemented method 'remove'");
  }

  @Override
  public boolean hasNode(T node) {
    throw new UnsupportedOperationException("Unimplemented method 'hasNode'");
  }

  @Override
  public void connect(T node1, T node2, String name, int weight) {
    
    // Om någon av noderna saknas i grafen skall undantaget NoSuchElementException genereras.

    if(!nodes.containsKey(node1) || !nodes.containsKey(node2))
    {
      throw new NoSuchElementException();
    }

    //Om vikten är negativ skall undantaget IllegalArgumentException genereras. 

    if (weight < 0) 
    {
      throw new IllegalArgumentException();
    }

    //Om en kant redan finns mellan dessa två noder skall undantaget IllegalStateException genereras.

    for (Edge<T> edge : nodes.get(node1)) 
    {
      if (edge.getDestination().equals(node2)) 
        {
          throw new IllegalStateException();
        }
    }

    nodes.get(node1).add(new Edge<>(node2, name, weight));
    nodes.get(node2).add(new Edge<>(node1, name, weight));

  }

  @Override
  public void disconnect(T node1, T node2) {
    throw new UnsupportedOperationException("Unimplemented method 'disconnect'");
  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {
    throw new UnsupportedOperationException("Unimplemented method 'setConnectionWeight'");
  }

  @Override
  public Set<T> getNodes() {
    throw new UnsupportedOperationException("Unimplemented method 'getNodes'");
  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) {
    throw new UnsupportedOperationException("Unimplemented method 'getEdgesFrom'");
  }

  @Override
  public Edge<T> getEdgeBetween(T node1, T node2) {
    throw new UnsupportedOperationException("Unimplemented method 'getEdgeBetween'");
  }

  @Override
  public Iterator<T> iterator() {
    throw new UnsupportedOperationException("Unimplemented method 'iterator'");
  }
}

