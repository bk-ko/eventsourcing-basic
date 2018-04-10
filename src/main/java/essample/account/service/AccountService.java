package essample.account.service;

import essample.account.command.AccountCommand;
import essample.account.command.CommandType;
import essample.account.event.Account;
import essample.account.event.AccountEvent;
import essample.account.event.AccountEventService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private AccountQueryService queryService;
    private AccountEventService eventService;
    private AccountEventRepository eventRepository;

    public AccountService(AccountQueryService queryService, AccountEventService eventService, AccountEventRepository eventRepository) {
        this.queryService = queryService;
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

    public AccountEvent createAccount() {
        AccountCommand command =
            new AccountCommand(CommandType.CREATE, 0);

        AccountEvent accountEvent = eventService.decide(command, null).get(0);

        eventRepository.save(accountEvent);
        return accountEvent;
    }

    public List<AccountEvent> depositAccount(String accountId, Long amount) {
        Account account = queryService.getAccount(accountId);

        AccountCommand command =
            new AccountCommand(CommandType.DEPOSIT, amount);

        List<AccountEvent> accountEvents = eventService.decide(command, account);

        eventRepository.saveAll(accountEvents);
        return accountEvents;
    }

    public List<AccountEvent> withdrawAccount(String accountId, Long amount) {
        Account account = queryService.getAccount(accountId);

        AccountCommand command =
            new AccountCommand(CommandType.WITHDRAW, amount);

        List<AccountEvent> accountEvents = eventService.decide(command, account);

        eventRepository.saveAll(accountEvents);
        return accountEvents;
    }

}