package bankaccount;

import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    private static final Path DATA_FILE = Path.of("data", "bank_accounts.csv");

    public static void main(String[] args) {
        BankAccountService bankAccountService = new BankAccountService(new BankAccountRepository(DATA_FILE));
        try (Scanner scanner = new Scanner(System.in)) {
            new BankAccountController(bankAccountService, new ConsoleInput(scanner)).run();
        }
    }
}
