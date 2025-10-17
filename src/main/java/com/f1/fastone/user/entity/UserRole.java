package com.f1.fastone.user.entity;

public enum UserRole {
    CUSTOMER(Authority.CUSTOMER),    // 고객 권한
    OWNER(Authority.OWNER),         // 업체 사장 권한
    MANAGER(Authority.MANAGER),     // 매니저 권한
    MASTER(Authority.MASTER);         // 관리자 권한

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String CUSTOMER = "ROLE_CUSTOMER";
        public static final String OWNER = "ROLE_OWNER";
        public static final String MANAGER = "ROLE_MANAGER";
        public static final String MASTER = "ROLE_MASTER";
    }
}
