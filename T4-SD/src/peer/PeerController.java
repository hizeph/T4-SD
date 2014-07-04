
package peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


public class PeerController {
    private final MulticastSocket multiSocket;
    private final InetAddress groupIP;
    private final HeartBeat heartBeat;
    
    public PeerController() throws UnknownHostException, IOException{
        groupIP = InetAddress.getByName("230.0.0.1");
        multiSocket = new MulticastSocket(7777);
        multiSocket.joinGroup(groupIP);
        heartBeat = new HeartBeat();
    }
    
    public void startHeartBeat(){
        heartBeat.start();
    }
}
