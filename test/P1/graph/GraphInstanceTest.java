/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package P1.graph;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for instance methods of Graph.
 *
 * <p>PS2 instructions: you MUST NOT add constructors, fields, or non-@Test
 * methods to this class, or change the spec of {@link #emptyInstance()}.
 * Your tests MUST only obtain Graph instances by calling emptyInstance().
 * Your tests MUST NOT refer to specific concrete implementations.
 */
public abstract class GraphInstanceTest {

    // Testing strategy
    //   test every interface in Graph
    //     add
    //       1. whether can add without error
    //       2. both adding new and adding existed can return a correct value
    //     set
    //       1. whether can set without error
    //       2. whether can return prev weight
    //       3. if set zero, whether can delete the edge
    //     remove
    //       whether can remove the vertex with all edges related to it
    //     vertices, sources, targets
    //       whether can return correct data after mutated
    //       use different and mixed mutators

    /**
     * Overridden by implementation-specific test classes.
     *
     * @return a new empty graph of the particular implementation being tested
     */
    public abstract Graph<String> emptyInstance();

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testInitialVerticesEmpty() {
        assertEquals("expected new graph to have no vertices",
                Collections.emptySet(), emptyInstance().vertices());
    }

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
}
