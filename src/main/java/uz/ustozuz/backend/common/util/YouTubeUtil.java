package uz.ustozuz.backend.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uz.ustozuz.backend.common.exception.BadRequestException;

// Ustoz kiritgan YouTube havolasini tekshiradi va bir xil ko'rinishga keltiradi
public final class YouTubeUtil {

    // watch?v=ID, youtu.be/ID, /embed/ID, /shorts/ID, /live/ID — ID har doim 11 belgi
    private static final Pattern VIDEO_ID = Pattern.compile(
            "^(?:https?://)?(?:www\\.|m\\.)?(?:youtube\\.com/(?:watch\\?(?:.*&)?v=|embed/|shorts/|live/)|youtu\\.be/)([A-Za-z0-9_-]{11})(?:[?&#/].*)?$");

    private YouTubeUtil() {
    }

    // Bo'sh qiymat -> null (video yo'q). Noto'g'ri havola -> 400 xato.
    public static String normalize(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        Matcher m = VIDEO_ID.matcher(url.trim());
        if (!m.matches()) {
            throw new BadRequestException("Faqat YouTube video havolasini kiriting (masalan, https://youtu.be/...)");
        }
        return "https://www.youtube.com/watch?v=" + m.group(1);
    }
}
