package essample.account.service;

import essample.account.event.Account;
import essample.account.event.AccountEvent;
import essample.account.event.AccountEventType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountQueryServiceTest {

    @Autowired
    AccountQueryService accountQueryService;
    private Account account;
    private int version;
    @Mock
    private AccountEventRepository repository;

    @Before
    public void setup() {
        account = Account.Builder.anAccount()
                                 .id("account-test")
                                 .accountBalance(0L)
                                 .version(version)
                                 .snapshotVersion(0)
                                 .events(new ArrayList<>())
                                 .build();

        List<AccountEvent> events = Collections.singletonList(getAccountEvent(AccountEventType.CREATED, 0));

        when(repository.findAllByEntityId("account-test"))
            .thenReturn(events);
    }

    @Test
    public void getAccount() {
        version = 1;
        Account queried = accountQueryService.getAccount("account-test");
        assertThat(queried.getId()).isEqualTo(account.getId());
        assertThat(queried.getVersion()).isEqualTo(account.getVersion());

    }

    private AccountEvent getAccountEvent(AccountEventType eventType, long amount) {
        return AccountEvent
            .Builder
            .anAccountEvent()
            .eventType(eventType)
            .entityId("account-test")
            .payload(amount)
            .build();
    }
}