package pl.umk.kik.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.umk.kik.utils.Player;
import pl.umk.kik.utils.Table;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static pl.umk.kik.utils.CookieUtils.getCookie;

@Controller
public class PlayController {

    @RequestMapping("/play")
    public String playPage(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
        String pt = request.getParameter("playerSelect");

        if (pt == null) {
            model.addAttribute("mess","ERROR: No player selection.");
            return "mess";
        }

        Player p1 = null, p2 = null;

        Cookie[] cks = request.getCookies();
        if (cks == null) {
            return "main";
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
            endGame(model,request);
        }

        if (p2 == null) {
            return "blad";
        }

        if (!p2.hasToken()) {
            return "busyplayer";
        }

        Table table = new Table(String.valueOf(ApplicationController.id), p1, p2, response);
        ApplicationController.tables.add(table);

        return "chat";
    }



    @RequestMapping("/endgame")
    public static String endGame(ModelMap model, HttpServletRequest request) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return "main";
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
            model.addAttribute("mess","Not removed (NULL error).");
            return "mess";
        }

        Player p2;
        if (table.getPlayer1() == player)
            p2 = table.getPlayer2();
        else
            p2 = table.getPlayer1();

        player.takeToken();
        p2.takeToken();
        ApplicationController.tables.remove(table);

        model.addAttribute("mess","Removed.");
        return "mess";
    }

}
