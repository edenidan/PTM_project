package client_side.ui.models;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class MultiSourcePrintWriter {
    private final PrintWriter out;
    private final BlockingQueue<String> inputs = new LinkedBlockingQueue<>();
    private boolean stop = false;
    private final Thread thread;

    public MultiSourcePrintWriter(PrintWriter out) {
        this.out = out;

        thread = new Thread(this::backgroundJob);
        thread.start();
    }

    private void backgroundJob() {
        while (!stop) {
            String line = null;
            try {
                line = inputs.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.out.println(line);
            this.out.flush();
        }
    }

    public BlockingQueue<String> getInputChannel() {
        return inputs;
    }

    @Override
    public void finalize() {
        this.stop = true;
        thread.interrupt();
    }

}
