package essample.account;

import essample.account.event.Account;
import essample.account.event.AccountEvent;
import essample.account.service.AccountQueryService;
import essample.account.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private AccountQueryService queryService;

    private AccountService commandService;

    public AccountController(AccountQueryService queryService, AccountService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    @GetMapping("/test")
    public Account fullTest() {
        AccountEvent createEvent = commandService.createAccount();

        String entityId = createEvent.getEntityId();
        commandService.depositAccount(entityId, 500L);
        commandService.depositAccount(entityId, 500L);
        commandService.depositAccount(entityId, 500L);
        commandService.depositAccount(entityId, 500L);
        commandService.depositAccount(entityId, 500L);
        commandService.withdrawAccount(entityId, 1000L);
        commandService.withdrawAccount(entityId, 1000L);
        commandService.withdrawAccount(entityId, 1000L);

        return queryService.getAccount(entityId);
    }
}
