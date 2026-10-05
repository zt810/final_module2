package bankaccount;

import bankaccount.exception.NotFoundBankAccountException;
import bankaccount.model.TaiKhoanNganHang;
import java.util.List;

public class BankAccountService {
    private static final int FIRST_ID = 1;

    private final BankAccountRepository repository;

    public BankAccountService(BankAccountRepository repository) {
        this.repository = repository;
    }

    public List<TaiKhoanNganHang> findAll() {
        return repository.findAll();
    }

    public int generateNextId() {
        List<TaiKhoanNganHang> accounts = repository.findAll();
        if (accounts.isEmpty()) {
            return FIRST_ID;
        }
        return accounts.get(accounts.size() - 1).getId() + 1;
    }

    public boolean existsByAccountCode(String accountCode) {
        return repository.findAll().stream()
                .anyMatch(account -> account.hasAccountCode(accountCode));
    }

    public void add(TaiKhoanNganHang account) {
        List<TaiKhoanNganHang> accounts = repository.findAll();
        accounts.add(account);
        repository.saveAll(accounts);
    }

    public TaiKhoanNganHang findByAccountCode(String accountCode) throws NotFoundBankAccountException {
        return repository.findAll().stream()
                .filter(account -> account.hasAccountCode(accountCode))
                .findFirst()
                .orElseThrow(NotFoundBankAccountException::new);
    }

    public void deleteByAccountCode(String accountCode) throws NotFoundBankAccountException {
        List<TaiKhoanNganHang> accounts = repository.findAll();
        boolean removed = accounts.removeIf(account -> account.hasAccountCode(accountCode));
        if (!removed) {
            throw new NotFoundBankAccountException();
        }
        repository.saveAll(accounts);
    }

    public List<TaiKhoanNganHang> search(String keyword) {
        return repository.findAll().stream()
                .filter(account -> account.matchesKeyword(keyword))
                .toList();
    }
}
