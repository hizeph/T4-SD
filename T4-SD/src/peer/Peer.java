package peer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.Message;

public class Peer implements IMember {
    
    private Message message;
    private final PeerController controller;
    
    public Peer(PeerController controller) throws UnknownHostException, IOException {
        this.controller = controller;
    }
    
    @Override
    public void deliver(String filename, IMember[] remotePeer) 
            throws RemoteException {
        // search in remotePeer ( RMI - remotePeer.search() )
    }

    @Override
    public void deliver(byte[] file, String filename, IMember remotePeer) 
            throws RemoteException {
        
        FileOutputStream music;
        try {
            String path = System.getProperty("user.dir") + System.getProperty("file.separator") + filename;
            music = new FileOutputStream(path);
            music.write(file, 0, file.length);
            music.close();
            System.out.println("Saved on: " + path);
        } catch (IOException ex) {
            System.out.println("!> Failed to write on disk");
        }
        // controller.savePeer
    }

    @Override
    public byte[] search(String filename, IMember member) 
            throws RemoteException {
        
        // return "filename" bytes

        byte b[] = null;
        return b;
    }
    
}
