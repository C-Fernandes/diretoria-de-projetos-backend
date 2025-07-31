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
import com.ufrn.imd.diretoriadeprojetos.models.Usuario;
import com.ufrn.imd.diretoriadeprojetos.repository.UsuarioRepository;

@Service
public class UsuarioService {

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
                String senhaCriptografada = passwordEncoder.encode(request.getPassword());
                Usuario novoUsuario = new Usuario(request.getName(), request.getEmail(), senhaCriptografada);
                usuarioRepository.save(novoUsuario);
        }

        public String login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));

                Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado após autenticação"));

                return jwtUtil.generateToken(usuario.getId());
        }
}