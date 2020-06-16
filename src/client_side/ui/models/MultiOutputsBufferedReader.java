package client_side.ui.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class MultiOutputsBufferedReader {
    private BufferedReader in;
    private List<BlockingQueue<String>> outputs;
    private boolean stop=false;

    public MultiOutputsBufferedReader(BufferedReader out){
        this.in = in;

        new Thread(this::backgroundJob).start();
    }

    private void backgroundJob(){
        String line;
        while(!stop)
            try {
                Thread.sleep(100);
                while (!"bye".equals(line = in.readLine()) && line != null)
                    for (BlockingQueue<String> q : outputs) {
                        try {
                            Thread.sleep(300 / outputs.size());//maximum response time of 300 millis
                        } catch (InterruptedException e) { }

                        q.put(line);
                    }
            }catch (IOException | InterruptedException e){}
    }

    public BlockingQueue<String> getOutputChannel(){
        BlockingQueue<String> newChannel = new LinkedBlockingDeque<>();
        outputs.add(newChannel);
        return newChannel;
    }

    public void unsubscribe(BlockingQueue<String> q){
        outputs.remove(q);
    }

    @Override
    public void finalize(){
        this.stop=true;
    }


}
