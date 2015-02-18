package pl.umk.kik.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.umk.kik.utils.Player;
import pl.umk.kik.utils.Table;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static pl.umk.kik.utils.CookieUtils.getCookie;

@Controller
public class ApplicationController {

    public static List<Player> players = new ArrayList<Player>();
    public static List<Table> tables = new ArrayList<Table>();
    public static int id = 0;

    @RequestMapping("/")
    public static String titlePage(ModelMap model, HttpServletRequest request) {
        String nickname = "";
        Cookie[] cks = request.getCookies();
        if (cks == null) {
            return "main";
        }

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

        model.addAttribute("players",players);
        return "tables";
    }

    @RequestMapping("/logout")
    public String logoutAccount(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return "main";
        }

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
            model.addAttribute("mess","Access denied.");
            return "mess";
        }

        model.addAttribute("err_mess","<font color=\"green\" size=12>Logout succesful.</font><br/>");
        response.addCookie(cookie);

        return "main";
    }

    @RequestMapping("/clearcookie")
    public String clearCookie(ModelMap model, HttpServletResponse response) {
        Cookie cookie = new Cookie("nickname","");
        cookie.setMaxAge(1);
        response.addCookie(cookie);
        model.addAttribute("mess","Ok.");
        return "mess";
    }

}