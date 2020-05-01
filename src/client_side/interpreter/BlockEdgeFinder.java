package client_side.interpreter;

import java.util.List;

public class BlockEdgeFinder {
    public static int getBlockStart(List<String> tokens, int startIndex) {
        return tokens.subList(startIndex, tokens.size()).indexOf("{") + startIndex;
    }

    // startIndex is before the start of the open '{'
    public static int getBlockEnd(List<String> tokens, int startIndex) {
        int blockStart = getBlockStart(tokens, startIndex);

        int numberOfOpens = 0;
        for (int i = blockStart; i < tokens.size(); i++) {
            if (tokens.get(i).equals("}"))
                numberOfOpens--;
            else if (tokens.get(i).equals("{"))
                numberOfOpens++;

            if (numberOfOpens == 0)
                return i;
        }
        return -1;
    }
}
