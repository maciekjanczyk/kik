import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ApplicationController {

    private List<Player> players = new ArrayList<Player>();
    //private ChatServer chatServer = new ChatServer(50002);
    private List<Table> tables = new ArrayList<Table>();
    private int id = 0;

    @RequestMapping("/")
    public String titlePage(HttpServletRequest request) {
        String addr = request.getRemoteAddr();
        for(Player p : players)
            if(p.getAddress().equals(addr)) {
                String[] code = new HtmlGrabber().toString("./src/main/tables.html").split("ADD_PLAYERS");
                String ret = code[0];
                for(Player p2 : players)
                    ret += "<option>"+p2.getNickname()+"</option>";

                ret += code[1];
                return ret;
            }
        return new HtmlGrabber().toString("./src/main/main.html");
    }

    @RequestMapping(value = "/secret/{name}", method = RequestMethod.GET)
    public String secret(@PathVariable String name) {
        try {
            FileOutputStream stream = new FileOutputStream("hello.txt");
            stream.write(name.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "ok.";
    }

    @RequestMapping("/tables")
    public String tablesPage(HttpServletRequest request) {
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

        String[] code = new HtmlGrabber().toString("./src/main/tables.html").split("ADD_PLAYERS");
        String ret = code[0];
        for(Player p : players)
            ret += "<option>"+p.getNickname()+"</option>";

        ret += code[1];
        return ret;
    }

    @RequestMapping("/play")
    public String playPage(HttpServletRequest request, HttpServletResponse response) {
        String pt = request.getParameter("playerSelect");

        if(pt.equals(""))
            return "ERROR: No player selection.";

        Player p1 = null, p2 = null;

        String addr = request.getRemoteAddr();
        for(Player p : players) {
            if(p.getAddress().equals(addr))
                p1 = p;
            else if(p.getNickname().equals(pt))
                p2 = p;
            else if(p1!=null||p2!=null)
                break;
        }
        Table table = new Table(String.valueOf(id), p1, p2, response);
        /*new Runnable() {

            @Override
            public void run() {
                int a = 0;
                while(true) a++;
            }
        }.run();*/
        tables.add(table);

        return new HtmlGrabber().toString("./src/main/chat.html");
    }

    @RequestMapping("/talk")
    public String talking(HttpServletRequest request, HttpServletResponse response) {
        String addr = request.getRemoteAddr();
        Table table = null;
        Player player = null;

        for(Player p : players)
            if(p.getAddress().equals(addr)) {
                player = p;
                break;
            }

        for(Table t : tables)
            if(t.getPlayer1()==player||t.getPlayer2()==player) {
                table = t;
                break;
            }

        String[] tab = new HtmlGrabber().toString("./src/main/chat.html").split("<!--CHAT-->");
        String ret = tab[0];
        String komunikat = "["+player.getNickname()+"] ";
        komunikat += request.getParameter("message")+"\n";
        table.conv.add(komunikat);
        for(String s : table.conv)
            ret += s;
        ret += tab[1];

        return ret;
    }

    @RequestMapping("/logout")
    public String logoutAccount(HttpServletRequest request) {
        String addr = request.getRemoteAddr();
        String ret = "";

        boolean out = false;
        for(Player p : players) {
            if(p.getAddress().equals(addr)) {
                players.remove(p);
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

        return ret;
    }

}