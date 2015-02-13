import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ApplicationController {

    private List<Player> players = new ArrayList<Player>();
    private List<Table> tables = new ArrayList<Table>();
    private int id = 0;

    private Cookie getCookie(HttpServletRequest request) {
        Cookie[] tab = request.getCookies();

        if (tab == null) {
            return null;
        }

        Cookie ret = null;
        for (Cookie c : tab) {
            if (c.getName().equals("nickname")) {
                ret = c;
                break;
            }
        }

        return ret;
    }

    @RequestMapping("/")
    public String titlePage(HttpServletRequest request) {
        String nickname = "";
        Cookie[] cks = request.getCookies();
        if (cks == null) {
            return new HtmlGrabber().toString("./src/main/main.html");
        }
        String ret = "";
        nickname = getCookie(request).getValue();

        int count = 0;
        for (Player p : players) {
            if (!p.getNickname().equals(nickname))
                count++;
        }
        if (players.size() == count) {
            Player plapla = new Player();
            plapla.insertNickname(nickname);
            plapla.setAddress(request.getRemoteAddr());
            plapla.takeToken();
            players.add(plapla);
        }

        for(Player p : players)
            if(p.getNickname().equals(nickname)) {
                String[] code = new HtmlGrabber().toString("./src/main/tables.html").split("ADD_PLAYERS");
                ret = code[0];
                for(Player p2 : players)
                    ret += "<option>"+p2.getNickname()+"</option>";

                ret += code[1];
            }
        return ret;
    }


    @RequestMapping("/getActivePlayers")
    public String getActivePlayers() {
        String ret="";
        for(Player p : players) {
            ret+="<option";
            if (!p.hasToken())
                ret+=" disabled";
            ret+=">"+p.getNickname()+"</option>";
        }
        return ret;
    }

    @RequestMapping("/tables")
    public String tablesPage(HttpServletRequest request, HttpServletResponse response) {
        Player player = new Player();
        if(request.getParameter("nickBox").isEmpty())
            return "Access denied.";
        player.insertNickname(request.getParameter("nickBox"));
        player.setAddress(request.getRemoteAddr());
        player.takeToken();
        for(Player p : players)
            if(p.getNickname().equals(player.getNickname())) {
                String[] code = new HtmlGrabber().toString(
                        "./src/main/main.html"
                ).split("<!--ERROR_HERE-->");
                String ret = code[0];
                ret += "<font color=\"red\" size=13>This player exists!</font><br/>";
                ret += code[1];
                return ret;
            }
        players.add(player);
        Cookie cookie = new Cookie("nickname", player.getNickname());

        String[] code = new HtmlGrabber().toString("./src/main/tables.html").split("ADD_PLAYERS");
        String ret = code[0];
        for(Player p : players)
            ret += "<option>"+p.getNickname()+"</option>";

        ret += code[1];
        response.addCookie(cookie);
        return ret;
    }

    @RequestMapping("/play")
    public String playPage(HttpServletRequest request, HttpServletResponse response) {
        String pt = request.getParameter("playerSelect");

        if (pt == null)
            return "ERROR: No player selection.";

        Player p1 = null, p2 = null;

        Cookie[] cks = request.getCookies();
        if (cks == null) {
            return titlePage(request);
        }

        String nick = getCookie(request).getValue();
        for(Player p : players) {
            if (p.getNickname().equals(nick))
                p1 = p;
            else if(p.getNickname().equals(pt))
                p2 = p;
            else if(p1!=null||p2!=null)
                break;
        }

        if (!p1.hasToken()) {
            endGame(request);
        }

        if (p2 == null) {
            return "Something went wrong. Try again.";
        }

        if (!p2.hasToken()) {
            return p2.getNickname()+" is playing right now.";
        }

        Table table = new Table(String.valueOf(id), p1, p2, response);
        tables.add(table);

        return new HtmlGrabber().toString("./src/main/chat.html");
    }

    @RequestMapping("/talk")
    public String talking(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return titlePage(request);
        }

        String nick = getCookie(request).getValue();
        Table table = null;
        Player player = null;

        for(Player p : players)
            if (p.getNickname().equals(nick)) {
                player = p;
                break;
            }

        for(Table t : tables)
            if (t.getPlayer1()==player||t.getPlayer2()==player) {
                table = t;
                break;
            }

        if (table == null) {
            return "This is playing right now.";
        }

        String[] tab = new HtmlGrabber().toString("./src/main/canvas.html").split("<!--CHAT-->");
        String ret = tab[0];
        String komunikat = "["+player.getNickname()+"] ";
        String mess = request.getParameter("message");
        if(mess != null) {
            komunikat += mess+"\n";
            table.conv.add(komunikat);
        }

        for(String s : table.conv)
            ret += s;
        ret += tab[1];

        return ret;
    }

    @RequestMapping("/talk2")
    public String talking2(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return titlePage(request);
        }

        String nick = getCookie(request).getValue();
        Table table = null;
        Player player = null;

        for(Player p : players)
            if (p.getNickname().equals(nick)) {
                player = p;
                break;
            }

        for(Table t : tables)
            if(t.getPlayer1()==player||t.getPlayer2()==player) {
                table = t;
                break;
            }

        if (table == null) {
            return "";
        }

        String ret="";

        for(String s : table.conv)
            ret += s;

        return ret;
    }

    @RequestMapping("/play2")
    public String playing2(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return titlePage(request);
        }

        String nick = getCookie(request).getValue();
        Table table = null;
        Player player = null;

        for(Player p : players)
            if (p.getNickname().equals(nick)) {
                player = p;
                break;
            }

        for(Table t : tables)
            if(t.getPlayer1()==player||t.getPlayer2()==player) {
                table = t;
                break;
            }

        String ret;
        String current_canvas = request.getParameter("mataDoGry");

        if (table == null) {
            return "";
        }

        if (current_canvas != null) {
            table.setTurn(table.getSign(player),current_canvas);
            ret = table.getCanvas();
        } else {
            ret = table.getCanvas();
        }

        return ret;
    }

    @RequestMapping("/psign")
    public String getPlayerSign(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return titlePage(request);
        }

        String nick = getCookie(request).getValue();
        Table table = null;
        Player player = null;

        for(Player p : players)
            if (p.getNickname().equals(nick)) {
                player = p;
                break;
            }

        for(Table t : tables)
            if(t.getPlayer1()==player||t.getPlayer2()==player) {
                table = t;
                break;
            }

        return table.getSign(player);
    }

    @RequestMapping("/isyourturn")
    public String isYourTurn(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return titlePage(request);
        }

        String nick = getCookie(request).getValue();
        Table table = null;
        Player player = null;

        for(Player p : players)
            if (p.getNickname().equals(nick)) {
                player = p;
                break;
            }

        for(Table t : tables)
            if(t.getPlayer1()==player||t.getPlayer2()==player) {
                table = t;
                break;
            }

        if (table == null) {
            return "";
        }

        if (player.getNickname() == table.whosTurn().getNickname())
            return "Yes.";
        else
            return "No.";
    }

    @RequestMapping("/getmynickname")
    public String getMyNickname(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return titlePage(request);
        }

        String nick = getCookie(request).getValue();
        Table table = null;
        Player player = null;

        for(Player p : players)
            if (p.getNickname().equals(nick)) {
                player = p;
                break;
            }

        for(Table t : tables)
            if(t.getPlayer1()==player||t.getPlayer2()==player) {
                table = t;
                break;
            }

        return player.getNickname();
    }

    @RequestMapping("/getgameresult")
    public String getGameResult(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return titlePage(request);
        }

        String nick = getCookie(request).getValue();
        Table table = null;
        Player player = null;

        for(Player p : players)
            if (p.getNickname().equals(nick)) {
                player = p;
                break;
            }

        for(Table t : tables)
            if(t.getPlayer1()==player||t.getPlayer2()==player) {
                table = t;
                break;
            }

        if (table == null) {
            return "";
        }

        table.parseGameResult(player);
        return table.getGameResult();
    }

    @RequestMapping("/logout")
    public String logoutAccount(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return titlePage(request);
        }

        String ret = "";
        String nick = getCookie(request).getValue();

        Cookie cookie = new Cookie("nickname","");
        cookie.setMaxAge(1);

        boolean out = false;
        for(Player p : players) {
            if (p.getNickname().equals(nick)) {
                players.remove(p);
                Table currTbl = p.getCurrentTable();
                if (currTbl != null) {
                    currTbl.getPlayer1().takeToken();
                    currTbl.getPlayer2().takeToken();
                    tables.remove(currTbl);
                }
                out = true;
                break;
            }
        }

        if(!out) {
            return "Access denied.";
        }

        String[] code = new HtmlGrabber().toString(
                "./src/main/main.html"
        ).split("<!--ERROR_HERE-->");
        ret = code[0];
        ret += "<font color=\"green\" size=12>Logout succesful.</font><br/>";
        ret += code[1];

        response.addCookie(cookie);

        return ret;
    }

    @RequestMapping("/clearcookie")
    public String clearCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("nickname","");
        cookie.setMaxAge(1);
        response.addCookie(cookie);
        return "Ok.";
    }

    @RequestMapping("/endgame")
    public String endGame(HttpServletRequest request) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return titlePage(request);
        }

        String nick = getCookie(request).getValue();
        Table table = null;
        Player player = null;

        for(Player p : players)
            if (p.getNickname().equals(nick)) {
                player = p;
                break;
            }

        for(Table t : tables)
            if (t.getPlayer1()==player||t.getPlayer2()==player) {
                table = t;
                break;
            }

        if (table == null)
            return "Nothing.";

        Player p2;
        if (table.getPlayer1() == player)
            p2 = table.getPlayer2();
        else
            p2 = table.getPlayer1();

        player.takeToken();
        p2.takeToken();
        tables.remove(table);

        return "Removed.";
    }

    @RequestMapping("/getgamebutton")
    public String getGameButton(HttpServletRequest request) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return titlePage(request);
        }

        String nick = getCookie(request).getValue();
        Table table = null;
        Player player = null;

        for(Player p : players)
            if (p.getNickname().equals(nick)) {
                player = p;
                break;
            }

        for(Table t : tables)
            if(t.getPlayer1()==player||t.getPlayer2()==player) {
                table = t;
                break;
            }

        String isDisabled = "";

        if (table == null)
            return "";

        return "<p>You have opponent to play!</p>"+
                "<input id=\"playRightNow\" type=\"button\" value=\"Play game\"onclick=\"return popitup2(\'/talk\');\""+isDisabled+"/>" +
                "<br/>"+
                "<input id=\"endRightNow\" type=\"button\" value=\"End game\"onclick=\"endGameRightNow();\""+isDisabled+"/>";

    }

}