/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package P1.poet;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.Collections;
import java.util.List;

/**
 * Tests for GraphPoet.
 */
public class GraphPoetTest {

    // Testing strategy
    //   TODO

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // TODO tests
    @Test
    public void testPoem() throws IOException {
        final String input = "Seek to explore new and exciting synergies!";
        GraphPoet poet = new GraphPoet(new File("src/P1/poet/where-no-man-has-gone-before.txt"));

        Assert.assertEquals("Seek to explore strange new life and exciting synergies!", poet.poem(input));
    }
}
