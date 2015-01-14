import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by macias on 13.12.14.
 */
public class Table implements Runnable {

    private Player p1 = null;
    private Player p2 = null;
    private String id = "";
    public List<String> conv = new ArrayList<String>();
    private int convSize = 0;
    public boolean isRunning = true;
    private HttpServletResponse lastResponse = null;

    public Table(String id, Player p1, Player p2, HttpServletResponse response) {
        this.p1 = p1;
        this.p2 = p2;
        this.id = id;
        this.lastResponse = response;
        conv.add("");
        convSize++;
    }

    public void setResponse(HttpServletResponse response) {
        this.lastResponse = response;
    }

    public Player getPlayer1() { return p1; }
    public Player getPlayer2() { return p2; }

    @Override
    public void run() {
        while(isRunning) {
            if(conv.size()>convSize) {
                convSize++;
                try {
                    lastResponse.sendRedirect("/talk");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
