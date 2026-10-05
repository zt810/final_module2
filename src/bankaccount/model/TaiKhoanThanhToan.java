package bankaccount.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TaiKhoanThanhToan extends TaiKhoanNganHang {
    private final String cardNumber;
    private final BigDecimal balance;

    public TaiKhoanThanhToan(int id, String accountCode, String ownerName, LocalDate createdDate,
                             String cardNumber, BigDecimal balance) {
        super(id, accountCode, ownerName, createdDate);
        this.cardNumber = cardNumber;
        this.balance = balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public String getAccountType() {
        return "Thanh Toan";
    }

    @Override
    protected List<String> getSpecificCsvValues() {
        return List.of(cardNumber, balance.toPlainString());
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | so the: %s | so tien trong tk: %s",
                cardNumber, formatMoney(balance));
    }
}
