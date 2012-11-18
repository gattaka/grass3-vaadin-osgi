package org.myftp.gattserver.grass3.articles.parser.misc;

/**
 *
 * http://www.rgagnon.com/javadetails/java-0306.html
 *
 */
public class HTMLEscaper {

    public static String stringToHTMLString(String string) {

        StringBuilder sb = new StringBuilder(string.length());
        int len = string.length();
        char c;

        for (int i = 0; i < len; i++) {
            c = string.charAt(i);

            // HTML Special Chars
            if (c == '"') {
                sb.append("&quot;");
            } else if (c == '&') {
                sb.append("&amp;");
            } else if (c == '<') {
                sb.append("&lt;");
            } else if (c == '>') {
                sb.append("&gt;");
            } else {
                int ci = 0xffff & c;
                if (ci < 160) // nothing special only 7 Bit
                {
                    sb.append(c);
                } else {
                    // Not 7 Bit use the unicode system
                    sb.append("&#");
                    sb.append(new Integer(ci).toString());
                    sb.append(';');
                }
            }
        }

        return sb.toString();
    }
}
