package com.paypilot.controller;

/** Safe login payload used by the frontend to scope account-owned records. */
public class LoginResponse {
    private final String message;
    private final String userId;
    private final Long accountId;

    public LoginResponse(String message, String userId, Long accountId) {
        this.message = message;
        this.userId = userId;
        this.accountId = accountId;
    }

    public String getMessage() { return message; }
    public String getUserId() { return userId; }
    public Long getAccountId() { return accountId; }
}
