package de.oriontec.postmail.persistence.repo;

import de.oriontec.postmail.persistence.model.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long> {
    List<Account> findByEmail(String email);
    List<Account> findByVerifyToken(String verifyToken);
    List<Account> findByAccessToken(String accessToken);

    @Transactional
    @Modifying
    @Query("update Account acc set acc.status = ?1 where acc.id = ?2")
    int setStatus(Integer status, Long id);

//    @Transactional
//    @Modifying
//    @Query("delete Account where acc.id = ?1")
//    int delete(Long id);

}
