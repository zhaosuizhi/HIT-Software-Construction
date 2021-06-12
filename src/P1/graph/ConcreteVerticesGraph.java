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
public class ConcreteVerticesGraph<T> implements Graph<T> {

    private final List<Vertex<T>> vertices = new ArrayList<>();
    private final Set<T> nameSet = new HashSet<>(); // all names on the graph, store for easy to search

    // Abstraction function:
    //   AF(vertices) = Directed Graph D = (V, E)
    //       V = all vertex.getSources() and vertex.getTargets() union together for vertex in vertices
    //       E = from src to vertex.getName for src in vertex.getSources()
    //             unions from vertex.getName to tar for tar in vertex.getTargets()
    //             for vertex in vertices
    // Representation invariant:
    //   all sources and targets are on the graph
    // Safety from rep exposure:
    //   All fields are "private final" so they can't be reassigned
    //   All fields are mutable, so defensive copy in methods: vertices


    private void checkRep() {
        for (Vertex<T> v : vertices) {
            nameSet.add(v.getName());
        }

        for (Vertex<T> v : vertices) {
            Map<T, Integer> srcSet = v.getSources();
            assert nameSet.containsAll(srcSet.keySet());
            Map<T, Integer> tarSet = v.getTargets();
            assert nameSet.containsAll(tarSet.keySet());
        }
    }

    @Override
    public boolean add(T vertex) {
        if (nameSet.contains(vertex)) {
            return false;
        } else {
            vertices.add(new Vertex<>(vertex));
            nameSet.add(vertex);
            checkRep();
            return true;
        }
    }

    @Override
    public int set(T source, T target, int weight) {
        if (!nameSet.contains(source) || !nameSet.contains(target))
            return 0;

        int ret = 0;
        for (Vertex<T> v : vertices) {
            if (v.getName().equals(source))
                ret = v.updateTarget(target, weight);
            else if (v.getName().equals(target))
                ret = v.updateSource(source, weight);
            // these two values would be same
        }
        checkRep();
        return ret;
    }

    @Override
    public boolean remove(T vertex) {
        boolean ret = nameSet.remove(vertex);
        if (ret) { // 移出与该顶点有关的所有边
            vertices.removeIf(e -> e.getName().equals(vertex));
            for (Vertex<T> v : vertices) {
                v.updateSource(vertex, 0); // remove the vertex as source
                v.updateTarget(vertex, 0); // remove the vertex as target
            }
        }
        checkRep();
        return ret;
    }

    @Override
    public Set<T> vertices() {
        return new HashSet<>(nameSet);
    }

    @Override
    public Map<T, Integer> sources(T target) {
        if (nameSet.contains(target)) {
            for (Vertex<T> v : vertices)
                if (v.getName().equals(target))
                    return new HashMap<>(v.getSources());
        }

        return new HashMap<>();
    }

    @Override
    public Map<T, Integer> targets(T source) {
        if (nameSet.contains(source)) {
            for (Vertex<T> v : vertices)
                if (v.getName().equals(source))
                    return new HashMap<>(v.getTargets());
        }

        return new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph {\n");

        sb.append("\tVertices: ")
                .append(String.join(", ", nameSet.toString()))
                .append('\n');

        sb.append("\tEdges:\n");
        for (Vertex<T> v : vertices) {
            T name = v.getName();
            Map<T, Integer> targetsMap = v.getTargets();
            for (T tarName : targetsMap.keySet()) {
                sb.append("\t\t").append(name)
                        .append("->").append(tarName)
                        .append(':').append(targetsMap.get(tarName))
                        .append('\n');
            }
        }

        sb.append("}");
        return sb.toString();
    }
}

/**
 * Vertex in a ConcreteVerticesGraph
 * Mutable.
 * This class is internal to the rep of ConcreteVerticesGraph.
 *
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Vertex<T> {
    private final T name;
    private final Map<T, Integer> sourceSet = new HashMap<>(); // 入边集及其权重
    private final Map<T, Integer> targetSet = new HashMap<>(); // 出边集及其权重

    // Abstraction function:
    //   AF(name, sourceSet, targetSet) = a vertex in a directed graph that
    //     any src->w in sourceSet, from src to name is a directed edge whose weight is w
    //     any tar->w in targetSet, from name to tar is a directed edge whose weight is w
    // Representation invariant:
    //   sourceSet.containsKey(name) == false
    //   targetSet.containsKey(name) == false
    // Safety from rep exposure:
    //   All fields are "private final" so they can't be reassigned
    //   Map is mutable, so defensive copy in getSources and getTargets
    //   source and target are guaranteed by spec since there's no silver bullet for copying generics

    public Vertex(T vertex) {
        name = vertex;
    }

    private void checkRep() {
        assert !sourceSet.containsKey(name);
        assert !targetSet.containsKey(name);
    }

    /**
     * Get the name of this vertex
     *
     * @return name
     */
    public T getName() {
        return name;
    }

    /**
     * Get all sources of this vertex
     *
     * @return a map of source->weight. <p>Caller must never mutate every T object in the returned map again!</p>
     */
    public Map<T, Integer> getSources() {
        return new HashMap<>(sourceSet);
    }

    /**
     * Get all targets of this vertex
     *
     * @return a map of target->weight. <p>Caller must never mutate every T object in the returned map again!</p>
     */
    public Map<T, Integer> getTargets() {
        return new HashMap<>(targetSet);
    }

    /**
     * Add, change, or remove a weighted directed edge in this graph.
     * If weight is nonzero, add an edge or update the weight of that edge;
     * vertices with the given labels are added to the graph if they do not
     * already exist.
     * If weight is zero, remove the edge if it exists
     *
     * @param source the source vertex
     * @param weight its weight
     * @return if the edge already had and just update its weight
     */
    public int updateSource(T source, int weight) {
        return updateRelationship(source, weight, sourceSet);
    }

    /**
     * Add, change, or remove a weighted directed edge in this graph.
     * If weight is nonzero, add an edge or update the weight of that edge;
     * vertices with the given labels are added to the graph if they do not
     * already exist.
     * If weight is zero, remove the edge if it exists
     *
     * @param target the target vertex
     * @param weight its weight
     * @return the previous weight of the edge, or zero if there was no such
     */
    public int updateTarget(T target, int weight) {
        return updateRelationship(target, weight, targetSet);
    }

    private int updateRelationship(T source, int weight, Map<T, Integer> set) {
        assert weight >= 0;
        assert !name.equals(source);

        Integer wBefore;
        if (weight > 0) // update
            wBefore = set.put(source, weight);
        else // remove
            wBefore = set.remove(source);

        checkRep();
        if (wBefore == null)
            return 0;
        else
            return wBefore;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vertex {\n");
        for (T source : sourceSet.keySet()) {
            sb.append('\t').append(source).append("->").append(name).append(':').append(sourceSet.get(source)).append('\n');
        }
        for (T target : targetSet.keySet()) {
            sb.append('\t').append(name).append("->").append(target).append(':').append(targetSet.get(target)).append('\n');
        }
        sb.append("}");
        return sb.toString();
    }
}
