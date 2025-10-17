package com.f1.fastone.user.service;

import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.user.dto.SignUpRequestDto;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserRole;
import com.f1.fastone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // MASTER_TOKEN
    private final String MASTER_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    @Transactional
    public void signup(SignUpRequestDto requestDto) {
        String username = requestDto.getUsername();
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인 - ErrorCode 사용
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(ErrorCode.USER_USERNAME_DUPLICATED.getMessage());
        }

        // email 중복확인 - ErrorCode 사용
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(ErrorCode.USER_EMAIL_DUPLICATED.getMessage());
        }

        // 사용자 ROLE 확인
        UserRole role = UserRole.CUSTOMER;
        if (requestDto.isMaster()) {
            if (requestDto.getMasterToken() == null ||
                    requestDto.getMasterToken().trim().isEmpty() ||
                    !MASTER_TOKEN.equals(requestDto.getMasterToken())) {
                throw new IllegalArgumentException(ErrorCode.USER_INVALID_MASTER_TOKEN.getMessage());
            }
            role = UserRole.MASTER;  // MASTER 역할 부여
        }

        // 사용자 등록 - Builder 패턴 사용
        User user = User.builder()
                .username(username)
                .nickname(requestDto.getNickname())
                .email(email)
                .password(password)
                .role(role)
                .isPublic(requestDto.getIsPublic())
                .phoneNumber(requestDto.getPhoneNumber())
                .build();

        userRepository.save(user);
    }
}
