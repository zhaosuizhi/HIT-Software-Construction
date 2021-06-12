/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package P1.poet;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Tests for GraphPoet.
 */
public class GraphPoetTest {

    // Testing strategy
    //   test every public method of GraphPoet with several testcases

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testConstructor() throws IOException {
        /* found test */
        new GraphPoet(new File("test/P1/poet/mugar-omni-theater.txt"));

        /* not found test */
        try {
            new GraphPoet(new File("FP(3hP(@he@P!"));
            throw new AssertionError();
        } catch (FileNotFoundException ignored) {
        }
    }

    @Test
    public void testPoem() throws IOException {
        final String input = "Seek to explore new and exciting synergies!";
        GraphPoet poet = new GraphPoet(new File("test/P1/poet/where-no-man-has-gone-before.txt"));

        Assert.assertEquals("Seek to explore strange new life and exciting synergies!", poet.poem(input));
    }

    @Test
    public void testToString() throws IOException {
        GraphPoet poet = new GraphPoet(new File("test/P1/poet/where-no-man-has-gone-before.txt"));
        Assert.assertEquals("GraphPoet {\n" +
                "\tVertices: [new, worlds, explore, and, to, civilizations, seek, strange, life, out]\n" +
                "\tEdges:\n" +
                "\t\tto->explore:1\n" +
                "\t\texplore->strange:1\n" +
                "\t\tstrange->new:1\n" +
                "\t\tnew->worlds:1\n" +
                "\t\tto->seek:1\n" +
                "\t\tseek->out:1\n" +
                "\t\tout->new:1\n" +
                "\t\tnew->life:1\n" +
                "\t\tlife->and:1\n" +
                "\t\tand->new:1\n" +
                "\t\tnew->civilizations:1\n" +
                "}", poet.toString());

        poet = new GraphPoet(new File("test/P1/poet/hello.txt"));
        Assert.assertEquals("GraphPoet {\n" +
                "\tVertices: [hello,, goodbye!]\n" +
                "\tEdges:\n" +
                "\t\thello,->hello,:2\n" +
                "\t\thello,->goodbye!:1\n" +
                "}", poet.toString());

    }
}
