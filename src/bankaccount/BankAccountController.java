package bankaccount;
import bankaccount.exception.DataAccessException;
import bankaccount.exception.InvalidInputException;
import bankaccount.exception.NotFoundBankAccountException;
import bankaccount.model.TaiKhoanNganHang;
import bankaccount.model.TaiKhoanThanhToan;
import bankaccount.model.TaiKhoanTietKiem;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BankAccountController {
    private static final int ADD_ACCOUNT = 1;
    private static final int DELETE_ACCOUNT = 2;
    private static final int DISPLAY_ACCOUNTS = 3;
    private static final int SEARCH_ACCOUNTS = 4;
    private static final int EXIT = 5;
    private static final int SAVINGS_ACCOUNT = 1;
    private static final int PAYMENT_ACCOUNT = 2;

    private static final String MAIN_MENU = """
            QUAN LY TAI KHOAN NGAN HANG
            Chon mot chuc nang theo so (de tiep tuc):
            1. Them moi
            2. Xoa
            3. Xem danh danh sach cac tai khoan ngan hang
            4. Tim kiem
            5. Thoat""";

    private static final String ACCOUNT_TYPE_MENU = """
            Loai taikhoan:
            1. tai khoan tiet kiem
            2. Tai khoan thanh toan""";

    private static final String TABLE_HEADER = String.format(TaiKhoanNganHang.DISPLAY_FORMAT,
            "Id", "Loai TK", "ma tai khoan", "chu tai khoan", "ngay tao") + " | thong tin chi tiet";

    private final BankAccountService bankAccountService;
    private final ConsoleInput input;

    public BankAccountController(BankAccountService bankAccountService, ConsoleInput input) {
        this.bankAccountService = bankAccountService;
        this.input = input;
    }

    public void run() {
        int choice;
        do {
            System.out.println();
            System.out.println(MAIN_MENU);
            choice = input.readChoice("Chon chuc nang: ", ADD_ACCOUNT, EXIT);
            execute(choice);
        } while (choice != EXIT);
    }

    private void execute(int choice) {
        try {
            switch (choice) {
                case ADD_ACCOUNT -> addAccount();
                case DELETE_ACCOUNT -> deleteAccount();
                case DISPLAY_ACCOUNTS -> displayAllAccounts();
                case SEARCH_ACCOUNTS -> searchAccounts();
                case EXIT -> System.out.println("Exit chuong trinh");
            }
        } catch (DataAccessException e) {
            System.out.println("Loi database: " + e.getMessage());
        }
    }

    private void addAccount() {
        System.out.println("THEM MOI TAI KHOAN NGAN HANG");
        System.out.println(ACCOUNT_TYPE_MENU);
        int accountType = input.readChoice("Chon loai tai khoan: ", SAVINGS_ACCOUNT, PAYMENT_ACCOUNT);

        String accountCode = readNewAccountCode();
        String ownerName = input.read("Ten chu tai khoan: ",
                value -> InputValidator.requireText(value, "Ten chu tai khoan"));
        LocalDate createdDate = input.read("ngay tao tai khoan (" + DateUtils.DATE_PATTERN + "): ",
                value -> InputValidator.requirePastOrPresentDate(value, "ngay tao tai khoan"));

        int id = bankAccountService.generateNextId();
        TaiKhoanNganHang account = accountType == SAVINGS_ACCOUNT
                ? readSavingsAccount(id, accountCode, ownerName, createdDate)
                : readPaymentAccount(id, accountCode, ownerName, createdDate);

        bankAccountService.add(account);
        System.out.println("Them tai khoan moi thanh cong");
    }

    private String readNewAccountCode() {
        return input.read("ma tai khoan: ", value -> {
            String accountCode = InputValidator.requireText(value, "ma tai khoan");
            if (bankAccountService.existsByAccountCode(accountCode)) {
                throw new InvalidInputException("Ma tai khoan " + accountCode + " da ton tai.");
            }
            return accountCode;
        });
    }

    private TaiKhoanTietKiem readSavingsAccount(int id, String accountCode, String ownerName,
                                                LocalDate createdDate) {
        BigDecimal depositAmount = input.read("So tien gui tiet kiem (vndd): ",
                value -> InputValidator.requirePositiveNumber(value, "So tien gui tiet kiem"));
        LocalDate depositDate = readDepositDate(createdDate);
        BigDecimal interestRate = input.read("Lai suat (%/nam): ",
                value -> InputValidator.requirePositiveNumber(value, "Lai suat"));
        int termInMonths = input.read("Ky han: ",
                value -> InputValidator.requirePositiveInteger(value, "Ky han"));
        return new TaiKhoanTietKiem(id, accountCode, ownerName, createdDate,
                depositAmount, depositDate, interestRate, termInMonths);
    }

    private LocalDate readDepositDate(LocalDate createdDate) {
        return input.read("Ngay gui tiet kiem (" + DateUtils.DATE_PATTERN + "): ", value -> {
            LocalDate depositDate = InputValidator.requirePastOrPresentDate(value, "Ngay gui tiet kiem");
            if (depositDate.isBefore(createdDate)) {
                throw new InvalidInputException("Khong the gui tiet kiem truoc khi tai khoan duoc tao ra ("
                        + DateUtils.format(createdDate) + ").");
            }
            return depositDate;
        });
    }

    private TaiKhoanThanhToan readPaymentAccount(int id, String accountCode, String ownerName,
                                                 LocalDate createdDate) {
        String cardNumber = input.read("so the: ",
                value -> InputValidator.requirePositiveDigits(value, "so the"));
        BigDecimal balance = input.read("So tien trong tai khoan (vnd): ",
                value -> InputValidator.requirePositiveNumber(value, "So tien trong tai khoan"));
        return new TaiKhoanThanhToan(id, accountCode, ownerName, createdDate, cardNumber, balance);
    }

    private void deleteAccount() {
        System.out.println("XOA TAI KHOAN NGAN HANG");
        while (true) {
            String accountCode = input.readLine("Nhap ma tai khoan can xoa (nhan enter de quay ve man hinh chinh): ");
            if (accountCode.isEmpty()) {
                return;
            }
            try {
                confirmAndDelete(bankAccountService.findByAccountCode(accountCode));
                return;
            } catch (NotFoundBankAccountException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void confirmAndDelete(TaiKhoanNganHang account) throws NotFoundBankAccountException {
        printAccounts(List.of(account));
        if (!input.confirm("Co chac chan muon xoa tai khoan ngan hang nay? (1-dong y/0-khong dong y): ")) {
            System.out.println("Da huy thao tac xoa the");
            return;
        }
        bankAccountService.deleteByAccountCode(account.getAccountCode());
        System.out.println("Xoa thanh cong tai khoan " + account.getAccountCode());
        displayAllAccounts();
    }

    private void displayAllAccounts() {
        System.out.println("DANH SACH CAC TAI KHOAN NGAN HANG HIEN CO");
        List<TaiKhoanNganHang> accounts = bankAccountService.findAll();
        if (accounts.isEmpty()) {
            System.out.println("Danh sach khong co tai khoan ngan hang nao");
            return;
        }
        printAccounts(accounts);
        System.out.println("Tong so: " + accounts.size() + " tai khoan.");
    }

    private void searchAccounts() {
        System.out.println("TIM KIEM TAI KHOAN NGAN HANG");
        String keyword = input.read("nhap ma tai khoan hoac ten chu can tim: ",
                value -> InputValidator.requireNotBlank(value, "tu khoa tim kiem"));
        List<TaiKhoanNganHang> results = bankAccountService.search(keyword);
        if (results.isEmpty()) {
            System.out.println("khong tim thay tai khoan nao khop voi tu khoa \"" + keyword + "\".");
            return;
        }
        System.out.println("Da tim thay " + results.size() + " tai khoan ngan hang phu hop voi tu khoa \"" + keyword + "\":");
        printAccounts(results);
    }

    private void printAccounts(List<TaiKhoanNganHang> accounts) {
        List<String> rows = accounts.stream().map(TaiKhoanNganHang::toString).toList();
        int tableWidth = rows.stream().mapToInt(String::length).max().orElse(0);
        String separator = "-".repeat(Math.max(tableWidth, TABLE_HEADER.length()));
        System.out.println(separator);
        System.out.println(TABLE_HEADER);
        System.out.println(separator);
        rows.forEach(System.out::println);
        System.out.println(separator);
    }
}
