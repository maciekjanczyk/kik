package pl.umk.kik.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.umk.kik.utils.Player;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class TablesController {

    @RequestMapping("/tables")
    public String tablesPage(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
        Player player = new Player();
        if(request.getParameter("nickBox").isEmpty()) {
            model.addAttribute("mess","Access denied.");
            return "mess";
        }
        player.insertNickname(request.getParameter("nickBox"));
        player.setAddress(request.getRemoteAddr());
        player.takeToken();
        for(Player p : ApplicationController.players)
            if(p.getNickname().equals(player.getNickname())) {
                model.addAttribute("err_mess","<font color=\"red\" size=13>This player exists!</font><br/>");
                return "main";
            }

        ApplicationController.players.add(player);
        Cookie cookie = new Cookie("nickname", player.getNickname());
        model.addAttribute("players",ApplicationController.players);
        response.addCookie(cookie);

        return "tables";
    }

}
