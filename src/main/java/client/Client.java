package client;

import dijkstra.Constant;
import dijkstra.MainDijkstra;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class Client {

    private Socket socket = null;
    BufferedWriter out = null;
    BufferedReader in = null;
    BufferedReader stdIn = null;

    public Client(String address, int port) throws Exception {
        // Set default ui like current OS
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        socket = new Socket(address, port);
        System.out.println("Connected");

        // Dijstra main panel
        MainDijkstra panel = new MainDijkstra(socket);

        // Show on frame
        JFrame frame = new JFrame();
        frame.add(panel);

        frame.setSize(Constant.mainWidth, Constant.mainHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String args[]) throws Exception {
        Client client = new Client("127.0.0.1", 1234);
    }
}
