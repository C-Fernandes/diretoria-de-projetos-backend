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
import com.ufrn.imd.diretoriadeprojetos.models.Usuario;
import com.ufrn.imd.diretoriadeprojetos.repository.UsuarioRepository;

@Service
public class AuthService {

        @Autowired
        private UsuarioRepository usuarioRepository;
        @Autowired
        private AuthenticationManager authenticationManager;
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private JwtUtil jwtUtil;

        public void registrar(RegisterRequest request) {
                if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
                        throw new RuntimeException("E-mail já cadastrado.");
                }
                String senhaCriptografada = passwordEncoder.encode(request.getSenha());
                Usuario novoUsuario = new Usuario(request.getNome(), request.getEmail(), senhaCriptografada);
                usuarioRepository.save(novoUsuario);
        }

        public LoginResponse login(String email, String senha) {
                // 1. Busca o usuário pelo e-mail
                Usuario usuario = usuarioRepository.findByEmail(email)
                                .orElseThrow(InvalidCredentials::new);

                // 2. Verifica manualmente se a senha corresponde
                if (!passwordEncoder.matches(senha, usuario.getPassword())) {
                        throw new InvalidCredentials();
                }

                // 3. Gera o token JWT (como antes)
                String token = jwtUtil.generateToken(usuario.getId());

                UserResponse userDto = new UserResponse(
                                usuario.getId(),
                                usuario.getNome(),
                                usuario.getEmail(),
                                usuario.getRole(),
                                usuario.getAprovadoPeloAdmin());

                return new LoginResponse(token, userDto);
        }
}