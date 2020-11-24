package client;

import client.CPUSchedule.App.MainPanel;
import client.dijkstra.Constant;
import client.dijkstra.MainDijkstra;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

public class Client {

    public static Socket socket = null;
    public static DataOutputStream out;
    public static DataInputStream in;
    BufferedReader stdIn = null;

    public Client(String address, int port) throws Exception {
        // Set default ui like current OS
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        socket = new Socket(address, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        System.out.println("Connected");

        // Dijstra main panel
        MainDijkstra dijkstraPanel = new MainDijkstra();
        MainPanel fCFSPanel = new MainPanel();

        // JTabbedPane
        JTabbedPane tabs = new JTabbedPane();
        tabs.add(dijkstraPanel);
        tabs.setTitleAt(0, "Dijkstra");
        tabs.add(fCFSPanel);
        tabs.setTitleAt(1, "CPU schedule");

        // Show on frame
        JFrame frame = new JFrame();
        frame.add(tabs);

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
