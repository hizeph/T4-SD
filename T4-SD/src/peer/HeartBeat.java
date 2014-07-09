package peer;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import message.Message;
import java.net.UnknownHostException;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HeartBeat extends Thread {
    
    private final MulticastSocket multiSocket;
    private final Message message;
    private final PeerController controller;
    
    
    public HeartBeat(PeerController controller) throws UnknownHostException, IOException {
        this.controller = controller;
        message = new Message("heartbeatMsg", "", controller.getLocalIP(), controller.getLocalPort());
        multiSocket = controller.getMulticastSocket();  
    }

    @Override
    public void run() {
        try {
            // send heartbeat
            byte[] buf = PeerController.messageToByte(message);
            DatagramPacket p = new DatagramPacket(buf, buf.length, controller.getGroupIP(), controller.getGroupPort());
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
