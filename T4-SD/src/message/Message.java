
package message;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Message implements Serializable {
    private InetAddress IP;
    private int port;
    private String typeMsg, fileName;
    
    public Message (String typeMsg, String fileName, String IP, int port) throws UnknownHostException{
        this.typeMsg = typeMsg;
        this.fileName = fileName;
        this.IP = InetAddress.getByName(IP);
        this.port = port;
    }
    
    public String getTypeMsg(){
        return typeMsg;
    }
    
    public void SetTypeMsg(String typeMsg){
        this.typeMsg = typeMsg;
    }
    
    public String getFileName(){
        return fileName;
    }
    
    public void setFileName(String fileName){
        this.fileName = fileName;
    }
    
    public InetAddress getMemberIP(){
        return IP;
    }
    
    public void setMemberIP(String IP) throws UnknownHostException{
        this.IP = InetAddress.getByName(IP);
    }
    
    public int getMemberPort(){
        return port;
    }
    
    public void setMemberPort(int port){
        this.port = port;
    }
}
