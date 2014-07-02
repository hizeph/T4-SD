package peer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import message.Message;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HeartBeat extends Thread {
    
    private final MulticastSocket multiSocket;
    private final InetAddress groupIP;
    private final Message message;
    private final String IP;

    public HeartBeat() throws UnknownHostException, IOException {
        IP = InetAddress.getLocalHost().getHostAddress();
        this.message = new Message("heartbeatMsg", "", IP, 2222);
        groupIP = InetAddress.getByName("230.0.0.1");
        this.multiSocket = new MulticastSocket(7777);
        this.multiSocket.joinGroup(groupIP);
    }

    private byte[] messageToByte() {
        ObjectOutputStream os = null;
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            os = new ObjectOutputStream(byteStream);
            os.flush();
            os.writeObject(message);
            os.flush();
            return byteStream.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void run() {
        try {
            // send heartbeat
            byte[] buf = messageToByte();
            DatagramPacket p = new DatagramPacket(buf, buf.length);
            while (true) {
                multiSocket.send(p);
                sleep(30000);
            }
        } catch (IOException ex) {
            Logger.getLogger(HeartBeat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(HeartBeat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
