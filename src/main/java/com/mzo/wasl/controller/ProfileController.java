package com.mzo.wasl.controller;

import com.mzo.wasl.payload.request.ProfileRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class ProfileController {

    @GetMapping("/profile")
    @PreAuthorize("hasRole('REGULAR')")
    public String getProfile() {
        /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getPrincipal().toString();*/
        return "profile";
    }


    @PutMapping("/profile")
    @PreAuthorize("hasRole('REGULAR')")
    public void UpdateProfile(@RequestBody ProfileRequest profileRequest) {
        /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getPrincipal().toString();*/
    }

}
