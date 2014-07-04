package peer;

public interface IMember extends java.rmi.Remote {

    public void deliver(String filename, IMember[] remotePeer) throws
            java.rmi.RemoteException;

    public void deliver(byte[] file, String filename, IMember remotePeer) throws
            java.rmi.RemoteException;

    public byte[] search(String filename, IMember member) throws
            java.rmi.RemoteException;
}
