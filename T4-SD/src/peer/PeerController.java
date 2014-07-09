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
import java.util.ArrayList;
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
                Naming.bind("rmi://" + localIP + ":" + localPort + "/peer_" + localPort, peerLocal);
                work = true;

            } catch (ExportException ex) {
                localPort++;
                System.out.println("fail");
            } catch (AlreadyBoundException ex) {
                localPort++;
                System.out.println("fail");
            } catch (RemoteException ex) {
                Logger.getLogger(PeerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (!work);
    }

    public void searchPeers(String filename) throws IOException {
        Message message = new Message("discoveryMsg", filename, getLocalIP(), getLocalPort());
        byte[] buf = messageToByte(message);
        DatagramPacket p = new DatagramPacket(buf, buf.length, getGroupIP(), getGroupPort());
        multiSocket.send(p);
    }

    public void startHeartBeat() {
        heartBeat.start();
    }

    public void stopHeartBeat() {
        heartBeat.interrupt();
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
                //System.out.println("Waiting request");
                multiSocket.receive(p);// fica aguardando requisição. outra thread do peer permite a ele requisitar arquivos
                message = byteToMessage(p.getData());
                if (!message.getMemberIP().getHostAddress().equals(getLocalIP())) {

                    if (message.getTypeMsg().equals("discoveryMsg")) {
                        /*Verifica se tem no historico algum peer que contenha a musica*/
                        ArrayList<IMember> lista_members = new ArrayList<IMember>();
                        int i;
                        for (i = 0; i < peerLocal.musicList.size(); i++) {
                            if (peerLocal.musicList.get(i).equals(message.getFileName())) {
                                System.out.println("Encontrada referëncia, enviar o rmi");
                                lista_members.add(peerLocal.peerList.get(i));
                            }
                        }
                        if (lista_members.size() > 0) {
                            IMember[] list = new IMember[lista_members.size()];
                            for (i = 0; i < lista_members.size(); i++) {
                                list[i] = lista_members.get(i);
                            }

                            try {
                                peer = (IMember) Naming.lookup("rmi://"+message.getMemberIP().getHostAddress()+":"+message.getMemberPort()+"/peer_"+(message.getMemberPort()));
                                peer.deliver(message.getFileName(), list);
                            } catch (NotBoundException | MalformedURLException | RemoteException ex) {
                                Logger.getLogger(PeerController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            searchMusic();
                        }
                    }
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
            System.out.println("Searching: " + message.getFileName());
            String path = System.getProperty("user.dir") + System.getProperty("file.separator") + "music" + System.getProperty("file.separator") + message.getFileName();
            FileInputStream music = new FileInputStream(path);
            nBytes = music.read(musicBytes, 0, 10000000);
            output = Arrays.copyOf(musicBytes, nBytes);
            music.close();

            peer = (IMember) Naming.lookup("rmi://" + message.getMemberIP().getHostAddress() + ":" + message.getMemberPort() + "/peer_" + (message.getMemberPort()));
            peer.deliver(output, message.getFileName(), (IMember) peerLocal);
            heartBeat.start();
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
