package com.aimms.common;

public final class Constants {

    private Constants() {
    }

    public static final String ROLE_EMPLOYEE = "employee";
    public static final String ROLE_MANAGER = "manager";
    public static final String ROLE_ADMIN = "admin";

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLE = "X-User-Role";

    public static final String QUOTA_TYPE_MONTHLY_BASE = "monthly_base";
    public static final String QUOTA_TYPE_ADDITIONAL = "additional";

    public static final String APPLICATION_STATUS_PENDING = "pending";
    public static final String APPLICATION_STATUS_MANAGER_APPROVED = "manager_approved";
    public static final String APPLICATION_STATUS_ADMIN_APPROVED = "admin_approved";
    public static final String APPLICATION_STATUS_REJECTED = "rejected";

    public static final String BILLING_MODE_SEAT = "seat";
    public static final String BILLING_MODE_TOKEN = "token";
    public static final String BILLING_MODE_CREDIT = "credit";
    public static final String BILLING_MODE_MIXED = "mixed";

    public static final String SYNC_TYPE_API = "api";
    public static final String SYNC_TYPE_FILE = "file";
    public static final String SYNC_TYPE_MANUAL = "manual";
}
