
package peer;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hizeph
 */
public class T4SD {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String keyboard = "";
        
        try {
            PeerController peerController = new PeerController();
            peerController.start();
            
            while (true) {
                System.out.println("Nome da musica:");
                keyboard = scan.nextLine();
                keyboard+=".mp3";
                if (!keyboard.equals("exit")){
                   peerController.searchPeers(keyboard);
                } else {
                    System.out.println("> Client closing");
                    System.exit(1);
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(T4SD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
