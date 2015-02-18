package pl.umk.kik.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.kik.utils.HtmlGrabber;
import pl.umk.kik.utils.Player;
import pl.umk.kik.utils.Table;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static pl.umk.kik.controllers.ApplicationController.titlePage;
import static pl.umk.kik.utils.CookieUtils.getCookie;

@RestController
public class ChatController {

    @RequestMapping("/talk")
    public String talking(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return titlePage(request);
        }

        String nick = getCookie(request).getValue();
        Table table = null;
        Player player = null;

        for(Player p : ApplicationController.players)
            if (p.getNickname().equals(nick)) {
                player = p;
                break;
            }

        for(Table t : ApplicationController.tables)
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

        for(Player p : ApplicationController.players)
            if (p.getNickname().equals(nick)) {
                player = p;
                break;
            }

        for(Table t : ApplicationController.tables)
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

}
