
package peer;

import java.io.IOException;
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
        try {
            PeerController peerController = new PeerController();
            peerController.startHeartBeat();
            
        } catch (IOException ex) {
            Logger.getLogger(T4SD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
