package peer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.Message;

public class PeerController extends Thread {

    private final MulticastSocket multiSocket;
    private final InetAddress groupIP;
    private final int groupPort = 7777;
    private final HeartBeat heartBeat;
    private Message message;
    private final String localIP;
    private int localPort;
    private Peer peerLocal;
    private IMember peer;

    public PeerController() throws UnknownHostException, IOException {
        
        groupIP = InetAddress.getByName("230.0.0.1");
        multiSocket = new MulticastSocket(groupPort);
        multiSocket.joinGroup(groupIP);
        
        heartBeat = new HeartBeat(this);
        localIP = InetAddress.getLocalHost().getHostAddress();
        localPort = Registry.REGISTRY_PORT;
        peerLocal = new Peer();

        boolean work = false;
        String hostURL;
        do {
            try {
                LocateRegistry.createRegistry(localPort);
                hostURL = "peer_" + String.valueOf(localPort);
                Naming.bind(hostURL, (IMember) peerLocal);
                work = true;
            } catch (AlreadyBoundException ex) {
                ex.printStackTrace();
            } catch (ExportException ex) {
                localPort++;
                System.out.println("fail");
            }
        } while (!work);
    }

    public void searchPeers(String filename) throws IOException {
        // **************************************    
        // search ArrayList of saved peers before
        // **************************************
        Message message = new Message("discoveryMsg", filename, getLocalIP(), getLocalPort());
        byte[] buf = messageToByte(message);
        DatagramPacket p = new DatagramPacket(buf, buf.length, getGroupIP(), getGroupPort());
        multiSocket.send(p);
    }

    public void startHeartBeat() {
        heartBeat.start();
    }

    public void stopHeartBeat() {
        heartBeat.kill();
    }

    public String getLocalIP() {
        return localIP;
    }

    public int getLocalPort() {
        return localPort;
    }

    public int getGroupPort() {
        return groupPort;
    }

    public InetAddress getGroupIP() {
        return groupIP;
    }

    public MulticastSocket getMulticastSocket() {
        return multiSocket;
    }

    @Override
    public void run() {
        byte[] buf = new byte[10000000];
        DatagramPacket p = new DatagramPacket(buf, buf.length);
        while (true) {
            
            try {
                System.out.println("Searching Music");
                multiSocket.receive(p);
                message = byteToMessage(p.getData());
                if (message.getTypeMsg().equals("discoveryMsg")) {
                    System.out.println("Searching Music");
                    searchMusic();
                }
            } catch (IOException ex) {
                Logger.getLogger(PeerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void searchMusic() {
        byte[] musicBytes = new byte[10000000];
        byte[] output;
        int nBytes = 0;
        try {
            // look in database
            String path = System.getProperty("user.dir") + System.getProperty("file.separator") + message.getFileName();
            FileInputStream music = new FileInputStream(path);
            nBytes = music.read(musicBytes, 0, 10000000);
            music.close();

            output = Arrays.copyOf(musicBytes, nBytes);
            peer = (IMember) Naming.lookup("peer_"+String.valueOf(message.getMemberPort()));
            peer.deliver(output, message.getFileName(), (IMember) peer);
            
        } catch (RemoteException ex) {
            Logger.getLogger(PeerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            System.out.println("!> Request not found");
        } catch (IOException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(PeerController.class.getName()).log(Level.SEVERE, null, ex);
        }

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

    public Message byteToMessage(byte[] b) {
        ObjectInputStream os = null;
        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(b);
            os = new ObjectInputStream(byteStream);
            return (Message) os.readObject();

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
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
