package com.neuma573.autoboard.global.utils;


import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class ContentSanitizer {

    public static String filterHtmlSource(String source) {
        Safelist safelist = Safelist.basicWithImages();

        // CKEditor specific tags and attributes
        safelist.addTags("h2", "h3", "h4", "br", "strong", "i", "figure", "div", "iframe", "table", "tbody", "tr", "td", "ul", "ol", "li", "span");
        safelist.addAttributes("figure", "class", "contenteditable");
        safelist.addAttributes("div", "class", "contenteditable", "role", "aria-label", "contenteditable", "dir", "lang");
        safelist.addAttributes("iframe", "src", "width", "height", "frameborder", "allow", "allowfullscreen", "style");
        safelist.addAttributes("td", "class", "role", "contenteditable");
        safelist.addAttributes("span", "class", "data-cke-filler");

        // Allow data-cke-filler attribute for <p> and <span> tags
        safelist.addAttributes("p", "data-cke-filler");
        safelist.addAttributes("span", "data-cke-filler");

        // Allow specific attributes on img tags
        safelist.addAttributes("img", "style");

        safelist.addAttributes("table", "class");

        safelist.addAttributes("blockquote", "cite");

        safelist.addAttributes("div", "class", "title", "aria-hidden", "style");
        safelist.addAttributes("svg", "xmlns", "viewBox", "width", "height", "fill", "version", "id", "xml:space", "xmlns:xlink");
        safelist.addAttributes("path", "d");

        safelist.addTags("a");
        safelist.addAttributes("a", "href", "target", "rel");

        return Jsoup.clean(source, safelist);
    }

    public static String removeHtmlTags(String source) {
        Safelist safelist = Safelist.none();
        return Jsoup.clean(source, safelist);
    }
}
