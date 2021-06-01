/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package P1.graph;

import java.util.*;

/**
 * An implementation of Graph.
 *
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteEdgesGraph implements Graph<String> {

    private final Set<String> vertices = new HashSet<>();
    private final List<Edge<String>> edges = new ArrayList<>();

    // Abstraction function:
    //   AF(vertices, edges) = Directed Graph D = (V, E)
    //       V = vertices
    //       E = edges
    // Representation invariant:
    //   1. vertices != null && edges != null
    //   2. for each e in edges, both e.getSource() and e.getTo() in vertices.
    //   The 1st one is guaranteed by "private final" modifier and "new" syntax on each field,
    //   so there's no need to check in checkRep method.
    // Safety from rep exposure:
    //   All fields are "private final" so they can't be reassigned
    //   All fields are mutable, so defensive copy in methods: vertices

    private void checkRep() {
        for (Edge<String> e : edges) {
            assert vertices.contains(e.getTarget());
            assert vertices.contains(e.getSource());
        }
    }

    /**
     * Add a vertex to the graph
     *
     * @param vertex label for the new vertex
     * @return whether vertex had not been on the graph
     */
    @Override
    public boolean add(String vertex) {
        boolean ret = vertices.add(vertex);
        checkRep();
        return ret;
    }

    /**
     * Set a new edge to this graph
     *
     * @param source label of the source vertex
     * @param target label of the target vertex
     * @param weight non-negative weight of the edge
     * @return <p> 0 if an new edge was set
     *         <p> 1 if the edge exists and its weight is update
     *         <p>-1 if argument error
     */
    @Override
    public int set(String source, String target, int weight) {
        if (!vertices.contains(source) || !vertices.contains(target))
            return -1;

        Edge<String> newEdge;
        try {
            newEdge = new Edge<>(source, target, weight);
        } catch (AssertionError e) { // validate error
            return -1;
        }

        int ret = 0;
        for (Edge<String> e : edges) { // remove the old edge
            if (e.getSource().equals(source) && e.getTarget().equals(target)) {
                edges.remove(e);
                ret = 1;
                break;
            }
        }
        edges.add(newEdge);
        checkRep();
        return ret;
    }

    /**
     * Remove the given vertex from this graph
     *
     * @param vertex label of the vertex to remove
     * @return if the vertex was in the graph
     */
    @Override
    public boolean remove(String vertex) {
        boolean ret = vertices.remove(vertex);
        if (ret) { // 移出与该顶点有关的所有边
            edges.removeIf(e -> e.getSource().equals(vertex) || e.getTarget().equals(vertex));
        }
        checkRep();
        return ret;
    }

    /**
     * Get all vertices
     *
     * @return The set of all vertices
     */
    @Override
    public Set<String> vertices() {
        return new HashSet<>(vertices);
    }

    /**
     * Get all sources of the given target
     *
     * @param target a label
     * @return The map of source->weight
     */
    @Override
    public Map<String, Integer> sources(String target) {
        Map<String, Integer> src = new HashMap<>();
        if (vertices.contains(target)) {
            for (Edge<String> e : edges) {
                if (e.getTarget().equals(target))
                    src.put(e.getSource(), e.getWeight());
            }
        }
        return src;
    }

    /**
     * Get all targets of the given source
     *
     * @param source a label
     * @return The map of target->weight
     */
    @Override
    public Map<String, Integer> targets(String source) {
        Map<String, Integer> tar = new HashMap<>();
        if (vertices.contains(source)) {
            for (Edge<String> e : edges) {
                if (e.getSource().equals(source))
                    tar.put(e.getTarget(), e.getWeight());
            }
        }
        return tar;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ConcreteEdgesGraph {\n");
        for (Edge<String> e : edges) {
            sb.append('\t').append(e).append('\n');
        }
        sb.append("}\n");

        return sb.toString();
    }
}

/**
 * TODO specification
 * Immutable.
 * This class is internal to the rep of ConcreteEdgesGraph.
 *
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Edge<T> {

    private final T source;
    private final T target;
    private final int weight;

    // Abstraction function:
    //   AF(from, to, weight) = a weighted directed edge from "from" to "to" whose weight is "weight"
    // Representation invariant:
    //   from.equals(to) == false
    //   weight > 0
    // Safety from rep exposure:
    //   All fields are "private final" so they can't be reassigned
    //   int is immutable
    //   source and target are guaranteed by spec since there's no silver bullet for copying generics

    /**
     * Create a new Edge
     * <p>Caller must never mutate "source" and "target" object again!</p>
     *
     * @param source source vertex.
     * @param target target vertex.
     * @param weight the weight of this edge
     */
    public Edge(T source, T target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;

        checkRep();
    }

    private void checkRep() {
        assert !source.equals(target);
        assert weight > 0;
    }


    /**
     * Get the source of the edge
     *
     * @return source vertex. <p>Caller must never mutate this T object again!</p>
     */
    public T getSource() {
        return source;
    }

    /**
     * Get the target of the edge
     *
     * @return target vertex. <p>Caller must never mutate this T object again!</p>
     */
    public T getTarget() {
        return target;
    }

    /**
     * Get the weight of the edge
     *
     * @return weight
     */
    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format("Edge{%s->%s, w=%d}", source, target, weight);
    }
}
