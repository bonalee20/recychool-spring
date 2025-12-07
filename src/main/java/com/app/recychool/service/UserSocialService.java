package com.app.recychool.service;

import com.app.recychool.domain.entity.UserSocial;
import com.app.recychool.domain.vo.UserSocialVO;

import java.util.List;

public interface UserSocialService {
    public void registerUserSocial(UserSocial userSocial);
    public List<String> findAllProvidersById(Long id);
    Long findUserIdByProvider(String provider, String providerId);

}
