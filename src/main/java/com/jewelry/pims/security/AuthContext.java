package com.jewelry.pims.security;

import com.jewelry.pims.domain.SystemEntities.AuthUser;

public final class AuthContext {

    private static final ThreadLocal<AuthUser> HOLDER = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(AuthUser user) {
        HOLDER.set(user);
    }

    public static AuthUser get() {
        return HOLDER.get();
    }

    public static Long userId() {
        return HOLDER.get() == null ? null : HOLDER.get().getId();
    }

    public static String username() {
        return HOLDER.get() == null ? "anonymous" : HOLDER.get().getUsername();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
