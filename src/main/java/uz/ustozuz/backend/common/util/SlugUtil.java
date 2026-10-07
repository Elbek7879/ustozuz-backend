package uz.ustozuz.backend.common.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class SlugUtil {

    private static final Pattern NON_ALLOWED = Pattern.compile("[^a-z0-9\\s-]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private SlugUtil() {
    }

    public static String slugify(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFKD);
        String lower = normalized.toLowerCase();
        String cleaned = NON_ALLOWED.matcher(lower).replaceAll("");
        return WHITESPACE.matcher(cleaned.trim()).replaceAll("-");
    }
}