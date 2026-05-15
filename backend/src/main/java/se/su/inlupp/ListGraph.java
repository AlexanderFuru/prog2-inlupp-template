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

    // Om noden saknas i grafen skall undantaget NoSuchElementException genereras.
    if(!nodes.containsKey(node))
    {
      throw new NoSuchElementException();
    }

    List<Edge<T>> edges = nodes.get(node);

    for(Edge <T> edge : edges)
      {
        T destination = edge.getDestination();

        nodes.get(destination).removeIf(e -> e.getDestination().equals(node));
      }

    nodes.remove(node);
    
  }

  @Override
  public boolean hasNode(T node) {
   return nodes.containsKey(node);
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
    
    if(!nodes.containsKey(node1) || !nodes.containsKey(node2))
    {
      throw new NoSuchElementException();
    }

    boolean found = false;

    for (Edge<T> edge : nodes.get(node1)) 
    {
      if (edge.getDestination().equals(node2)) 
        {
          found = true;
          break;
        } 
    }
    
    if (!found)
      {
          throw new IllegalStateException();
      }
      
    nodes.get(node1).removeIf(e -> e.getDestination().equals(node2));

    nodes.get(node2).removeIf(e -> e.getDestination().equals(node1));
    
  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {
    
    if(weight < 0)
    {
      throw new IllegalArgumentException();
    }

    if(!nodes.containsKey(node1) || !nodes.containsKey(node2))
    {
      throw new NoSuchElementException();
    }

    boolean found = false;

    for (Edge<T> edge : nodes.get(node1)) 
    {
      if (edge.getDestination().equals(node2)) 
        {
          edge.setWeight(weight);
          found = true;
          break;
        } 
    }
    
    if (!found)
      {
          throw new NoSuchElementException();
      }

    for (Edge<T> edge : nodes.get(node2)) 
    {
      if (edge.getDestination().equals(node1)) 
        {
          edge.setWeight(weight);
          break;
        } 

    }

  }

  @Override
  public Set<T> getNodes() {
    
    return new HashSet<>(nodes.keySet());

  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) {
    if(!nodes.containsKey(node)){
      throw new NoSuchElementException();
    }

     return new ArrayList<>(nodes.get(node));
  }

  @Override
  public Edge<T> getEdgeBetween(T node1, T node2) {
    
    if(!nodes.containsKey(node1) || !nodes.containsKey(node2))
    {
      throw new NoSuchElementException();
    }

     for (Edge<T> edge : nodes.get(node1)) 
    {
       if (edge.getDestination().equals(node2)) 
        {
          return edge;
        } 
    }
  
    return null;
    
  }

  public String toString(){
    StringBuilder sb = new StringBuilder();

    for (Map.Entry<T, List<Edge<T>>> entry : nodes.entrySet())
      {
        sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
      }
      return sb.toString();
  } 

  @Override
  public Iterator<T> iterator() {
    return nodes.keySet().iterator();
  }
}

