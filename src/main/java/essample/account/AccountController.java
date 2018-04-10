package essample.account;

import essample.account.event.Account;
import essample.account.event.AccountEvent;
import essample.account.service.AccountCommandService;
import essample.account.service.AccountQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private AccountQueryService queryService;

    private AccountCommandService commandService;

    public AccountController(AccountQueryService queryService, AccountCommandService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    @GetMapping("/accounts/{id}")
    public Account getAccount(@PathVariable String id) {
        return queryService.getAccount(id);
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
