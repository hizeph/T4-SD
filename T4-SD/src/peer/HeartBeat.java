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
    private final int groupPort = 7777;
    private final Message message;
    private final String IP;
    private boolean stop;

    public HeartBeat() throws UnknownHostException, IOException {
        stop = false;
        IP = InetAddress.getLocalHost().getHostAddress();
        message = new Message("heartbeatMsg", "", IP, 2222);
        groupIP = InetAddress.getByName("230.0.0.1");
        multiSocket = new MulticastSocket(groupPort);
        multiSocket.joinGroup(groupIP);
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
    
    public void kill(){
        stop = true;
    }

    @Override
    public void run() {
        try {
            // send heartbeat
            byte[] buf = messageToByte();
            DatagramPacket p = new DatagramPacket(buf, buf.length, groupIP, groupPort);
            while (!stop) {
                multiSocket.send(p);
                System.out.println("sent");
                sleep(30000);
            }
            this.interrupt();
            
        } catch (IOException ex) {
            Logger.getLogger(HeartBeat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(HeartBeat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
