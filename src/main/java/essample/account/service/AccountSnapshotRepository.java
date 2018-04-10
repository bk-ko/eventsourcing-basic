package essample.account.service;

import essample.account.event.AccountSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountSnapshotRepository extends JpaRepository<AccountSnapshot, String> {
    AccountSnapshot findFirstByEntityIdOrderBySnapshotVersionDesc(String entityId);
}
