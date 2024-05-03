package com.neuma573.autoboard.global.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentSanitizerTest {

    @Test
    public void testFilterHtmlSource() {
        String htmlSource = "<img src=x onerror=alert(1) style=\"width:0;height:0\">";

        String expected = "<img style=\"width:0;height:0\">";

        String actual = ContentSanitizer.filterHtmlSource(htmlSource);

        assertEquals(expected, actual, "Filtered HTML should not contain any script tags or styles.");
    }

    @Test
    public void testFilterHtmlSourceWithBlank() {
        String htmlSource = "<p></p>";

        String expected = "";

        String actual = ContentSanitizer.removeHtmlTags(htmlSource);

        assertEquals(expected, actual, "Filtered HTML is not blank");
    }
}
