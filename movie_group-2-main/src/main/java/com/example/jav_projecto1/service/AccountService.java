package com.example.jav_projecto1.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.jav_projecto1.entities.Account;
import com.example.jav_projecto1.enumm.Gender;
import com.example.jav_projecto1.respiratory.AccountRespiratory;

@Service
public class AccountService {

    private final AccountRespiratory accRespiratory;

    public AccountService(AccountRespiratory accountRespiratory) {
        this.accRespiratory = accountRespiratory;
    }

    public Account registerUser(String username,
            String password,
            String email,
            String name,
            LocalDate birthday,
            Gender gender,
            String phoneNumber,
            String address) {

        if (accRespiratory.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username has been already taken!");
        }

        Account newacc = Account.builder().username(username).password(password).email(email).name(name)
                .birthday(birthday).gender(gender).phoneNumber(phoneNumber).address(address).build();

        return accRespiratory.save(newacc);
    }

    public Optional<Account> login(String username, String password) {
        Optional<Account> acc_optn = accRespiratory.findByUsername(username);

        if (acc_optn.isPresent()) {
            Account acc = acc_optn.get();
            if (acc.getPassword().equals(password)) {
                return Optional.of(acc);
            }
        }

        return Optional.empty();
    }
    public Account registerUser(Account newacc) {
    if (accRespiratory.findByUsername(newacc.getUsername()).isPresent()) {
        throw new IllegalArgumentException("Username has been already taken!");
    }
    return accRespiratory.save(newacc);
}
}
