package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.ArrayList;

import javax.xml.validation.Validator;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.auth.JwtUtil;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.LoginRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.RegisterRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.LoginResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.UserResponse;
import com.ufrn.imd.diretoriadeprojetos.errors.InvalidCredentials;
import com.ufrn.imd.diretoriadeprojetos.models.User;
import com.ufrn.imd.diretoriadeprojetos.repository.UserRepository;

@Service
public class AuthService {
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private PasswordEncoder passwordEncoder;
        @Autowired
        private JwtUtil jwtUtil;

        public void registrar(RegisterRequest request) {
                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                        throw new RuntimeException("E-mail já cadastrado.");
                }
                String senhaCriptografada = passwordEncoder.encode(request.getSenha());
                User novoUser = new User(request.getNome(), request.getEmail(), senhaCriptografada);
                userRepository.save(novoUser);
        }

        public LoginResponse login(String email, String senha) {
                User User = userRepository.findByEmail(email)
                                .orElseThrow(InvalidCredentials::new);

                if (!passwordEncoder.matches(senha, User.getPassword())) {
                        throw new InvalidCredentials();
                }
                String token = jwtUtil.generateToken(User.getId());

                UserResponse userDto = new UserResponse(
                                User.getId(),
                                User.getName(),
                                User.getEmail(),
                                User.getRole(),
                                User.getIsAdminApproved());

                return new LoginResponse(token, userDto);
        }
}