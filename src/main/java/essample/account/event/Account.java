package essample.account.event;

import java.util.List;
import java.util.UUID;

/**
 * # Aggregate Class
 */
public class Account {

    private String id;

    private int version;

    private int snapshotVersion;

    private Long accountBalance = 0L;

    private List<AccountEvent> events;

    public Account(String id) {
        this.id = id;
    }

    public Account apply(Account account, AccountEvent event) {

        switch (event.getEventType()) {
            case CREATED:
                return account.initialState(account);
            case DEPOSITED:
                return account.deposit(event.getPayload());
            case WITHDRAWN:
                return account.withdraw(event.getPayload());
            case ALERT_MAIL_SENT:
                return account.alertMailSent();
            default:
                throw new IllegalStateException("not supported event type");
        }
    }

    private Account initialState(Account account) {
        return Builder
            .anAccount()
            .id(account.id)
            .accountBalance(0L)
            .version(1)
            .snapshotVersion(0)
            .build();
    }

    private Account deposit(Long payload) {
        this.accountBalance += payload;
        this.version += 1;
        return this;
    }

    private Account withdraw(Long payload) {
        this.accountBalance -= payload;
        this.version += 1;
        return this;
    }

    private Account alertMailSent() {
        this.version += 1;
        return this;
    }

    public Account replay(List<AccountEvent> events) {
        events.stream()
              .filter(e -> e.getVersion() > version)
              .forEach(e -> apply(this, e));

        this.events = events;
        return this;
    }

    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public int getSnapshotVersion() {
        return snapshotVersion;
    }

    public Long getAccountBalance() {
        return accountBalance;
    }

    public List<AccountEvent> getEvents() {
        return events;
    }

    public boolean needSnapShot() {
        return version > 0 && version % 5 == 0;
    }

    public AccountSnapshot toAccountSnapshot() {
        return AccountSnapshot.Builder
            .anAccountSnapshot()
            .snapshotId("snapshot-" + UUID.randomUUID().toString().substring(0, 8))
            .entityId(id)
            .version(version)
            .snapshotVersion(snapshotVersion + 1)
            .accountBalance(accountBalance)
            .build();
    }

    @Override
    public String toString() {
        return "Account{" +
            "id='" + id + '\'' +
            ", version=" + version +
            ", snapshotVersion=" + snapshotVersion +
            ", accountBalance=" + accountBalance +
            ", events=" + events +
            '}';
    }

    public boolean willBalanceUnderZero(long payload) {
        return accountBalance - payload <= 0;
    }

    public static final class Builder {
        private String id;
        private int version;
        private int snapshotVersion;
        private Long accountBalance;
        private List<AccountEvent> events;

        private Builder() {
        }

        public static Builder anAccount() {
            return new Builder();
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder version(int version) {
            this.version = version;
            return this;
        }

        public Builder snapshotVersion(int snapshotVersion) {
            this.snapshotVersion = snapshotVersion;
            return this;
        }

        public Builder accountBalance(Long accountBalance) {
            this.accountBalance = accountBalance;
            return this;
        }

        public Builder events(List<AccountEvent> events) {
            this.events = events;
            return this;
        }

        public Account build() {
            Account account = new Account(id);
            account.snapshotVersion = this.snapshotVersion;
            account.accountBalance = this.accountBalance;
            account.version = this.version;
            account.events = this.events;
            return account;
        }
    }
}
