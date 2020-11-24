package server;

import server.dijsktra.*;
import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;

public class RunnableApp implements Runnable {

    private final Socket socket;
    private final Gson gson = new Gson();
    boolean isValid = true;
    public static DataInputStream in;
    public static DataOutputStream out;

    // server keys
    private static PublicKey serverPublicKey;
    private static PrivateKey serverPrivateKey;

    // client key
    public static PublicKey clientPublicKey;

    public RunnableApp(Socket socket) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.socket = socket;

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        // Gen public and private keys of current client connected
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, new SecureRandom());
        KeyPair kp = keyPairGenerator.genKeyPair();
        serverPublicKey = kp.getPublic();
        serverPrivateKey = kp.getPrivate();

        // Send serverPublicKey to client
        out.write(serverPublicKey.getEncoded());
        out.flush();

        // Get client public key
        byte[] publicKeyBytes = new byte[2048];
        in.read(publicKeyBytes, 0, 2048);
        X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        // Save
        clientPublicKey = kf.generatePublic(ks);
    }

    public String socketReadLine() throws Exception {
        String line = in.readUTF();

        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.DECRYPT_MODE, serverPrivateKey);

        byte decryptOut[] = c.doFinal(Base64.getDecoder().decode(line));

        return new String(decryptOut);
    }

    public static void socketSend(String line) throws Exception {
        // send text to client -> using clientPublicKey
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, clientPublicKey);

        byte encryptOut[] = c.doFinal(line.getBytes());
        String strEncrypt = Base64.getEncoder().encodeToString(encryptOut);

        out.writeUTF(strEncrypt);
        out.flush();
    }

    @Override
    @SuppressWarnings("ConvertToTryWithResources")
    public void run() {
        try {
            while (true) {
                String input = socketReadLine();

                // get-shortest-path-A-B
                if (input.contains("get-shortest-path")) {
                    String startNode = input.split("-")[3];
                    String endNode = input.split("-")[4];

                    String edgesString = socketReadLine();
                    Dijkstra dijkstra = new Dijkstra(edgesString);

                    if (dijkstra.validateGraph(startNode, endNode)) {
                        List<String> result = dijkstra.getShortestPath(startNode, endNode);
                        if (result == null) {
                            socketSend("error: func err.");
                        } else {
                            socketSend(gson.toJson(result));
                        }
                    } else {
                        socketSend("error: Your nodes invalid or does not have a path.");
                    }
                }

                if (input.equals("bye")) {
                    break;
                }
            }

            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
