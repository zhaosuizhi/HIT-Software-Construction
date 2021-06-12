/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package P1.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    @Test
    public void testAdd() {
        Graph<String> graph = emptyInstance();
        String a = "a";
        String b = "b";

        Assert.assertTrue(graph.add(a));
        Assert.assertTrue(graph.add(b));
        Assert.assertFalse(graph.add(b));
    }

    @Test
    public void testSet() {
        Graph<String> graph = emptyInstance();
        String a = "a";
        String b = "b";
        String c = "c";

        graph.add(a);
        graph.add(b);
        int w1 = 3;
        int w2 = 5;

        Assert.assertEquals(0, graph.set(a, b, w1));
        Assert.assertEquals(0, graph.set(b, a, w2));
        Assert.assertEquals(0, graph.set(b, b, w2));
        Assert.assertEquals(w1, graph.set(a, b, w2));

        Assert.assertEquals(0, graph.set(c, a, w2));

        Map<String, Integer> srcA = graph.sources(a);
        Assert.assertEquals(1, srcA.size());
        Assert.assertEquals(w2, (int) srcA.get(b));

        Map<String, Integer> srcB = graph.sources(b);
        Assert.assertEquals(w2, (int) srcB.get(a));
    }

    @Test
    public void testRemove() {
        Graph<String> graph = emptyInstance();
        String a = "a";
        String b = "b";
        String c = "c";

        graph.add(a);
        graph.add(b);
        int w = 5;

        graph.set(a, b, w);
        graph.set(b, a, w);
        Assert.assertTrue(graph.remove(a));
        Assert.assertFalse(graph.remove(c));

        Map<String, Integer> srcB = graph.sources(b);
        Assert.assertEquals(0, srcB.size());
    }

    @Test
    public void testVertices() {
        Graph<String> graph = emptyInstance();
        String a = "a";
        String b = "b";
        String c = "c";

        graph.add(a);
        graph.add(b);
        graph.add(c);

        Set<String> vertices = graph.vertices();
        Assert.assertEquals(3, vertices.size());
        Assert.assertTrue(vertices.contains(a));
        Assert.assertTrue(vertices.contains(b));
        Assert.assertTrue(vertices.contains(c));
    }

    @Test
    public void testSources() {
        Graph<String> graph = emptyInstance();
        String a = "a";
        String b = "b";
        String c = "c";

        graph.add(a);
        graph.add(b);
        graph.add(c);
        graph.set(b, a, 1);
        graph.set(c, a, 2);

        Map<String, Integer> src = graph.sources(a);
        Assert.assertEquals(2, src.size());
        Assert.assertEquals(1, (int) src.get(b));
        Assert.assertEquals(2, (int) src.get(c));
    }

    @Test
    public void testTargets() {
        Graph<String> graph = emptyInstance();
        String a = "a";
        String b = "b";
        String c = "c";

        graph.add(a);
        graph.add(b);
        graph.add(c);
        graph.set(b, a, 1);
        graph.set(c, a, 2);

        Map<String, Integer> src = graph.targets(c);
        Assert.assertEquals(1, src.size());
        Assert.assertEquals(2, (int) src.get(a));
    }

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
