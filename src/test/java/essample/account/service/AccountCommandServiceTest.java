package essample.account.service;

import essample.account.event.Account;
import essample.account.event.AccountEventService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountCommandServiceTest {

    @MockBean
    private AccountQueryService queryService;

    @MockBean
    private AccountEventService eventService;

    @MockBean
    private AccountEventRepository eventRepository;

    @Autowired
    private AccountCommandService commandService;

    @Before
    public void setUp() {
        Account testAccount = new Account("account-test");

        Mockito.when(queryService.getAccount("account-test"))
               .thenReturn(testAccount);
    }

    @Test
    public void depositAccount() {
    }

    @Test
    public void withdrawAccount() {
    }
}