/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package P1.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests for ConcreteVerticesGraph.
 * <p>
 * This class runs the GraphInstanceTest tests against ConcreteVerticesGraph, as
 * well as tests for that particular implementation.
 * <p>
 * Tests against the Graph spec should be in GraphInstanceTest.
 */
public class ConcreteVerticesGraphTest extends GraphInstanceTest {

    /*
     * Provide a ConcreteVerticesGraph for tests in GraphInstanceTest.
     */
    @Override
    public Graph<String> emptyInstance() {
        return new ConcreteVerticesGraph<>();
    }

    /*
     * Testing ConcreteVerticesGraph...
     */

    // Testing strategy for ConcreteVerticesGraph.toString()
    //   Insert several vertices and edges, then check if the output is correct

    @Test
    public void testGraphToString() {
        Graph<String> graph = emptyInstance();

        String a = "a";
        String b = "b";
        int w = 2;

        graph.add(a);
        graph.add(b);
        graph.set(a, b, w);
        graph.set(b, a, w);

        Assert.assertEquals("Graph {\n" +
                "\tVertices: [a, b]\n" +
                "\tEdges:\n" +
                "\t\ta->b:2\n" +
                "\t\tb->a:2\n" +
                "}", graph.toString());
    }

    /*
     * Testing Vertex...
     */

    // Testing strategy for Vertex
    //   test every public method of Vertex
    //   grouped by type of the method

    @Test
    public void testVertexConstructor() {
        String a = "a";
        new Vertex<>(a);
    }

    /**
     * Test all observers of Vertex
     */
    @Test
    public void testVertexGet() {
        String a = "a";
        String b = "b";
        int w = 1;

        Vertex<String> v = new Vertex<>(a);
        v.updateTarget(b, w);

        Assert.assertEquals(a, v.getName()); // getName

        Assert.assertEquals(new HashMap<>(), v.getSources()); // getSources

        Map<String, Integer> set = new HashMap<>();
        set.put(b, w);
        Assert.assertEquals(set, v.getTargets()); // getTargets
    }

    /**
     * Test all mutators of Vertex
     */
    @Test
    public void testVertexUpdate() {
        String a = "a";
        String b = "b";
        int w1 = 1;
        int w2 = 2;

        Vertex<String> v = new Vertex<>(a);

        Assert.assertEquals(0, v.updateSource(b, w2));
        Assert.assertEquals(w2, v.updateSource(b, 0));
        Assert.assertEquals(0, v.updateTarget(b, w1));
        Assert.assertEquals(w1, v.updateTarget(b, w2));

        Map<String, Integer> srcSet = new HashMap<>();
        Map<String, Integer> tarSet = new HashMap<>();
        tarSet.put(b, w2);
        Assert.assertEquals(srcSet, v.getSources());
        Assert.assertEquals(tarSet, v.getTargets());
    }

    @Test
    public void testVertexToString() {
        Vertex<String> v = new Vertex<>("a");
        v.updateTarget("b", 1);
        v.updateSource("c", 2);

        Assert.assertEquals("Vertex {\n" +
                "\tc->a:2\n" +
                "\ta->b:1\n" +
                "}", v.toString());
    }
}
