
package peer;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class T4SD {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String keyboard = "";
        
        try {
            PeerController peerController = new PeerController();
            peerController.start();
            
            while (true) {
                System.out.println("Digitar:");
                keyboard = scan.nextLine();
                if (!keyboard.equals("exit")){
                    keyboard+=".mp3";
                   peerController.searchPeers(keyboard);
                } else {
                    System.out.println("> Client closing");
                    peerController.stopHeartBeat();
                    peerController.interrupt();
                    System.exit(1);
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(T4SD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
