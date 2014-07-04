
package peer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import message.Message;


public class PeerController {
    
    private final MulticastSocket multiSocket;
    private final InetAddress groupIP;
    private final int groupPort = 7777;
    private final HeartBeat heartBeat;
    private ArrayList<Peer> peerList;
    private final String localIP;
    private final int localPort;
    
    public PeerController() throws UnknownHostException, IOException{
        groupIP = InetAddress.getByName("230.0.0.1");
        multiSocket = new MulticastSocket(groupPort);
        multiSocket.joinGroup(groupIP);
        heartBeat = new HeartBeat(this);
        peerList = new ArrayList<>();
        localIP = InetAddress.getLocalHost().getHostAddress();
        localPort = 2222;
    }
    
    public void searchPeers (String filename) throws IOException {
        // **************************************    
        // search ArrayList of saved peers before
        // **************************************
        Message message = new Message("discoveryMsg", filename, getLocalIP(), getLocalPort());
        byte[] buf = messageToByte(message);
        DatagramPacket p = new DatagramPacket(buf, buf.length, getGroupIP(), getGroupPort());
        multiSocket.send(p);
    }
    
    public void startHeartBeat(){
        heartBeat.start();
    }
    
    public void stopHeartBeat(){
        heartBeat.kill();
    }
    
    public String getLocalIP(){
        return localIP;
    }
    
    public int getLocalPort(){
        return localPort;
    }
    
    public int getGroupPort(){
        return groupPort;
    }
    
    public InetAddress getGroupIP(){
        return groupIP;
    }
    
    public MulticastSocket getMulticastSocket(){
        return multiSocket;
    }
    
    public static byte[] messageToByte(Message message) {
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
    
}
