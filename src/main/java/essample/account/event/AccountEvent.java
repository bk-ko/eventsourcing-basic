package essample.account.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * # Event Class
 */
@Entity
public class AccountEvent {

    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "event_type")
    private AccountEventType eventType;

    private int version;

    @Column(name = "snapshot_version")
    private int snapshotVersion;

    // not yet json
    private Long payload;

    private Date timestamp;

    public AccountEvent() {
    }

    public String getEventId() {
        return eventId;
    }

    public String getEntityId() {
        return entityId;
    }

    public AccountEventType getEventType() {
        return eventType;
    }

    public int getVersion() {
        return version;
    }

    public int getSnapshotVersion() {
        return snapshotVersion;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Long getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "AccountEvent{" +
            "eventId='" + eventId + '\'' +
            ", entityId='" + entityId + '\'' +
            ", eventType=" + eventType +
            ", version=" + version +
            ", snapshotVersion=" + snapshotVersion +
            ", payload=" + payload +
            ", timestamp=" + timestamp +
            '}';
    }

    public static final class Builder {
        private String eventId;
        private String entityId;
        private AccountEventType eventType;
        private int version;
        private int snapshotVersion;
        private Long payload;
        private Date timestamp;

        private Builder() {
        }

        public static Builder anAccountEvent() {
            return new Builder();
        }

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder entityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder eventType(AccountEventType eventType) {
            this.eventType = eventType;
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

        public Builder payload(Long payload) {
            this.payload = payload;
            return this;
        }

        public Builder timestamp(Date timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public AccountEvent build() {
            AccountEvent accountEvent = new AccountEvent();
            accountEvent.entityId = this.entityId;
            accountEvent.eventType = this.eventType;
            accountEvent.version = this.version;
            accountEvent.snapshotVersion = this.snapshotVersion;
            accountEvent.eventId = this.eventId;
            accountEvent.payload = this.payload;
            accountEvent.timestamp = this.timestamp;
            return accountEvent;
        }
    }
}
