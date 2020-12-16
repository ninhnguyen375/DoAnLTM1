package client;

import client.CPUSchedule.App.MainPanel;
import client.dijkstra.Constant;
import client.dijkstra.MainDijkstra;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
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
    private final int keySize = 2048;
    public static int port = 1234;

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
        keyPairGenerator.initialize(keySize, new SecureRandom());
        KeyPair kp = keyPairGenerator.genKeyPair();
        clientPublicKey = kp.getPublic();
        clientPrivateKey = kp.getPrivate();

        // Send clientPublicKey to server
        out.write(clientPublicKey.getEncoded());
        out.flush();

        // Get public key from server
        byte[] publicKeyBytes = new byte[keySize];
        in.read(publicKeyBytes, 0, keySize);
        X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        // Save
        serverPublicKey = kf.generatePublic(ks);

        initLayout();
    }

    public static String socketReadLine() throws Exception {
        String result = "";

        // Lấy tổng số block cần phải nhận
        int blockCount = in.readInt();

        // Lấy tất cả các block
        for (int i = 0; i < blockCount; i++) {
            String line = in.readUTF();

            // Phiên dịch block
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, clientPrivateKey);
            byte decryptOut[] = c.doFinal(Base64.getDecoder().decode(line));

            result += new String(decryptOut);
        }

        System.out.println("socketReadline:" + result);
        return result;
    }

    public static void socketSend(String line) throws Exception {
        // Dùng serverPublicKey để mã hóa chuỗi gửi cho server
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, serverPublicKey);

        // Lấy độ dài bit của public key
        final RSAPublicKey rsapub = (RSAPublicKey) clientPublicKey;
        int keyBitLength = rsapub.getModulus().bitLength();

        byte[] lineBytes = line.getBytes();

        // Dộ dài mã hóa hợp lệ của RSA
        int validSize = keyBitLength / 8 - 11;

        // Chia gửi thành từng block theo độ dài hợp lệ
        int blockCount = (int) Math.ceil((float) lineBytes.length / validSize);
        int remaining = line.getBytes().length;

        // Gửi tổng số block cho server
        out.writeInt(blockCount);
        out.flush();

        // Gửi từng block theo kích thước hợp lệ
        ByteBuffer bb = ByteBuffer.wrap(lineBytes);
        for (int i = 0; i < blockCount; i++) {
            // lấy kích thước thích hợp nhỏ nhất
            int minLength = Math.min(validSize, remaining);
            remaining -= minLength;

            // lấy giá trị cho block
            byte[] block = new byte[minLength];
            bb.get(block, 0, minLength);

            // Mã hóa và gửi đi
            byte encryptOut[] = c.doFinal(block);
            String strEncrypt = Base64.getEncoder().encodeToString(encryptOut);

            out.writeUTF(strEncrypt);
            out.flush();
        }
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
        frame.setResizable(false);
    }

    public static void main(String args[]) throws Exception {
        // Set default ui like current OS
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        ChooseServer form = new ChooseServer();
        
        form.setLocationRelativeTo(null);
        form.pack();
        form.setVisible(true);
    }
}
