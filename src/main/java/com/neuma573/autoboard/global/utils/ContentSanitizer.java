package com.neuma573.autoboard.global.utils;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentSanitizer {

    public static String filterHtmlSource(String source) {
        Safelist safelist = Safelist.basicWithImages();

        // CKEditor specific tags and attributes
        safelist.addTags("h2", "h3", "h4", "br", "strong", "i", "figure", "div", "iframe", "table", "tbody", "tr", "td", "ul", "ol", "li", "span", "oembed");
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

        safelist.addTags("iframe");
        safelist.addAttributes("iframe", "src", "width", "height", "frameborder", "allow", "allowfullscreen", "style");

        safelist.addTags("oembed");
        safelist.addAttributes("oembed", "url");

        return ContentSanitizer.convertOembedToIframe(Jsoup.clean(source, safelist));
    }

    public static String removeHtmlTags(String source) {
        Safelist safelist = Safelist.none();
        return Jsoup.clean(source, safelist);
    }

    public static String convertOembedToIframe(String source) {
        Document doc = Jsoup.parse(source);
        Elements oembedElements = doc.select("oembed[url]");

        oembedElements.stream()
                .filter(oembed -> oembed.attr("url").contains("youtu"))
                .forEach(oembed -> {
                    String url = oembed.attr("url");
                    String videoId = extractYouTubeVideoId(url);
                    if (videoId != null) {
                        String iframeUrl = "https://www.youtube.com/embed/" + videoId;
                        Element iframe = new Element("iframe")
                                .attr("src", iframeUrl)
                                .attr("width", "560")
                                .attr("height", "315")
                                .attr("frameborder", "0")
                                .attr("allow", "autoplay; encrypted-media")
                                .attr("allowfullscreen", true);
                        oembed.replaceWith(iframe);
                    }
                });

        return doc.body().html();
    }

    private static String extractYouTubeVideoId(String url) {
        String pattern = "(?:https?://)?(?:www\\.)?(?:youtube\\.com/watch\\?v=|youtu\\.be/|youtube\\.com/embed/)([\\w-]{11})(?:\\S+)?";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
