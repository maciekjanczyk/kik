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
    private String canvas = "---------";
    private String p1sign = "O";
    private String p2sign = "X";
    private Player turn;
    private int p1points = 0;
    private int p2points = 0;

    public Table(String id, Player p1, Player p2, HttpServletResponse response) {
        this.p1 = p1;
        this.p2 = p2;
        this.id = id;
        this.lastResponse = response;
        this.turn = p1;
        conv.add("");
        convSize++;
    }

    public void setResponse(HttpServletResponse response) {
        this.lastResponse = response;
    }

    public Player getPlayer1() { return p1; }
    public Player getPlayer2() { return p2; }

    public String getCanvas() {
        return canvas;
    }

    public String getSign(Player p) {
        if (p == p1) {
            return p1sign;
        }
        else {
            return p2sign;
        }
    }

    public Player whosTurn() {
        return turn;
    }

    public void setWhosTurn(Player p) {
        turn = p;
    }

    public void setTurn(String sign, String canv) {
        String num = canv.split("s")[1];
        int i = Integer.decode(num);
        char[] tmp = canvas.toCharArray();
        tmp[i-1] = sign.toCharArray()[0];
        canvas = new String(tmp);
        if (turn == p1)
            turn = p2;
        else
            turn = p1;
    }

    public String getGameResult() {
        return p1.getNickname()+" "+p1points+" - "+p2points+" "+p2.getNickname();
    }

    public void parseGameResult(Player player) {
        char[] canv = canvas.toCharArray();
        char sign = getSign(player).toCharArray()[0];
        if(parseGameResultStatement(canv, sign)) {
            if(player == p1) {
                p1points++;
            } else {
                p2points++;
            }
            canvas = "---------";
        } else {
            int ile = 0;
            for(char c : canv) {
                if(c == '-') ile++;
            }
            if (ile == 0) {
                p1points++;
                p2points++;
                canvas = "---------";
            }
        }
    }

    private Boolean parseGameResultStatement(char[] tab, char s) {
        Boolean statement =
        (
            tab[0] == s &&
            tab[1] == s &&
            tab[2] == s
        ) ||
        (
            tab[3] == s &&
            tab[4] == s &&
            tab[5] == s
        ) ||
        (
            tab[6] == s &&
            tab[7] == s &&
            tab[8] == s
        ) ||
        (
            tab[0] == s &&
            tab[3] == s &&
            tab[6] == s
        ) ||
        (
            tab[1] == s &&
            tab[4] == s &&
            tab[7] == s
        ) ||
        (
            tab[2] == s &&
            tab[5] == s &&
            tab[8] == s
        ) ||
        (
            tab[0] == s &&
            tab[4] == s &&
            tab[8] == s
        ) ||
        (
            tab[2] == s &&
            tab[4] == s &&
            tab[6] == s
        );

        return statement;
    }

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
