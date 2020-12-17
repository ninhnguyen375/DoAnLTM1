package server;

import server.CPUSchedule.ExecuteCPUAlgorythm;
import server.dijsktra.*;
import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
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
    private final int keySize = 2048;

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
        keyPairGenerator.initialize(keySize, new SecureRandom());
        KeyPair kp = keyPairGenerator.genKeyPair();
        serverPublicKey = kp.getPublic();
        serverPrivateKey = kp.getPrivate();

        // Send serverPublicKey to client
        out.write(serverPublicKey.getEncoded());
        out.flush();

        // Get client public key
        byte[] publicKeyBytes = new byte[keySize];
        in.read(publicKeyBytes, 0, keySize);
        X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        // Save
        clientPublicKey = kf.generatePublic(ks);
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
            c.init(Cipher.DECRYPT_MODE, serverPrivateKey);
            byte decryptOut[] = c.doFinal(Base64.getDecoder().decode(line));

            result += new String(decryptOut);
        }

        System.out.println("socketReadline:" + result);
        return result;
    }

    public static void socketSend(String line) throws Exception {
        // Dùng clientPublicKey để mã hóa chuỗi gửi cho client
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, clientPublicKey);

        // Lấy độ dài bit của public key
        final RSAPublicKey rsapub = (RSAPublicKey) clientPublicKey;
        int keyBitLength = rsapub.getModulus().bitLength();

        byte[] lineBytes = line.getBytes();

        // Dộ dài mã hóa hợp lệ của RSA
        int validSize = keyBitLength / 8 - 11;

        // Chia gửi thành từng block theo độ dài hợp lệ
        int blockCount = (int) Math.ceil((float) lineBytes.length / validSize);
        int remaining = line.getBytes().length;

        // Gửi tổng số block cho client
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

    @Override
    @SuppressWarnings("ConvertToTryWithResources")
    public void run() {
        System.out.println("New Client Connected");
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
                } else if (input.contains("get-algorythm")) { // get-algorythm-name_of_algorythm
                    // tìm tên thuật toán
                    String algorithm = input.split("-")[2];
                    System.out.println(algorithm);
                    // để cho hàm xử lý của server xử lý
                    ExecuteCPUAlgorythm executeCPUAlgorythm = new ExecuteCPUAlgorythm(socketReadLine(), algorithm);
                    // .execute sẽ trả về CPUSChedule. Client sẽ nhận và xử lý thành dạng dataset để hiện thị chart
                    // trả kết quả về dưới client dãy này để render ra graph
                    socketSend(gson.toJson(executeCPUAlgorythm.execute()));
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
