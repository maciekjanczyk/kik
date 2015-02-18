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
public class PlayController {

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
        for(Player p : ApplicationController.players) {
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

        Table table = new Table(String.valueOf(ApplicationController.id), p1, p2, response);
        ApplicationController.tables.add(table);

        return new HtmlGrabber().toString("./src/main/chat.html");
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

    @RequestMapping("/endgame")
    public static String endGame(HttpServletRequest request) {
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

        if (table == null)
            return "Nothing.";

        Player p2;
        if (table.getPlayer1() == player)
            p2 = table.getPlayer2();
        else
            p2 = table.getPlayer1();

        player.takeToken();
        p2.takeToken();
        ApplicationController.tables.remove(table);

        return "Removed.";
    }

}
