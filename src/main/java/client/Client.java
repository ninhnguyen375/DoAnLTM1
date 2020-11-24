package client;

import client.CPUSchedule.App.MainPanel;
import client.dijkstra.Constant;
import client.dijkstra.MainDijkstra;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

public class Client {

    public static Socket socket = null;
    public static DataOutputStream out;
    public static DataInputStream in;
    BufferedReader stdIn = null;

    // client keys
    public static PublicKey clientPublicKey;
    public static PrivateKey clientPrivateKey;

    // server key
    public static PublicKey serverPublicKey;

    public Client(String address, int port) throws Exception {

        socket = new Socket(address, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        // Gen public and private keys
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, new SecureRandom());
        KeyPair kp = keyPairGenerator.genKeyPair();
        clientPublicKey = kp.getPublic();
        clientPrivateKey = kp.getPrivate();

        // Send clientPublicKey to server
        out.write(clientPublicKey.getEncoded());
        out.flush();

        // Get public key from server
        byte[] publicKeyBytes = new byte[2048];
        in.read(publicKeyBytes, 0, 2048);
        X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        // Save
        serverPublicKey = kf.generatePublic(ks);

        initLayout();
    }

    public static String socketReadLine() throws Exception {
        String line = in.readUTF();

        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.DECRYPT_MODE, clientPrivateKey);

        byte decryptOut[] = c.doFinal(Base64.getDecoder().decode(line));

        return new String(decryptOut);
    }

    public static void socketSend(String line) throws Exception {
        // send text to server -> using serverPublicKey
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, serverPublicKey);

        byte encryptOut[] = c.doFinal(line.getBytes());
        String strEncrypt = Base64.getEncoder().encodeToString(encryptOut);

        out.writeUTF(strEncrypt);
        out.flush();
    }

    private void initLayout() throws Exception {
        // Set default ui like current OS
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

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
