/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package P1.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

/**
 * Tests for ConcreteEdgesGraph.
 * <p>
 * This class runs the GraphInstanceTest tests against ConcreteEdgesGraph, as
 * well as tests for that particular implementation.
 * <p>
 * Tests against the Graph spec should be in GraphInstanceTest.
 */
public class ConcreteEdgesGraphTest extends GraphInstanceTest {

    /*
     * Provide a ConcreteEdgesGraph for tests in GraphInstanceTest.
     */
    @Override
    public Graph<String> emptyInstance() {
        return new ConcreteEdgesGraph();
    }

    /*
     * Testing ConcreteEdgesGraph...
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
        Assert.assertEquals(1, graph.set(a, b, w2));
        Assert.assertEquals(-1, graph.set(b, b, w2));
        Assert.assertEquals(-1, graph.set(b, c, w2));
        Assert.assertEquals(-1, graph.set(c, a, w2));

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

    // Testing strategy for ConcreteEdgesGraph.toString()
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

        System.out.println(graph);

        Assert.assertEquals("ConcreteEdgesGraph {\n" +
                "\tEdge{a->b, w=2}\n" +
                "\tEdge{b->a, w=2}\n" +
                "}\n", graph.toString());
    }

    /*
     * Testing Edge...
     */

    // Testing strategy for Edge
    //    1. Test constructor
    //        if success when source not equals to target
    //        if error when source equals to target
    //        if error when weight == 0
    //        if error when weight < 0
    //    2. test all get methods
    //    3. test toString method

    @Test
    public void testEdgeConstructor() {
        String a = "a";
        String b = "b";
        String anotherA = "a";
        Edge<String> edge;

        edge = new Edge<>(a, b, 1);

        try {
            edge = new Edge<>(a, anotherA, 1);
            throw new AssertionError();
        } catch (AssertionError ignored) {
        }

        try {
            edge = new Edge<>(a, b, 0);
            throw new AssertionError();
        } catch (AssertionError ignored) {
        }

        try {
            edge = new Edge<>(a, b, -1);
            throw new AssertionError();
        } catch (AssertionError ignored) {
        }
    }

    @Test
    public void testEdgeGet() {
        String a = "a";
        String b = "b";
        int w = 1;
        Edge<String> edge = new Edge<>(a, b, w);

        Assert.assertEquals(a, edge.getSource());
        Assert.assertEquals(b, edge.getTarget());
        Assert.assertEquals(w, edge.getWeight());
    }

    @Test
    public void testEdgeToString() {
        String a = "a";
        String b = "b";
        int w = 2;
        Edge<String> edge = new Edge<>(a, b, w);

        Assert.assertEquals(String.format("Edge{%s->%s, w=%d}", a, b, w), edge.toString());
    }
}
