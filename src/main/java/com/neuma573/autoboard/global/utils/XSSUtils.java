package com.neuma573.autoboard.global.utils;


import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class XSSUtils {

    public static String filterHtmlSource(String source) {
        Safelist safelist = Safelist.basicWithImages();

        safelist.addTags("figure");
        safelist.addAttributes("figure", "class");

        safelist.addAttributes("img", "style");

        safelist.addTags("iframe");
        safelist.addAttributes("iframe", "src", "width", "height", "frameborder", "allow", "allowfullscreen");

        safelist.addAttributes("blockquote", "cite");

        return Jsoup.clean(source, safelist);
    }

    public static String removeHtmlTags(String source) {
        Safelist safelist = Safelist.none();


        return Jsoup.clean(source, safelist);
    }
}
