package com.f1.fastone.user.dto;

import com.f1.fastone.user.entity.UserRole;

public record UpdateUserRoleRequestDto (
    String username,
    UserRole role
) {}
