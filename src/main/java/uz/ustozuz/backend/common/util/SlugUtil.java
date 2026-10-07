package uz.ustozuz.backend.common.util;

import java.text.Normalizer;
import java.util.Map;
import java.util.regex.Pattern;

public final class SlugUtil {

    private static final Pattern NON_ALLOWED = Pattern.compile("[^a-z0-9\\s-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s-]+");

    // Kirill yozuvidagi nomlar ham o'qiladigan havolaga aylansin
    private static final Map<Character, String> CYRILLIC = Map.ofEntries(
            Map.entry('а', "a"), Map.entry('б', "b"), Map.entry('в', "v"), Map.entry('г', "g"),
            Map.entry('д', "d"), Map.entry('е', "e"), Map.entry('ё', "yo"), Map.entry('ж', "j"),
            Map.entry('з', "z"), Map.entry('и', "i"), Map.entry('й', "y"), Map.entry('к', "k"),
            Map.entry('л', "l"), Map.entry('м', "m"), Map.entry('н', "n"), Map.entry('о', "o"),
            Map.entry('п', "p"), Map.entry('р', "r"), Map.entry('с', "s"), Map.entry('т', "t"),
            Map.entry('у', "u"), Map.entry('ф', "f"), Map.entry('х', "x"), Map.entry('ц', "ts"),
            Map.entry('ч', "ch"), Map.entry('ш', "sh"), Map.entry('щ', "sh"), Map.entry('ъ', ""),
            Map.entry('ы', "i"), Map.entry('ь', ""), Map.entry('э', "e"), Map.entry('ю', "yu"),
            Map.entry('я', "ya"), Map.entry('ў', "o"), Map.entry('қ', "q"), Map.entry('ғ', "g"),
            Map.entry('ҳ', "h")
    );

    private SlugUtil() {
    }

    public static String slugify(String input) {
        StringBuilder latin = new StringBuilder();
        for (char ch : input.toLowerCase().toCharArray()) {
            latin.append(CYRILLIC.getOrDefault(ch, String.valueOf(ch)));
        }

        String normalized = Normalizer.normalize(latin.toString(), Normalizer.Form.NFKD);
        String cleaned = NON_ALLOWED.matcher(normalized).replaceAll("");
        String slug = WHITESPACE.matcher(cleaned.trim()).replaceAll("-");
        return slug.isEmpty() ? "kurs" : slug;
    }
}
