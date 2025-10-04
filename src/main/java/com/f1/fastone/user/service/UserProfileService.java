package com.f1.fastone.user.service;

import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.user.dto.UserProfileDto;
import com.f1.fastone.user.dto.UserProfileUpdateRequestDto;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserRepository userRepository;

    /**
     * username으로 프로필 조회 (주소 목록 포함)
     */
    @Transactional(readOnly = true)
    public UserProfileDto getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, "유저가 없습니다."));

        if (user.isDeleted()) {
            log.warn("삭제된 사용자 접근 시도: username={}", username);
            throw new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, "삭제된 사용자입니다.");
        }

        log.debug("프로필 조회 성공: username={}, 주소 개수={}",
                username, user.getAddresses().size());

        return UserProfileDto.from(user);
    }


    /**
     * 사용자 기본 프로필 수정 (주소 제외)
     */
    @Transactional
    public UserProfileDto updateMyProfile(String username, UserProfileUpdateRequestDto requestDto) {

        log.info("프로필 수정 시작: username={}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 사용자: username={}", username);
                    return new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, "유저가 없습니다.");
                });

        if (user.isDeleted()) {
            log.warn("삭제된 사용자 수정 시도: username={}", username);
            throw new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, "삭제된 사용자입니다.");
        }

        // 기본 프로필만 업데이트
        updateUserProfile(user, requestDto);

        User savedUser = userRepository.save(user);

        log.info("프로필 수정 완료: username={}", username);

        return UserProfileDto.from(savedUser);
    }

    /**
     * User 기본 정보 업데이트
     */
    private void updateUserProfile(User user, UserProfileUpdateRequestDto requestDto) {
        if (requestDto.getNickname() != null) {
            user.updateNickname(requestDto.getNickname());
        }

        if (requestDto.getPhoneNumber() != null) {
            user.updatePhoneNumber(requestDto.getPhoneNumber());
        }

        if (requestDto.getIsPublic() != null) {
            user.updateIsPublic(requestDto.getIsPublic());
        }
    }

    /**
     * 회원 탈퇴 (soft delete)
     */
    @Transactional
    public void deleteMyAccount(String username) {
        log.info("회원 탈퇴 요청: username={}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, "유저가 없습니다."));

        if (user.isDeleted()) {
            log.warn("이미 삭제된 사용자: username={}", username);
            throw new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, "이미 탈퇴한 사용자입니다.");
        }

        // Soft delete 처리 (deleted_at, deleted_by 설정)
        user.softDelete(username);  // BaseEntity의 delete 메서드 사용
        userRepository.save(user);

        log.info("회원 탈퇴 완료: username={}", username);
    }

}
