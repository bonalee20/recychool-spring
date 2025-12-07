package com.app.recychool.service;

import com.app.recychool.domain.entity.UserSocial;
import com.app.recychool.domain.vo.UserSocialVO;
import com.app.recychool.exception.UserException;
import com.app.recychool.repository.UserSocialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserSocialServiceImpl implements UserSocialService {
    
    private final UserSocialRepository userSocialRepository;
    
    @Override
    public void registerUserSocial(UserSocial UserSocial) {

    }

    @Override
    public List<String> findAllProvidersById(Long id) {
        List<UserSocial> userSocials = userSocialRepository.findByUser_Id(id);
        return userSocials.stream()
                .map(UserSocial::getUserSocialProvider)
                .filter(provider -> provider != null)
                .toList();
    }

    @Override
    public Long findUserIdByProvider(String provider, String providerId) {
        Optional<UserSocial> userSocial = userSocialRepository.findByUserSocialProviderIgnoreCaseAndUserSocialProviderId(provider, providerId);
        if(userSocial.isEmpty()){
            throw new UserException("provider 에러 발생");
        }
        return userSocial.map(us -> us.getUser().getId()).orElse(null);
    }
}
