package pl.umk.kik.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.kik.utils.Player;
import pl.umk.kik.utils.Table;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static pl.umk.kik.controllers.ApplicationController.titlePage;
import static pl.umk.kik.utils.CookieUtils.getCookie;

@RestController
public class InvitationController {
    @RequestMapping("/getgamebutton")
    public String getGameButton(HttpServletRequest request) {
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

        String isDisabled = "";

        if (table == null)
            return "";

        return "<p>You have opponent to play!</p>"+
                "<input id=\"playRightNow\" type=\"button\" value=\"Play game\"onclick=\"return popitup2(\'/talk\');\""+isDisabled+"/>" +
                "<br/>"+
                "<input id=\"endRightNow\" type=\"button\" value=\"End game\"onclick=\"endGameRightNow();\""+isDisabled+"/>";

    }
}
