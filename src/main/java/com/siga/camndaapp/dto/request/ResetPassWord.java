package com.siga.camndaapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author MHAMDI Wassim 06/03/2025
 * SIGA'S Product
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPassWord {

    private String key;
    private String newPassword;
    private String confirmPassword;
}
