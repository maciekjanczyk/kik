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
public class ChatController {

    @RequestMapping("/talk")
    public String talking(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
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
            return "busyplayer";
        }

        String komunikat = "["+player.getNickname()+"] ";
        String mess = request.getParameter("message");
        if(mess != null) {
            komunikat += mess+"\n";
            table.conv.add(komunikat);
        }

        model.addAttribute("messages",table.conv);
        return "canvas";
    }

}
