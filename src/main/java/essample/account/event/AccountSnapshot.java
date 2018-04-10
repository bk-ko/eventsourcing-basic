package essample.account.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AccountSnapshot {

    @Id
    @Column(name = "snapshot_id")
    private String snapshotId;

    @Column(name = "entity_id")
    private String entityId;

    private int version;

    @Column(name = "snapshot_version")
    private int snapshotVersion;

    @Column(name = "account_balance")
    private Long accountBalance = 0L;

    public AccountSnapshot() {
    }

    public String getSnapshotId() {
        return snapshotId;
    }

    public String getEntityId() {
        return entityId;
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

    public Account toAccount() {
        return Account
            .Builder
            .anAccount()
            .id(entityId)
            .version(version)
            .snapshotVersion(snapshotVersion)
            .accountBalance(accountBalance)
            .build();
    }

    public static final class Builder {
        private String snapshotId;
        private String entityId;
        private int version;
        private int snapshotVersion;
        private Long accountBalance = 0L;

        private Builder() {
        }

        public static Builder anAccountSnapshot() {
            return new Builder();
        }

        public Builder snapshotId(String snapshotId) {
            this.snapshotId = snapshotId;
            return this;
        }

        public Builder entityId(String entityId) {
            this.entityId = entityId;
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

        public AccountSnapshot build() {
            AccountSnapshot accountSnapshot = new AccountSnapshot();
            accountSnapshot.accountBalance = this.accountBalance;
            accountSnapshot.snapshotId = this.snapshotId;
            accountSnapshot.snapshotVersion = this.snapshotVersion;
            accountSnapshot.version = this.version;
            accountSnapshot.entityId = this.entityId;
            return accountSnapshot;
        }
    }
}
