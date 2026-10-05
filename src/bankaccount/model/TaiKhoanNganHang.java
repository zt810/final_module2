package bankaccount.model;
import bankaccount.DateUtils;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class TaiKhoanNganHang {
    public static final String DISPLAY_FORMAT = "%-4s | %-10s | %-12s | %-22s | %-10s";

    private final int id;
    private final String accountCode;
    private final String ownerName;
    private final LocalDate createdDate;

    protected TaiKhoanNganHang(int id, String accountCode, String ownerName, LocalDate createdDate) {
        this.id = id;
        this.accountCode = accountCode;
        this.ownerName = ownerName;
        this.createdDate = createdDate;
    }

    public abstract String getAccountType();
    protected abstract List<String> getSpecificCsvValues();

    public int getId() {
        return id;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public boolean hasAccountCode(String code) {
        return accountCode.equalsIgnoreCase(code);
    }

    public boolean matchesKeyword(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);
        return accountCode.toLowerCase(Locale.ROOT).contains(lowerCaseKeyword)
                || ownerName.toLowerCase(Locale.ROOT).contains(lowerCaseKeyword);
    }

    public List<String> toCsvValues() {
        List<String> values = new ArrayList<>(List.of(
                String.valueOf(id), accountCode, ownerName, DateUtils.format(createdDate)));
        values.addAll(getSpecificCsvValues());
        return values;
    }

    @Override
    public String toString() {
        return String.format(DISPLAY_FORMAT, id, getAccountType(), accountCode, ownerName,
                DateUtils.format(createdDate));
    }

    protected static String formatMoney(BigDecimal amount) {
        return new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US)).format(amount) + " VND";
    }
}
