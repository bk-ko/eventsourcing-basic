package essample.account;

import essample.account.event.Account;
import essample.account.event.AccountEvent;
import essample.account.service.AccountCommandService;
import essample.account.service.AccountQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventSourcingIntegrationTest {

    @Autowired
    AccountCommandService commandService;

    @Autowired
    AccountQueryService queryService;

    @Test
    public void integration_test() {

        AccountEvent createdEvent =
            commandService.createAccount();

        String entityId = createdEvent.getEntityId();

        commandService.depositAccount(entityId, 500L);
        commandService.depositAccount(entityId, 500L);
        commandService.depositAccount(entityId, 500L);
        commandService.depositAccount(entityId, 500L);
        commandService.depositAccount(entityId, 500L);

        // maybe create snapshot
        commandService.withdrawAccount(entityId, 1000L);
        commandService.withdrawAccount(entityId, 1000L);

        // maybe alert mail sent event (when balance under zero)
        commandService.withdrawAccount(entityId, 1000L);

        Account account = queryService.getAccount(entityId);

        // CREATED(1), DEPOSITED(5), WITHDRAWN(3), ALERT_MAIL_SENT(1)
        assertThat(account.getVersion()).isEqualTo(9);

        assertThat(account.getAccountBalance()).isEqualTo(-500);

        // Create 1 snapshot per 5events
        assertThat(account.getSnapshotVersion()).isEqualTo(1);
    }


}
