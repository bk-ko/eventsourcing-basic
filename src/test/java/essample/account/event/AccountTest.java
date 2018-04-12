package essample.account.event;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountTest {

    private final long AMOUNTS_500 = 500;
    private final long AMOUNTS_1000 = 1000;
    private Account account;
    private int version;

    @Before
    public void setup() {
        Random r = new Random();
        version = r.nextInt();
        account = Account.Builder.anAccount()
                                 .id("account-test")
                                 .accountBalance(0L)
                                 .version(version)
                                 .snapshotVersion(1)
                                 .build();
    }

    @After
    public void tearDown() {
        account = null;
    }

    @Test
    public void apply_created_event() {

        Account account = new Account("account-test");
        AccountEvent createdEvent = generate_createdEvent();

        account.apply(account, createdEvent);

        assertThat(account.getVersion()).isEqualTo(1);
        assertThat(account.getAccountBalance()).isEqualTo(0);
    }

    @Test
    public void apply_depositedEvent() {
        AccountEvent depositedEvent = generate_depositedEvent(version + 1, AMOUNTS_500);
        account.apply(account, depositedEvent);

        assertThat(account.getAccountBalance())
            .isEqualTo(AMOUNTS_500);
    }

    @Test
    public void apply_withdrawnEvent() {
        AccountEvent withdrawnEvent = generate_withdrawnEvent(version + 1, AMOUNTS_1000);
        Account apply = account.apply(account, withdrawnEvent);

        assertThat(apply.getVersion()).isEqualTo(version + 1);
        assertThat(account.getAccountBalance())
            .isEqualTo(-1 * AMOUNTS_1000);
    }

    @Test
    public void replay() {
        // event is immutable
        List<AccountEvent> events = new ArrayList<>();
        events.add(generate_depositedEvent(version + 1, AMOUNTS_500));
        events.add(generate_depositedEvent(version + 2, AMOUNTS_500));
        events.add(generate_withdrawnEvent(version + 3, AMOUNTS_1000));

        account.replay(events);

        assertThat(account.getVersion()).isEqualTo(version + 3);
        assertThat(account.getAccountBalance()).isEqualTo(0);
    }

    private AccountEvent generate_createdEvent() {
        return AccountEvent.Builder
            .anAccountEvent()
            .eventId(UUID.randomUUID().toString().substring(0, 8))
            .entityId(account.getId())
            .eventType(AccountEventType.CREATED)
            .payload(0L)
            .version(0)
            .snapshotVersion(0)
            .timestamp(new Date())
            .build();
    }

    private AccountEvent generate_depositedEvent(int version, long amounts) {
        return AccountEvent.Builder
            .anAccountEvent()
            .eventId(UUID.randomUUID().toString().substring(0, 8))
            .entityId(account.getId())
            .eventType(AccountEventType.DEPOSITED)
            .payload(amounts)
            .version(version)
            .timestamp(new Date())
            .build();
    }

    private AccountEvent generate_withdrawnEvent(int version, long amounts) {
        return AccountEvent.Builder
            .anAccountEvent()
            .eventId(UUID.randomUUID().toString().substring(0, 8))
            .entityId(account.getId())
            .eventType(AccountEventType.WITHDRAWN)
            .payload(amounts)
            .version(version)
            .snapshotVersion(0)
            .timestamp(new Date())
            .build();
    }
}