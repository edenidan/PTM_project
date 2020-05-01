package client_side.interpreter;

import java.util.Arrays;

public class BlockEdgeFinder {
    public static int getBlockStart(String[] tokens, int startIndex) {
        return Arrays.asList(tokens).subList(startIndex, tokens.length).indexOf("{") + startIndex;
    }

    // startIndex is before the start of the open '{'
    public static int getBlockEnd(String[] tokens, int startIndex) {
        int blockStart = getBlockStart(tokens, startIndex);

        int numberOfOpens = 0;
        for (int i = blockStart; i < tokens.length; i++) {
            if (tokens[i].equals("}"))
                numberOfOpens--;
            else if (tokens[i].equals("{"))
                numberOfOpens++;

            if (numberOfOpens == 0)
                return i;
        }
        return -1;
    }
}
