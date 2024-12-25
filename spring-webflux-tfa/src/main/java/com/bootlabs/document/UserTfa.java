package com.bootlabs.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class UserTfa {

    private boolean status = false;

    private LocalDateTime activationDate;

    private String activationSecretKey;
}
