package peer;

import java.rmi.RemoteException;
import message.Message;

public class Peer implements IMember {
    private Message message;

    @Override
    public void deliver(String filename, IMember[] remotePeer) 
            throws RemoteException {

    }

    @Override
    public void deliver(byte[] file, String filename, IMember remotePeer) 
            throws RemoteException {

    }

    @Override
    public byte[] search(String filename, IMember member) 
            throws RemoteException {
        
        byte b[] = null;
        return b;
    }
    
}
