package com.f1.fastone.user.repository;

import com.f1.fastone.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    // 사용자의 모든 주소 조회
    List<UserAddress> findByUserUsernameOrderByCreatedAtDesc(String username);

    // 사용자의 특정 주소 조회
    Optional<UserAddress> findByIdAndUserUsername(Long id, String username);

    // 사용자의 주소 개수 조회
    long countByUserUsername(String username);

    // 사용자 대표 주소 조회
    Optional<UserAddress> findFirstByUserUsernameAndIsDefaultTrue(String username);
}
