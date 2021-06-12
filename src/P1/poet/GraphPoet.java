/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package P1.poet;

import P1.graph.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * A graph-based poetry generator.
 *
 * <p>GraphPoet is initialized with a corpus of text, which it uses to derive a
 * word affinity graph.
 * Vertices in the graph are words. Words are defined as non-empty
 * case-insensitive strings of non-space non-newline characters. They are
 * delimited in the corpus by spaces, newlines, or the ends of the file.
 * Edges in the graph count adjacencies: the number of times "w1" is followed by
 * "w2" in the corpus is the weight of the edge from w1 to w2.
 *
 * <p>For example, given this corpus:
 * <pre>    Hello, HELLO, hello, goodbye!    </pre>
 * <p>the graph would contain two edges:
 * <ul><li> ("hello,") -> ("hello,")   with weight 2
 *     <li> ("hello,") -> ("goodbye!") with weight 1 </ul>
 * <p>where the vertices represent case-insensitive {@code "hello,"} and
 * {@code "goodbye!"}.
 *
 * <p>Given an input string, GraphPoet generates a poem by attempting to
 * insert a bridge word between every adjacent pair of words in the input.
 * The bridge word between input words "w1" and "w2" will be some "b" such that
 * w1 -> b -> w2 is a two-edge-long path with maximum-weight weight among all
 * the two-edge-long paths from w1 to w2 in the affinity graph.
 * If there are no such paths, no bridge word is inserted.
 * In the output poem, input words retain their original case, while bridge
 * words are lower case. The whitespace between every word in the poem is a
 * single space.
 *
 * <p>For example, given this corpus:
 * <pre>    This is a test of the Mugar Omni Theater sound system.    </pre>
 * <p>on this input:
 * <pre>    Test the system.    </pre>
 * <p>the output poem would be:
 * <pre>    Test of the system.    </pre>
 *
 * <p>PS2 instructions: this is a required ADT class, and you MUST NOT weaken
 * the required specifications. However, you MAY strengthen the specifications
 * and you MAY add additional methods.
 * You MUST use Graph in your rep, but otherwise the implementation of this
 * class is up to you.
 */
public class GraphPoet {

    private final Graph<String> graph = Graph.empty();

    // Abstraction function:
    //   TODO
    // Representation invariant:
    //   TODO
    // Safety from rep exposure:
    //   TODO

    /**
     * Create a new poet with the graph from corpus (as described above).
     *
     * @param corpus text file from which to derive the poet's affinity graph
     * @throws IOException if the corpus file cannot be found or read
     */
    public GraphPoet(File corpus) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(corpus));

        String inputBuf;
        while ((inputBuf = br.readLine()) != null) { // read corpus line by line, each line is a sentence
            List<String> wordList = parseLine(inputBuf);

            for (String word : wordList) // update vertex
                graph.add(word);

            int wordCount = wordList.size();
            for (int i = 0; i < wordCount - 1; i++) { // update edges
                String src = wordList.get(i);
                String tar = wordList.get(i + 1);
                int w = graph.targets(src).getOrDefault(tar, 0) + 1;
                graph.set(src, tar, w);
            }
        }
    }

    /**
     * Split the given line into a list consists of each word.
     *
     * @param line a text line
     * @return formatted line, which is a list consists of each word
     */
    private List<String> parseLine(String line) {
        List<String> formattedLine = new ArrayList<>();

        StringTokenizer token = new StringTokenizer(line, " ");
        while (token.hasMoreTokens()) {
            String word = token.nextToken().trim().toLowerCase(); // trim and lower
            formattedLine.add(word);
        }

        return formattedLine;
    }

    private void checkRep() {
        // TODO checkRep
    }

    /**
     * Generate a poem.
     *
     * @param input string from which to create the poem
     * @return poem (as described above)
     */
    public String poem(String input) {
        List<String> formattedInput = parseLine(input);
        int wordCount = formattedInput.size();

        if (wordCount == 0) // empty input
            return "";

        StringBuilder sb = new StringBuilder();

        String src, tar = null;
        for (int i = 0; i < wordCount - 1; i++) {
            src = formattedInput.get(i);
            tar = formattedInput.get(i + 1);

            /* Find possible bridge words */
            Map<String, Integer> bridgeFromSrc = graph.targets(src);
            Map<String, Integer> bridgeFromTar = graph.sources(tar);

            Set<String> bridgeSet = new HashSet<>(bridgeFromSrc.keySet());
            bridgeSet.retainAll(bridgeFromTar.keySet());

            /* Find the best bridge word */
            String bestBridge = null;
            int max_w = -1;
            for (String bridge : bridgeSet) {
                int w = bridgeFromSrc.get(bridge) + bridgeFromTar.get(bridge); // current weight
                if (bestBridge == null) { // first possible bridge, choose this
                    bestBridge = bridge;
                    max_w = w;
                } else { // not the first one, need to compare
                    if (w > max_w) { // better bridge word than the chosen before, choose this
                        bestBridge = bridge;
                        max_w = w;
                    }
                }
            }

            /* generate word */
            if (i == 0) // first word of the sentence, upper its first letter
                sb.append(src.substring(0, 1).toUpperCase()).append(src.substring(1));
            else
                sb.append(' ').append(src);

            if (bestBridge != null)
                sb.append(' ').append(bestBridge);

        }
        assert tar != null;
        sb.append(' ').append(tar);

        return sb.toString();
    }

    // TODO toString()
    @Override
    public String toString() {
        return graph.toString();
    }
}
