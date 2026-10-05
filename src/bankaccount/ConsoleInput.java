package bankaccount;
import bankaccount.exception.InvalidInputException;
import java.util.Scanner;
import java.util.function.Function;

public class ConsoleInput {
    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public <T> T read(String prompt, Function<String, T> parser) {
        while (true) {
            try {
                return parser.apply(readLine(prompt));
            } catch (InvalidInputException e) {
                System.out.println("loi: " + e.getMessage());
            }
        }
    }

    public int readChoice(String prompt, int min, int max) {
        return read(prompt, value -> InputValidator.requireChoice(value, min, max));
    }

    public boolean confirm(String prompt) {
        return read(prompt, InputValidator::requireYesOrNo);
    }
}
