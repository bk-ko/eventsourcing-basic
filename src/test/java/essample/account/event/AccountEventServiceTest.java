package essample.account.event;

import essample.account.service.AccountSnapshotRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class AccountEventServiceTest {

    @MockBean
    AccountSnapshotRepository snapshotRepository;

    @Autowired
    AccountEventService accountEventService;

    @Test
    public void decide() {
    }
}