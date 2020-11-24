package server;

import server.dijsktra.*;
import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class RunnableApp implements Runnable {

    private final Socket socket;
    private final Gson gson = new Gson();
    boolean isValid = true;

    public RunnableApp(Socket socket) throws IOException {
        this.socket = socket;
    }

    @Override
    @SuppressWarnings("ConvertToTryWithResources")
    public void run() {
        try {
            System.out.println("New client Connected");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            while (true) {
                String input = in.readUTF();

                // get-shortest-path-A-B
                if (input.contains("get-shortest-path")) {
                    String startNode = input.split("-")[3];
                    String endNode = input.split("-")[4];

                    String edgesString = in.readUTF();
                    Dijkstra dijkstra = new Dijkstra(edgesString);
                    System.out.println(edgesString);
                    System.out.println(input);

                    if (dijkstra.validateGraph(startNode, endNode)) {
                        List<String> result = dijkstra.getShortestPath(startNode, endNode);
                        if (result == null) {
                            out.writeUTF("error: func err.");
                            out.flush();
                        } else {
                            out.writeUTF(gson.toJson(result));
                            out.flush();
                        }
                    } else {
                        out.writeUTF("error: Your nodes invalid or does not have a path.");
                    }
                }

                if (input.equals("bye")) {
                    break;
                }
            }

            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
