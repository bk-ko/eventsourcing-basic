package essample.account.service;

import essample.account.command.AccountCommand;
import essample.account.command.CommandType;
import essample.account.event.Account;
import essample.account.event.AccountEvent;
import essample.account.event.AccountEventService;
import essample.account.event.AccountEventType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AccountCommandServiceTest {

    @Mock
    private AccountQueryService queryService;

    @Mock
    private AccountEventService eventService;

    @Mock
    private AccountEventRepository eventRepository;

    @Autowired
    private AccountCommandService commandService;

    private Account testAccount = new Account("account-test");

    @Before
    public void setUp() {

        when(queryService.getAccount("account-test"))
            .thenReturn(testAccount);
    }

    @Test
    public void createAccount() {

        AccountCommand command = getAccountCommand(CommandType.CREATE, 0);
        AccountEvent e = getAccountEvent(AccountEventType.CREATED, 0);

        when(eventService.decide(command, testAccount))
            .thenReturn(Collections.singletonList(e));

        List<AccountEvent> events = commandService.createAccount();
        assertThat(events.get(0).getEventType()).isEqualTo(AccountEventType.CREATED);

    }

    @Test
    public void depositAccount() {
        AccountCommand command = getAccountCommand(CommandType.DEPOSIT, 100);
        AccountEvent e = getAccountEvent(AccountEventType.DEPOSITED, 100);

        when(eventService.decide(command, testAccount))
            .thenReturn(Collections.singletonList(e));

        List<AccountEvent> events = commandService.createAccount();
        assertThat(events.get(0).getEventType()).isEqualTo(AccountEventType.DEPOSITED);

    }

    @Test
    public void withdrawAccount() {
        AccountCommand command = getAccountCommand(CommandType.WITHDRAW, 100);
        AccountEvent e = getAccountEvent(AccountEventType.WITHDRAWN, 100);

        when(eventService.decide(command, testAccount))
            .thenReturn(Collections.singletonList(e));

        List<AccountEvent> events = commandService.createAccount();
        assertThat(events.get(0).getEventType()).isEqualTo(AccountEventType.WITHDRAWN);

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

    private AccountCommand getAccountCommand(CommandType type, long amount) {
        return new AccountCommand(type, amount);
    }
}