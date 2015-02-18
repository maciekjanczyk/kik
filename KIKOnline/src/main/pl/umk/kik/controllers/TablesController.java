package pl.umk.kik.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.kik.utils.HtmlGrabber;
import pl.umk.kik.utils.Player;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class TablesController {

    @RequestMapping("/tables")
    public String tablesPage(HttpServletRequest request, HttpServletResponse response) {
        Player player = new Player();
        if(request.getParameter("nickBox").isEmpty())
            return "Access denied.";
        player.insertNickname(request.getParameter("nickBox"));
        player.setAddress(request.getRemoteAddr());
        player.takeToken();
        for(Player p : ApplicationController.players)
            if(p.getNickname().equals(player.getNickname())) {
                String[] code = new HtmlGrabber().toString(
                        "./src/main/main.html"
                ).split("<!--ERROR_HERE-->");
                String ret = code[0];
                ret += "<font color=\"red\" size=13>This player exists!</font><br/>";
                ret += code[1];
                return ret;
            }
        ApplicationController.players.add(player);
        Cookie cookie = new Cookie("nickname", player.getNickname());

        String[] code = new HtmlGrabber().toString("./src/main/tables.html").split("ADD_PLAYERS");
        String ret = code[0];
        for(Player p : ApplicationController.players)
            ret += "<option>"+p.getNickname()+"</option>";

        ret += code[1];
        response.addCookie(cookie);
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

}
