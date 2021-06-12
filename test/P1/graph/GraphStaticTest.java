/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package P1.graph;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

/**
 * Tests for static methods of Graph.
 * <p>
 * To facilitate testing multiple implementations of Graph, instance methods are
 * tested in GraphInstanceTest.
 */
public class GraphStaticTest {

    // Testing strategy
    //   empty()
    //     no inputs, only output is empty graph
    //     observe with vertices()

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testEmptyVerticesEmpty() {
        assertEquals("expected empty() graph to have no vertices",
                Collections.emptySet(), Graph.empty().vertices());
    }

    @Test
    public void testGenericGraph() {
        Graph<Double> graph = Graph.empty();
        graph.add(3.14);
        graph.add(159.0);
        graph.add(26.0);
        graph.add(0.0);

        graph.set(3.14, 159.0, 1);
        graph.set(159.0, 26.0, 2);
        graph.set(0.0, 159.0, 3);

        assertEquals("Graph {\n" +
                "\tVertices: [0.0, 159.0, 3.14, 26.0]\n" +
                "\tEdges:\n" +
                "\t\t3.14->159.0:1\n" +
                "\t\t159.0->26.0:2\n" +
                "\t\t0.0->159.0:3\n" +
                "}", graph.toString());
    }
}
