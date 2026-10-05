package bankaccount.model;
import bankaccount.DateUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TaiKhoanTietKiem extends TaiKhoanNganHang {
    private final BigDecimal depositAmount;
    private final LocalDate depositDate;
    private final BigDecimal interestRate;
    private final int termInMonths;

    public TaiKhoanTietKiem(int id, String accountCode, String ownerName, LocalDate createdDate,
                            BigDecimal depositAmount, LocalDate depositDate,
                            BigDecimal interestRate, int termInMonths) {
        super(id, accountCode, ownerName, createdDate);
        this.depositAmount = depositAmount;
        this.depositDate = depositDate;
        this.interestRate = interestRate;
        this.termInMonths = termInMonths;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public LocalDate getDepositDate() {
        return depositDate;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public int getTermInMonths() {
        return termInMonths;
    }

    @Override
    public String getAccountType() {
        return "Tiet Kiem";
    }

    @Override
    protected List<String> getSpecificCsvValues() {
        return List.of(depositAmount.toPlainString(), DateUtils.format(depositDate),
                interestRate.toPlainString(), String.valueOf(termInMonths));
    }

    @Override
    public String toString() {
        return super.toString() + String.format(
                " | so tien gui: %s | ngay gui: %s | lai suat: %s%%/year | ki han: %d thang",
                formatMoney(depositAmount), DateUtils.format(depositDate),
                interestRate.stripTrailingZeros().toPlainString(), termInMonths);
    }
}
