package ru.lab1.dealership.web.dto;

public record UserRequest(String fullName, ru.lab1.dealership.domain.user.UserRole role) {}
