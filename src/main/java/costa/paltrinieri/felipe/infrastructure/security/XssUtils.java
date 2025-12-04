package costa.paltrinieri.felipe.infrastructure.security;

import org.springframework.web.util.HtmlUtils;

public final class XssUtils {

    private XssUtils() {
    }

    public static String sanitize(String input) {
        return input != null ? HtmlUtils.htmlEscape(input) : null;
    }

}
