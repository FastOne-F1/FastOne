package com.f1.fastone.user.service;

import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.user.dto.UserAddressCreateRequestDto;
import com.f1.fastone.user.dto.UserAddressDto;
import com.f1.fastone.user.dto.UserAddressUpdateRequestDto;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserAddress;
import com.f1.fastone.user.repository.UserAddressRepository;
import com.f1.fastone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;

    /**
     * 사용자 주소 목록 조회
     */
    @Transactional(readOnly = true)
    public List<UserAddressDto> getMyAddresses(String username) {

        log.debug("주소 목록 조회: username={}", username);

        List<UserAddress> addresses = userAddressRepository
                .findByUserUsernameOrderByCreatedAtDesc(username);

        return addresses.stream()
                .map(UserAddressDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 주소 추가
     */
    @Transactional
    public UserAddressDto addAddress(String username, UserAddressCreateRequestDto requestDto) {

        log.info("주소 추가: username={}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 새 주소 생성
        UserAddress newAddress = UserAddress.builder()
                .postalCode(requestDto.getPostalCode())
                .city(requestDto.getCity())
                .address(requestDto.getAddress())
                .addressDetail(requestDto.getAddressDetail())
                .user(user)
                .build();

        UserAddress savedAddress = userAddressRepository.save(newAddress);

        log.info("주소 추가 완료: username={}, addressId={}", username, savedAddress.getId());

        return UserAddressDto.from(savedAddress);
    }

    /**
     * 주소 수정
     */
    @Transactional
    public UserAddressDto updateAddress(String username, Long addressId,
                                        UserAddressUpdateRequestDto requestDto) {

        log.info("주소 수정: username={}, addressId={}", username, addressId);

        UserAddress address = userAddressRepository
                .findByIdAndUserUsername(addressId, username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "주소를 찾을 수 없습니다."));

        // 주소 업데이트
        address.updateAddress(
                requestDto.getPostalCode(),
                requestDto.getCity(),
                requestDto.getAddress(),
                requestDto.getAddressDetail()
        );

        UserAddress savedAddress = userAddressRepository.save(address);

        log.info("주소 수정 완료: username={}, addressId={}", username, addressId);

        return UserAddressDto.from(savedAddress);
    }

    /**
     * 주소 삭제
     */
    @Transactional
    public void deleteAddress(String username, Long addressId) {

        log.info("주소 삭제: username={}, addressId={}", username, addressId);

        UserAddress address = userAddressRepository
                .findByIdAndUserUsername(addressId, username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "주소를 찾을 수 없습니다."));

        userAddressRepository.delete(address);

        log.info("주소 삭제 완료: username={}, addressId={}", username, addressId);
    }
}
