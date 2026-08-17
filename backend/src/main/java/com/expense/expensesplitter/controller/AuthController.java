package com.expense.expensesplitter.controller;

import com.expense.expensesplitter.dto.AuthRequest;
import com.expense.expensesplitter.dto.AuthResponse;
import com.expense.expensesplitter.model.User;
import com.expense.expensesplitter.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    AuthResponse response = authService.login(request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody User user) {
        AuthResponse response = authService.register(user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}