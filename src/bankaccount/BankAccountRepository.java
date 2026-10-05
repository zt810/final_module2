package bankaccount;
import bankaccount.exception.DataAccessException;
import bankaccount.model.TaiKhoanNganHang;
import bankaccount.model.TaiKhoanThanhToan;
import bankaccount.model.TaiKhoanTietKiem;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BankAccountRepository {
    private static final String CSV_SEPARATOR = ",";
    private static final String UTF8_BOM = "﻿";
    private static final int SAVINGS_COLUMN_COUNT = 8;
    private static final int PAYMENT_COLUMN_COUNT = 6;

    private final Path filePath;

    public BankAccountRepository(Path filePath) {
        this.filePath = filePath;
    }

    public List<TaiKhoanNganHang> findAll() {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }
        List<String> lines = readLines();
        List<TaiKhoanNganHang> accounts = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.isBlank()) {
                accounts.add(parseLine(line, i + 1));
            }
        }
        return accounts;
    }

    public void saveAll(List<TaiKhoanNganHang> accounts) {
        List<String> lines = accounts.stream()
                .map(account -> String.join(CSV_SEPARATOR, account.toCsvValues()))
                .toList();
        try {
            Files.createDirectories(filePath.toAbsolutePath().getParent());
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataAccessException("Khong the ghi file " + filePath, e);
        }
    }

    private List<String> readLines() {
        try {
            return Files.readString(filePath, StandardCharsets.UTF_8).replace(UTF8_BOM, "").lines().toList();
        } catch (IOException e) {
            throw new DataAccessException("Khong the doc file " + filePath, e);
        }
    }

    private TaiKhoanNganHang parseLine(String line, int lineNumber) {
        String[] values = Arrays.stream(line.split(CSV_SEPARATOR, -1))
                .map(String::trim)
                .toArray(String[]::new);
        try {
            return toAccount(values);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new DataAccessException(
                    "dong " + lineNumber + " trong file " + filePath + " khong hop le: " + line, e);
        }
    }

    private TaiKhoanNganHang toAccount(String[] values) {
        if (values.length != SAVINGS_COLUMN_COUNT && values.length != PAYMENT_COLUMN_COUNT) {
            throw new IllegalArgumentException("so cot khong hop le: " + values.length);
        }
        int id = Integer.parseInt(values[0]);
        String accountCode = values[1];
        String ownerName = values[2];
        LocalDate createdDate = DateUtils.parse(values[3]);
        if (values.length == SAVINGS_COLUMN_COUNT) {
            return new TaiKhoanTietKiem(id, accountCode, ownerName, createdDate,
                    new BigDecimal(values[4]), DateUtils.parse(values[5]),
                    new BigDecimal(values[6]), Integer.parseInt(values[7]));
        }
        return new TaiKhoanThanhToan(id, accountCode, ownerName, createdDate,
                values[4], new BigDecimal(values[5]));
    }
}
