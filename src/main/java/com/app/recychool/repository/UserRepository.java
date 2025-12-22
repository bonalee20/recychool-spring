package com.app.recychool.repository;

import com.app.recychool.domain.dto.UserResponseDTO;
import com.app.recychool.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findIdByUserEmail(String userEmail);
    
    boolean existsByUserEmail(String userEmail);
    
    @Query("SELECT u.userEmail FROM User u WHERE u.userName = :userName AND u.userPhone = :userPhone")
    List<String> findUserEmailByUserNameAndUserPhone(String userName, String userPhone);
    
    User findIdByUserEmailAndUserPhone(String userEmail, String userPhone);

    boolean existsByUserIdentityKey(String userIdentityKey);

    Optional<User> findByUserEmail(String userEmail);
}
