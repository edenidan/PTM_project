package client_side.ui.models;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class MultiSourcePrintWriter {
    private final PrintWriter out;
    private List<BlockingQueue<String>> inputs;
    private boolean stop = false;

    public MultiSourcePrintWriter(PrintWriter out) {
        this.out = out;

        new Thread(this::backgroundJob).start();
    }

    private void backgroundJob() {
        while (!stop) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            for (BlockingQueue<String> q : inputs) {
                try {
                    Thread.sleep(300 / inputs.size());//maximum response time of 300 millis
                } catch (InterruptedException ignored) {
                }
                String line = q.poll();
                if (line == null)
                    continue;
                this.out.println(line);
            }
        }
    }

    public BlockingQueue<String> getInputChannel() {
        BlockingQueue<String> newChannel = new LinkedBlockingDeque<>();
        inputs.add(newChannel);
        return newChannel;
    }

    public void unsubscribe(BlockingQueue<String> q) {
        inputs.remove(q);
    }

    @Override
    public void finalize() {
        this.stop = true;
    }

}
