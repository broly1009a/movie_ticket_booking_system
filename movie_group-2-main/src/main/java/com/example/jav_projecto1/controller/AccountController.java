package com.example.jav_projecto1.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.jav_projecto1.dto.LoginRequest;
import com.example.jav_projecto1.dto.RegisterRequest;
import com.example.jav_projecto1.entities.Account;
import com.example.jav_projecto1.service.AccountService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/register")
    public String getRegisterpage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register_page";
    }

    @GetMapping("/login")
    public String getLoginpage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login_page";
    }

    @GetMapping("personal")
    public String getPersonalpage(HttpSession session, Model model) {
        Account acc = (Account) session.getAttribute("userLogin");
        if (acc == null) {
            return "redirect:/login";
        }
        model.addAttribute("userLogin", acc);
        return "personal_page";
    }

    @PostMapping("/register")
    public String registerAccount(@ModelAttribute RegisterRequest registerRequest, Model model) {
        try {
            Account newacc = accountService.registerUser(registerRequest.getUsername(), registerRequest.getPassword(),
                    registerRequest.getEmail(), registerRequest.getName(), registerRequest.getBirthday(),
                    registerRequest.getGender(), registerRequest.getPhoneNumber(), registerRequest.getAddress());
            return (newacc == null) ? "error_page" : "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e);
            model.addAttribute("registerRequest", registerRequest);
            return "register_page";
        }

    }

    @PostMapping("/login")
    public String loginAccount(@ModelAttribute LoginRequest request, Model model, HttpSession session) {
        Optional<Account> acc_optn = accountService.login(request.getUsername(), request.getPassword());

        if (acc_optn.isPresent()) {
            session.setAttribute("userLogin", acc_optn.get());
            return "redirect:/personal";
        }
        model.addAttribute("error", "Invalid username or password");
        return "login_page";
    }
}
