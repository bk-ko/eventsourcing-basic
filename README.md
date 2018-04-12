# Event Sourcing Basic Sample 

- [12 Things You Should Know About Event Sourcing](http://blog.leifbattermann.de/2017/04/21/12-things-you-should-know-about-event-sourcing/)
- [event-sourcing-in-practice](http://ookami86.github.io/event-sourcing-in-practice/)

**Eventsourcing** 에 대해 많은 글 들을 읽었지만  
뭔가 많이 아쉬웠는데 위의 두 article을 읽고 sample을 만들어 보았다.
구현 하면서 느낀 부분들은

- **EventSourcing 과 Command Sourcing 의 차이점**
- **event apply 와 snapshot, replay 구현**
- **command -> aggregate (state) 적용이 까지 cycle** 
- **자연스러운 CQRS**

Microservices 나 Event Driven Architecture 에서 주로 언급되다 보니, 생각이 장황해져서 계좌(account) 생성, 입금(deposit), 출금(withdraw) 라는 아주 간단한 예제로 구현해봤다.


## *apply* function

<blockquote class="twitter-tweet" data-lang="ko"><p lang="en" dir="ltr">want to learn event sourcing? <br><br>f(state, event) =&gt; state</p>&mdash; gregyoung (@gregyoung) <a href="https://twitter.com/gregyoung/status/313358540821647360?ref_src=twsrc%5Etfw">2013년 3월 17일</a></blockquote>

event sourcing에서 가장 중요한 function 이라고 본다.
""event 를 적용한다"" 로 생각하면 간단하지만, 실제로는 생각해야 될게 많다.
event sourcing은 immutable한 event를 통해서 하나의 aggregate를 얻어내는 것이기 때문에 aggregate를 조회 할때 마다 state의 변경(transition) 만 일어나야지, event의 action마저 발생해서는 안되기 때문에 조심해야 한다. ( side effect , mail 발송을 생각하면 쉽다.)

```java
public Account apply(Account account, AccountEvent event) {
    switch (event.getEventType()) {
        case CREATED:
            return account.initialState(account);
        case DEPOSITED:
            return account.deposit(event.getPayload());
        case WITHDRAWN:
            return account.withdraw(event.getPayload());
        case ALERT_MAIL_SENT:
            return account.alertMailSent();
        default:
            throw new IllegalStateException("not supported event type");
    }
}
```

## *replay* & *snapshot*
update 완료된 상태의 aggregate를 조회하는 data sourcing 과 달리 event sourcing은 event 로부터 현재 상태를 조회해오므로 replay는 필수 이다.  side effect 없는 replay는 얼마든지 상관 없다지만,  event 갯수가 엄청나게 많다면 snapshot 이 꼭 필요하다.  

아무리 많은 event가 발생하더라도 매 5번의 event마다 snapshot을 찍으면 최대 5개의 event 조회만으로 충분히 aggregate를 구할수 있다.

```java
public Account getAccount(String accountId) {
     Account account = recreateAccountFromSnapshot(accountId);
     List<AccountEvent> accountEvents =
         repository.findAllByEntityId(accountId);

     return account.replay(accountEvents);
 }
```

## *command*,  *decide*
command는 event를 얻기위해 요청하는 명령 class 라면, decide는 그 command를 실행할지 결정(accept), 검증하고, 필요한  event 목록을 반환하는 function이다.

![decide](https://monosnap.com/image/P4bj9LpDSLCXLnwfqPU7ASXLKnEB8q.png)

```java
public List<AccountEvent> depositAccount(String accountId, Long amount) {
    Account account = queryService.getAccount(accountId);

    AccountCommand command =
        new AccountCommand(CommandType.DEPOSIT, amount);

    List<AccountEvent> accountEvents = eventService.decide(command, account);

    // immutable event 저장 => aggregate 상태 변경
    eventRepository.saveAll(accountEvents);
    return accountEvents;
}
```

## CQRS Pattern
event sourcing  을 하면 command와 query의 작동방식이 확연히 다르기 때문에  (cqrs)[https://martinfowler.com/bliki/CQRS.html] pattern 사용이 매우 자연스럽다. 

이번 Sample 에서는 간단히 Service level로 분리 했다.
```
public class AccountController {
    private AccountQueryService queryService;
    private AccountCommandService commandService;

...
```
![Alt text](https://monosnap.com/image/TKYQ47DHJFaswZIqtdc01Y1dyiYALa.png)

## 느낀점
보통 이야기하는 event sourcing 의 장점은
- event 의 특성상 (decoupled, immutable) reliable 한 domain 변경
- history 를 온전히 저장;
- O/R 불일치에서 오는 문제 해결
- 언제든지 replay 할수 있는 자유로운 single source of truth

kafka 와 같은 disk 저장이 되는 Message queue 와 너무 어울리는것 같다.

하지만 직접 구현해보니,
- 확실히 over engineering 이 되기 싶다
- schema evolution 에 대한 고려...
- side effect 관리

무조건 적용하기 보다는 영리하게 사용해야하면 좋을것 같다, 

> **[Spring Statemachine]**(https://projects.spring.io/spring-statemachine/) 
> Microservice 에서 saga와 같이 state 관리를 위해서 많이 언급되는데,
> statemachine을 매번 조회 하는게 너무 무겁다는 생각이 들었고, 본연의 복잡함(?) 때문에 성능도 안나왔던 기억이... 

### 참고
>[CQRS journey -Microsoft](https://msdn.microsoft.com/en-us/library/jj554200.aspx)
>[Microservices Patterns](https://www.amazon.com/Microservice-Patterns-Chris-Richardson/dp/1617294543)
