package essample.account.service;

import essample.account.event.Account;
import essample.account.event.AccountEvent;
import essample.account.event.AccountSnapshot;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountQueryService {

    private AccountEventRepository repository;
    private AccountSnapshotRepository snapshotRepository;

    public AccountQueryService(AccountEventRepository repository, AccountSnapshotRepository snapshotRepository) {
        this.repository = repository;
        this.snapshotRepository = snapshotRepository;
    }

    public Account getAccount(String accountId) {

        Account account = recreateAccountFromSnapshot(accountId);

        List<AccountEvent> accountEvents =
            repository.findAllByEntityId(accountId);

        return account.replay(accountEvents);
    }

    private Account recreateAccountFromSnapshot(String accountId) {
        AccountSnapshot accountSnapshot
            = snapshotRepository.findFirstByEntityIdOrderBySnapshotVersionDesc(accountId);

        return (accountSnapshot == null)
            ? new Account(accountId)
            : accountSnapshot.toAccount();
    }
}
