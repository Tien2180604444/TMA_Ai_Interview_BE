//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.interview.controller;

import com.example.interview.entity.caching.User;
import com.example.interview.service.UserService;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/auth"})
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping
    public User auth(@AuthenticationPrincipal OidcUser user) {
        return this.userService.SyncUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable UUID id) {
        return this.userService.getUserById(id);
    }
}
