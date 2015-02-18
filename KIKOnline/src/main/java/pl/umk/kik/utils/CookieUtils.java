package pl.umk.kik.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtils {
    public static Cookie getCookie(HttpServletRequest request) {
        Cookie[] tab = request.getCookies();

        if (tab == null) {
            return null;
        }

        Cookie ret = null;
        for (Cookie c : tab) {
            if (c.getName().equals("nickname")) {
                ret = c;
                break;
            }
        }

        return ret;
    }
}
