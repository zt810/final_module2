package bankaccount;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public final class DateUtils {
    public static final String DATE_PATTERN = "dd/MM/yyyy";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private DateUtils() {
    }

    public static LocalDate parse(String text) {
        return LocalDate.parse(text, FORMATTER);
    }

    public static String format(LocalDate date) {
        return date.format(FORMATTER);
    }
}
