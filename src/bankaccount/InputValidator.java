package bankaccount;

import bankaccount.exception.InvalidInputException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public final class InputValidator {
    private static final Pattern DECIMAL_NUMBER = Pattern.compile("\\d+(\\.\\d+)?");
    private static final Pattern POSITIVE_DIGITS = Pattern.compile("0*[1-9]\\d*");
    private static final Set<String> YES_ANSWERS = Set.of("yes", "1");
    private static final Set<String> NO_ANSWERS = Set.of("no", "0");

    private InputValidator() {
    }

    public static String requireNotBlank(String value, String fieldName) {
        if (value.isBlank()) {
            throw new InvalidInputException(fieldName + "khong duoc de trong");
        }
        return value.trim();
    }

    public static String requireText(String value, String fieldName) {
        String text = requireNotBlank(value, fieldName);
        if (text.contains(",")) {
            throw new InvalidInputException(fieldName + "khong duoc chua dau phay");
        }
        return text;
    }

    public static BigDecimal requirePositiveNumber(String value, String fieldName) {
        return parseDecimal(requireNotBlank(value, fieldName))
                .filter(number -> number.signum() > 0)
                .orElseThrow(() -> new InvalidInputException(fieldName + " phai la so lon hon 0"));
    }

    public static int requirePositiveInteger(String value, String fieldName) {
        return parseInteger(requireNotBlank(value, fieldName))
                .filter(number -> number > 0)
                .orElseThrow(() -> new InvalidInputException(fieldName + " phai la so nguyen duong"));
    }

    public static String requirePositiveDigits(String value, String fieldName) {
        String digits = requireNotBlank(value, fieldName);
        if (!POSITIVE_DIGITS.matcher(digits).matches()) {
            throw new InvalidInputException(fieldName + " phai la so duong, cac chu so tu 0 den 9");
        }
        return digits;
    }


    public static LocalDate requirePastOrPresentDate(String value, String fieldName) {
        LocalDate date = parseDate(requireNotBlank(value, fieldName))
                .orElseThrow(() -> new InvalidInputException(
                        fieldName + " phai la ngay co dinh dang " + DateUtils.DATE_PATTERN));
        if (date.isAfter(LocalDate.now())) {
            throw new InvalidInputException(fieldName + " khong co event hien tai");
        }
        return date;
    }

    public static int requireChoice(String value, int min, int max) {
        return parseInteger(value.trim())
                .filter(choice -> choice >= min && choice <= max)
                .orElseThrow(() -> new InvalidInputException(
                        "Lua chon khong hop le, nhap cac so tu " + min + " den " + max));
    }

    public static boolean requireYesOrNo(String value) {
        String answer = value.trim().toLowerCase(Locale.ROOT);
        if (YES_ANSWERS.contains(answer)) {
            return true;
        }
        if (NO_ANSWERS.contains(answer)) {
            return false;
        }
        throw new InvalidInputException("nhap 1 hoac 2 tuong duong voi dong y hoac khong dong y");
    }

    private static Optional<BigDecimal> parseDecimal(String text) {
        return DECIMAL_NUMBER.matcher(text).matches() ? Optional.of(new BigDecimal(text)) : Optional.empty();
    }

    private static Optional<Integer> parseInteger(String text) {
        try {
            return Optional.of(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static Optional<LocalDate> parseDate(String text) {
        try {
            return Optional.of(DateUtils.parse(text));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }
}
