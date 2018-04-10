package essample.account.service;

import essample.account.event.AccountEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountEventRepository extends JpaRepository<AccountEvent, String> {

    List<AccountEvent> findAllByEntityId(String entityId);

}
