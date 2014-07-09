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

public class Peer extends UnicastRemoteObject implements IMember, Serializable {

    public ArrayList<IMember> peerList;
    public ArrayList<String> musicList;
    private double timestamp;

    public Peer() throws RemoteException {
        peerList = new ArrayList<>();
        musicList = new ArrayList<>();
        timestamp = System.currentTimeMillis();
    }

    @Override
    public void deliver(String filename, IMember[] remotePeer)
            throws RemoteException {

        if (System.currentTimeMillis() > timestamp) {

            timestamp = System.currentTimeMillis() + 5000;

            byte[] file = new byte[10000000];
            for (IMember i : remotePeer) {
                file = i.search(filename, this);
                if (file != null) {
                    
                    //System.out.println("Adicionado no historico");
                    musicList.add(filename);
                    peerList.add(i);
                    
                    FileOutputStream music;
                    try {
                        String path = System.getProperty("user.dir") + System.getProperty("file.separator") + filename;
                        music = new FileOutputStream(path);
                        music.write(file, 0, file.length);
                        music.close();
                        System.out.println("Saved on: " + path);
                        break;
                    } catch (IOException ex) {
                        System.out.println("!> Failed to write on disk");
                    }
                }
            }
        }
    }

    @Override
    public void deliver(byte[] file, String filename, IMember remotePeer)
            throws RemoteException {
        
        musicList.add(filename);
        peerList.add(remotePeer);
        //System.out.println("Adicionado no historico");

        if (System.currentTimeMillis() > timestamp) {
            timestamp = System.currentTimeMillis() + 5000;
            
            System.out.println("Receiving " + filename);

            FileOutputStream music;
            try {
                String path = System.getProperty("user.dir") + System.getProperty("file.separator") + filename;
                music = new FileOutputStream(path);
                music.write(file, 0, file.length);
                music.close();
                System.out.println("Saved on: " + path);
            } catch (IOException ex) {
                System.out.println("! Failed to write on disk");
            }
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
            // System.out.println("!> Request not found");
            return null;
        } catch (IOException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        }

        output = Arrays.copyOf(musicBytes, nBytes);
        return output;
    }

}
