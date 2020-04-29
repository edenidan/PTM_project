package server_side;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MySerialServer implements Server {
    private boolean stop = false;

    private void operateInCurrentThread(int port, ClientHandler clientHandler) {
        try (ServerSocket server = new ServerSocket(port)) {
            server.setSoTimeout(1000);

            while (!stop) {
                try (Socket client = server.accept()) {
                    clientHandler.handleClient(client.getInputStream(), client.getOutputStream()); // closes the streams
                } catch (SocketTimeoutException ignored) {
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(int port, ClientHandler clientHandler) {
        new Thread(() -> operateInCurrentThread(port, clientHandler)).start();
    }

    @Override
    public void stop() {
        stop = true;
    }
}
