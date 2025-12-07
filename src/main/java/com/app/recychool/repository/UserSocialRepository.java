package com.app.recychool.repository;

import com.app.recychool.domain.entity.UserSocial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSocialRepository extends JpaRepository<UserSocial, Long> {
    
    List<UserSocial> findByUser_Id(Long userId);
    
    Optional<UserSocial> findByUserSocialProviderIgnoreCaseAndUserSocialProviderId(String provider, String providerId);
}
