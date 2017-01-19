import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
 
public class SocketClient {
	
 
    HashMap<String, DataOutputStream> clients;
    private ServerSocket ServerSocket = null;
 
    public static void main(String[] args) {
        new SocketClient().start();
    }
 
    public SocketClient() {
        // ����� hashmap ������(Key, value) ����
        clients = new HashMap<String, DataOutputStream>();
        // clients ����ȭ
        Collections.synchronizedMap(clients);
    }
 
    private void start() {
        
        // Port ���� ���Ǹ����� 5001�� ���� (Random������ ���氡��)
        int port = 5001;
        Socket socket = null;
 
        try {
            // �������� ������ while������ �����Ͽ� accept(���)�ϰ� ���ӽ� ip�ּҸ� ȹ���ϰ� ����ѵ�
            // MultiThread�� �����Ѵ�.
            ServerSocket = new ServerSocket(port);
            System.out.println("���Ӵ����");
            while (true) {
                socket = ServerSocket.accept();
                InetAddress ip = socket.getInetAddress();
                System.out.println(ip + "  connected");
                new MultiThread(socket).start();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
 
    class MultiThread extends Thread {
 
        Socket socket = null;
 
        String mac = null;
        String msg = null;
 
        DataInputStream input;
        DataOutputStream output;
 
        public MultiThread(Socket socket) {
            this.socket = socket;
            try {
                // ��ü�� �ְ���� Stream�����ڸ� �����Ѵ�.
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
            }
        }
 
        public void run() {
 
                // ���ӵ��� �ٷ� Mac �ּҸ� �޾ƿ� ����ϰ� clients�� ������ �Ѱ��ְ� Ŭ���̾�Ʈ���� mac�ּҸ�������.
                mac = socket.getInetAddress().getHostAddress();
                System.out.println("Ip address : " + mac);
                clients.put(mac, output);
                sendMsg(mac + "   ����");
 
                // ���Ŀ� ä�ø޼������Ž�
                while (input != null) {
                    try {
                        String temp = input.readUTF();
                        sendMsg(temp);
                        System.out.println(temp);
                    } catch (IOException e) {
                        sendMsg("No massege");
                        break;
                    }
                }
        }
 
        // �޼��������� Ŭ���̾�Ʈ���� Return �� sendMsg �޼ҵ�
        private void sendMsg(String msg) {
 
            // clients�� Key���� �޾Ƽ� String �迭�μ���
            Iterator<String> it = clients.keySet().iterator();
 
            // Return �� key���� ����������
            while (it.hasNext()) {
                try {
                    OutputStream dos = clients.get(it.next());
                    // System.out.println(msg);
                    DataOutputStream output = new DataOutputStream(dos);
                    output.writeUTF(msg);
 
                } catch (IOException e) {
                    System.out.println(e);
                }
            }	
        }
    }
}

