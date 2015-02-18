package pl.umk.kik.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.kik.utils.Player;
import pl.umk.kik.utils.Table;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static pl.umk.kik.utils.CookieUtils.getCookie;

@RestController
public class ApiController {

    @RequestMapping("/psign")
    public String getPlayerSign(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return "ERROR";
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

        return table.getSign(player);
    }

    @RequestMapping("/isyourturn")
    public String isYourTurn(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return "ERROR";
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

        if (player.getNickname() == table.whosTurn().getNickname())
            return "Yes.";
        else
            return "No.";
    }

    @RequestMapping("/getmynickname")
    public String getMyNickname(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return "ERROR";
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

        return player.getNickname();
    }

    @RequestMapping("/getgameresult")
    public String getGameResult(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();

        if (cks == null) {
            return "ERROR";
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

        table.parseGameResult(player);
        return table.getGameResult();
    }

    @RequestMapping("/play2")
    public String playing2(HttpServletRequest request, HttpServletResponse response) {
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

    @RequestMapping("/getActivePlayers")
    public String getActivePlayers() {
        String ret="";
        for(Player p : ApplicationController.players) {
            ret+="<option";
            if (!p.hasToken())
                ret+=" disabled";
            ret+=">"+p.getNickname()+"</option>";
        }
        return ret;
    }

    @RequestMapping("/talk2")
    public String talking2(HttpServletRequest request, HttpServletResponse response) {
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
