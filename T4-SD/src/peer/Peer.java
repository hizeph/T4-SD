package peer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.Message;

public class Peer extends UnicastRemoteObject implements IMember, Serializable {

    private Message message;
    private final String hostURL = "peer";
    private ArrayList<IMember> peerList;
    private ArrayList<String> musicList;
    public Peer() throws RemoteException {
        
    }

    @Override
    public void deliver(String filename, IMember[] remotePeer)
            throws RemoteException {

        byte[] file = remotePeer[0].search(filename, this);

        FileOutputStream music;
        try {
            String path = System.getProperty("user.dir") + System.getProperty("file.separator") +  filename;
            music = new FileOutputStream(path);
            music.write(file, 0, file.length);
            music.close();
            System.out.println("Saved on: " + path);
        } catch (IOException ex) {
            System.out.println("!> Failed to write on disk");
        }
    }

    @Override
    public void deliver(byte[] file, String filename, IMember remotePeer)
            throws RemoteException {
        peerList.add(remotePeer);
        musicList.add(filename);
        System.out.println("Add nova musica "+filename+" e novo peer");
        
        System.out.println("Delivering "+filename);
        
        
        
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
    }

    @Override
    public byte[] search(String filename, IMember member)
            throws RemoteException {

        // return "filename" bytes
        byte[] musicBytes = new byte[10000000];
        byte[] output;
        int nBytes = 0;
        try {
            // look in database
            String path = System.getProperty("user.dir") + System.getProperty("file.separator") + filename;
            FileInputStream music = new FileInputStream(path);
            nBytes = music.read(musicBytes, 0, 10000000);
            music.close();
        } catch (FileNotFoundException ex) {
            System.out.println("!> Request not found");
        } catch (IOException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        }

        output = Arrays.copyOf(musicBytes, nBytes);
        return output;
    }

}
