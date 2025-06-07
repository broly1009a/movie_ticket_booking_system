package com.example.jav_projecto1.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jav_projecto1.dto.LoginRequest;
import com.example.jav_projecto1.dto.RegisterRequest;
import com.example.jav_projecto1.entities.Account;
import com.example.jav_projecto1.service.AccountService;
import com.example.jav_projecto1.dto.LoginResponse;
import jakarta.servlet.http.HttpSession;
import com.example.jav_projecto1.entities.Role;

import java.time.LocalDate;

import com.example.jav_projecto1.respiratory.RoleRepository;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AccountService accountService;
    private final RoleRepository roleRepository;

    public AuthController(AccountService accountService, RoleRepository roleRepository) {
        this.accountService = accountService;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest registerRequest, HttpSession session) {
        try {
            LocalDate registerDate = LocalDate.now();
            Boolean status = true;
            // Lấy role từ DB thay vì tạo mới
            Role role = roleRepository.findById(2L).orElse(null);
            if (role == null) {
                return ResponseEntity.badRequest().body(null);
            }

            Account newAcc = Account.builder()
                    .username(registerRequest.getUsername())
                    .password(registerRequest.getPassword())
                    .email(registerRequest.getEmail())
                    .name(registerRequest.getName())
                    .birthday(registerRequest.getBirthday())
                    .gender(registerRequest.getGender())
                    .identityCard(registerRequest.getIdentityCard())
                    .phoneNumber(registerRequest.getPhoneNumber())
                    .address(registerRequest.getAddress())
                    .registerDate(registerDate)
                    .status(status)
                    .role(role)
                    .build();

            Account savedAcc = accountService.registerUser(newAcc);
            if (savedAcc == null) {
                return ResponseEntity.badRequest().build();
            }
            session.setAttribute("userLogin", savedAcc);
            LoginResponse resp = LoginResponse.builder()
                    .accountId(savedAcc.getAccountId())
                    .username(savedAcc.getUsername())
                    .email(savedAcc.getEmail())
                    .name(savedAcc.getName())
                    .role(savedAcc.getRole() != null ? savedAcc.getRole().getRoleName().name() : null)
                    .phoneNumber(savedAcc.getPhoneNumber())
                    .address(savedAcc.getAddress())
                    .birthday(savedAcc.getBirthday())
                    .gender(savedAcc.getGender())
                    .build();
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
              e.printStackTrace(); 
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        Optional<Account> accOpt = accountService.login(request.getUsername(), request.getPassword());
        if (accOpt.isPresent()) {
            Account acc = accOpt.get();
            session.setAttribute("userLogin", acc);
            LoginResponse resp = LoginResponse.builder()
                    .accountId(acc.getAccountId())
                    .username(acc.getUsername())
                    .email(acc.getEmail())
                    .name(acc.getName())
                    .role(acc.getRole() != null ? acc.getRole().getRoleName().name() : null)
                    .phoneNumber(acc.getPhoneNumber())
                    .address(acc.getAddress())
                    .birthday(acc.getBirthday())
                    .gender(acc.getGender())
                    .build();
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.removeAttribute("userLogin");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/personal")
    public ResponseEntity<LoginResponse> getPersonalPage(HttpSession session) {
        Account acc = (Account) session.getAttribute("userLogin");
        if (acc == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        LoginResponse resp = LoginResponse.builder()
                .accountId(acc.getAccountId())
                .username(acc.getUsername())
                .email(acc.getEmail())
                .name(acc.getName())
                .role(acc.getRole() != null ? acc.getRole().getRoleName().name() : null)
                .phoneNumber(acc.getPhoneNumber())
                .address(acc.getAddress())
                .birthday(acc.getBirthday())
                .gender(acc.getGender())
                .build();
        return ResponseEntity.ok(resp);
    }
}