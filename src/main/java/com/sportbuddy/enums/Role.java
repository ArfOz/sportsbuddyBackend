package com.sportbuddy.enums;

public enum Role {

    USER,               // Regular user
    VERIFIED_USER,      // User with verified phone/email
    PREMIUM_USER,       // User with premium features

    COACH,              // Sports coach
    VERIFIED_COACH,     // Coach with verified certifications

    PARENT,             // Parent managing a child account
    CHILD,              // Child account

    MODERATOR,          // Handles reports and event moderation
    ADMIN               // System administrator
}
