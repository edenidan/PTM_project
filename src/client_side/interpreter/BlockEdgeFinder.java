package client_side.interpreter;

public class BlockEdgeFinder {
    public static int getBlockStart(String[] tokens, int startIndex) {
        for (; startIndex < tokens.length; startIndex++) {
            if ("{".equals(tokens[startIndex]))
                return startIndex;
        }
        return -1;
    }

    public static int getBlockEnd(String[] tokens, int startIndex)
    {//the start_index is before the start of the open '{'
        int numberOfOpens = 0;
        startIndex = getBlockStart(tokens, startIndex);

        for (; startIndex < tokens.length; startIndex++) {
            if (tokens[startIndex].equals("}"))
                numberOfOpens--;

            if (tokens[startIndex].equals("{"))
                numberOfOpens++;

            if (numberOfOpens == 0)
                return startIndex;
        }
        return -1;
    }
}
