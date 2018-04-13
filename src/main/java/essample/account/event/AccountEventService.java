package essample.account.event;

import essample.account.command.AccountCommand;
import essample.account.service.AccountSnapshotRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AccountEventService {

    private final AccountSnapshotRepository snapshotRepository;

    public AccountEventService(AccountSnapshotRepository snapshotRepository) {
        this.snapshotRepository = snapshotRepository;
    }

    public List<AccountEvent> decide(AccountCommand command, Account account) {

        if (account == null) {
            throw new IllegalStateException("account needed");
        } else if (account.needSnapShot()) {
            createSnapShot(account);
        }

        switch (command.getCommandType()) {
            case CREATE:
                return decideCreateCommand(command, account);
            case DEPOSIT:
                return decideDepositCommand(command, account);
            case WITHDRAW:
                return decideWithdrawCommand(command, account);
            default:
                throw new IllegalStateException("not supported event type");
        }
    }

    private List<AccountEvent> decideCreateCommand(AccountCommand command, Account account) {
        return Collections.singletonList(
            AccountEvent.Builder
                .anAccountEvent()
                .eventId(UUID.randomUUID().toString().substring(0, 8))
                .entityId(account.getId())
                .eventType(AccountEventType.CREATED)
                .payload(command.getPayload())
                .version(1)
                .snapshotVersion(0)
                .timestamp(new Date())
                .build());
    }

    private List<AccountEvent> decideWithdrawCommand(AccountCommand command, Account account) {

        if (!command.isValidCommand()) {
            throw new IllegalStateException("command is not valid");
        }

        List<AccountEvent> accountEvents = new ArrayList<>();
        int version = account.getVersion();

        if (account.needToMailSend(command.getPayload())) {
            version += 1;
            accountEvents.add(AccountEvent.Builder
                                  .anAccountEvent()
                                  .eventId(UUID.randomUUID().toString().substring(0, 8))
                                  .entityId(account.getId())
                                  .eventType(AccountEventType.ALERT_MAIL_SENT)
                                  .payload(0L)
                                  .version(version)
                                  .snapshotVersion(getSnapshotVersion(account))
                                  .timestamp(new Date())
                                  .build());
        }

        version += 1;
        accountEvents.add(AccountEvent.Builder
                              .anAccountEvent()
                              .eventId(UUID.randomUUID().toString().substring(0, 8))
                              .entityId(account.getId())
                              .eventType(AccountEventType.WITHDRAWN)
                              .payload(command.getPayload())
                              .version(version)
                              .snapshotVersion(getSnapshotVersion(account))
                              .timestamp(new Date())
                              .build());

        return accountEvents;
    }

    private List<AccountEvent> decideDepositCommand(AccountCommand command, Account account) {

        if (!command.isValidCommand()) {
            throw new IllegalStateException("command is not valid");
        }

        return Collections.singletonList(
            AccountEvent.Builder
                .anAccountEvent()
                .eventId(UUID.randomUUID().toString().substring(0, 8))
                .entityId(account.getId())
                .eventType(AccountEventType.DEPOSITED)
                .payload(command.getPayload())
                .version(account.getVersion() + 1)
                .snapshotVersion(getSnapshotVersion(account))
                .timestamp(new Date())
                .build());
    }

    private void createSnapShot(Account account) {
        snapshotRepository.save(account.toAccountSnapshot());
    }

    private int getSnapshotVersion(Account account) {

        int snapshotVersion = account.getSnapshotVersion();

        if (account.needSnapShot()) {
            createSnapShot(account);
            return snapshotVersion + 1;
        } else {
            return snapshotVersion;
        }
    }
}
