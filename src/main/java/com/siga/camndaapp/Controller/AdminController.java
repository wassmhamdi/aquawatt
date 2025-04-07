package com.siga.camndaapp.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MHAMDI Wassim 27/02/2025
 * SIGA'S Product
 */

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    @PostMapping("/testAdmin")
    public ResponseEntity<String> seyHello() {
        return ResponseEntity.ok("Hello from Admin Controller!");
    }
}