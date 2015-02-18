package pl.umk.kik.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.kik.utils.HtmlGrabber;
import pl.umk.kik.utils.Player;
import pl.umk.kik.utils.Table;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static pl.umk.kik.utils.CookieUtils.getCookie;

@RestController
public class ApplicationController {

    public static List<Player> players = new ArrayList<Player>();
    public static List<Table> tables = new ArrayList<Table>();
    public static int id = 0;

    @RequestMapping("/")
    public static String titlePage(HttpServletRequest request) {
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

}