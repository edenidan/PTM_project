package client_side.ui.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class MultiOutputsBufferedReader {
    private final BufferedReader in;
    private final List<BlockingQueue<String>> outputs = new ArrayList<>();
    private boolean stop = false;
    private final Thread thread;

    public MultiOutputsBufferedReader(BufferedReader in) {
        this.in = in;

        thread = new Thread(this::backgroundJob);
        thread.start();
    }

    private void backgroundJob() {
        String line;
        while (!stop)
            try {
                Thread.sleep(100);
                while (true) {
                    line = in.readLine();
                    if (line == null) break;
                    for (BlockingQueue<String> q : outputs)
                        q.put(line);
                }
            } catch (IOException | InterruptedException ignored) {
            }
    }

    public BlockingQueue<String> getOutputChannel() {
        BlockingQueue<String> newChannel = new LinkedBlockingDeque<>();
        outputs.add(newChannel);
        return newChannel;
    }

    public void unsubscribe(BlockingQueue<String> q) {
        outputs.remove(q);
    }

    @Override
    public void finalize() {
        this.stop = true;
        thread.interrupt();
    }
}
