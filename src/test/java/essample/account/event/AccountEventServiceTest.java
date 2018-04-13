package essample.account.event;

import essample.account.command.AccountCommand;
import essample.account.command.CommandType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AccountEventServiceTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Autowired
    AccountEventService accountEventService;

    @Test
    public void decide_CreateCommand() {
        AccountCommand command = new AccountCommand(CommandType.CREATE, 0);
        Account account = new Account("account-test");
        List<AccountEvent> events = accountEventService.decide(command, account);

        assertThat(events.get(0).getEventType()).isEqualTo(AccountEventType.CREATED);
    }

    @Test
    public void decide_DepositCommand() {
        AccountCommand command = new AccountCommand(CommandType.DEPOSIT, 50);
        Account account = new Account("account-test");
        List<AccountEvent> events = accountEventService.decide(command, account);

        assertThat(events.get(0).getEventType()).isEqualTo(AccountEventType.DEPOSITED);
    }

    @Test
    public void decide_Invalid_DepositCommand() {
        AccountCommand command = new AccountCommand(CommandType.DEPOSIT, 0);
        Account account = new Account("account-test");

        exception.expect(IllegalStateException.class);
        accountEventService.decide(command, account);
    }

    @Test
    public void decide_WithdrawCommand() {
        AccountCommand command = new AccountCommand(CommandType.WITHDRAW, 50);
        Account account = Account.Builder.anAccount()
                                         .id("account-test")
                                         .accountBalance(100).build();
        List<AccountEvent> events = accountEventService.decide(command, account);
        assertThat(events.get(0).getEventType()).isEqualTo(AccountEventType.WITHDRAWN);
    }

    @Test
    public void decide_WithdrawCommand_when_balance_under_zero() {
        AccountCommand command = new AccountCommand(CommandType.WITHDRAW, 50);
        Account account = new Account("account-test");
        List<AccountEvent> events = accountEventService.decide(command, account);
        assertThat(events.get(0).getEventType()).isEqualTo(AccountEventType.ALERT_MAIL_SENT);
    }

    @Test
    public void decide_WithdrawCommand_when_snapshot_needed() {
        int snapshotVersion = 0;
        AccountCommand command = new AccountCommand(CommandType.WITHDRAW, 50);
        Account account = Account.Builder.anAccount()
                                         .id("account-test")
                                         .version(5)
                                         .snapshotVersion(snapshotVersion)
                                         .accountBalance(100).build();
        List<AccountEvent> events = accountEventService.decide(command, account);
        assertThat(events.get(0).getEventType()).isEqualTo(AccountEventType.WITHDRAWN);
        assertThat(events.get(0).getSnapshotVersion()).isEqualTo(snapshotVersion + 1);
    }
}