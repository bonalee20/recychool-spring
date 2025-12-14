package com.app.recychool.repository;

import com.app.recychool.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findIdByUserEmail(String userEmail);
    
    boolean existsByUserEmail(String userEmail);
    
    List<String> findUserEmailByUserNameAndUserPhone(String userName, String userPhone);
    
    User findIdByUserEmailAndUserPhone(String userEmail, String userPhone);

    boolean existsByUserIdentityKey(String userIdentityKey);

}
