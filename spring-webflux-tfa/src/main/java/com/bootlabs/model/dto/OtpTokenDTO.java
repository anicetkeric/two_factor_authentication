package com.bootlabs.model.dto;

import com.bootlabs.document.User;

public record OtpTokenDTO(String token, User user) {}
