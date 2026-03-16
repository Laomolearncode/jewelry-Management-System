package com.jewelry.pims.service;

import com.jewelry.pims.domain.SystemEntities;
import com.jewelry.pims.dto.auth.AuthDtos;

public interface AuthService {

    AuthDtos.LoginResponse login(AuthDtos.LoginRequest request);

    void logout(String token);

    AuthDtos.ProfileResponse profile();

    SystemEntities.AuthUser parseToken(String token);
}
