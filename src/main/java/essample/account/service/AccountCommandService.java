package essample.account.service;

import essample.account.command.AccountCommand;
import essample.account.command.CommandType;
import essample.account.event.Account;
import essample.account.event.AccountEvent;
import essample.account.event.AccountEventService;
import essample.account.event.AccountEventType;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class AccountCommandService {

    private AccountQueryService queryService;
    private AccountEventService eventService;
    private AccountEventRepository eventRepository;

    public AccountCommandService(AccountQueryService queryService,
                                 AccountEventService eventService,
                                 AccountEventRepository eventRepository) {
        this.queryService = queryService;
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

    public List<AccountEvent> createAccount() {
        AccountCommand command =
            new AccountCommand(CommandType.CREATE, 0);

        Account newAccount = new Account("account-" + UUID.randomUUID().toString().substring(0, 8));

        List<AccountEvent> accountEvents = eventService.decide(command, newAccount);

        eventRepository.saveAll(accountEvents);
        return accountEvents;
    }

    public List<AccountEvent> depositAccount(String accountId, Long amount) {
        Account account = queryService.getAccount(accountId);

        AccountCommand command =
            new AccountCommand(CommandType.DEPOSIT, amount);

        List<AccountEvent> accountEvents = eventService.decide(command, account);

        eventRepository.saveAll(accountEvents);
        return accountEvents;
    }

    @Transactional
    public List<AccountEvent> withdrawAccount(String accountId, Long amount) {
        Account account = queryService.getAccount(accountId);

        AccountCommand command =
            new AccountCommand(CommandType.WITHDRAW, amount);

        List<AccountEvent> accountEvents = eventService.decide(command, account);

        // events and state changes are decoupled
        accountEvents.stream()
                     .filter(e -> e.getEventType() == AccountEventType.ALERT_MAIL_SENT)
                     .forEach(e -> System.out.println(e.getEventType() + " : " + e.getEntityId()));

        eventRepository.saveAll(accountEvents);
        return accountEvents;
    }

}
