package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.ufrn.imd.diretoriadeprojetos.dtos.response.UserResponse;
import com.ufrn.imd.diretoriadeprojetos.enums.Role;
import com.ufrn.imd.diretoriadeprojetos.errors.EntityNotFound;
import com.ufrn.imd.diretoriadeprojetos.models.User;
import com.ufrn.imd.diretoriadeprojetos.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender emailSender;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void updateApprovalStatus(UUID userId, boolean isApproved) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFound("Usuário não encontrado com o ID: " + userId));

        userToUpdate.setIsAdminApproved(isApproved);
        userRepository.save(userToUpdate);

        if (isApproved) {
            sendApprovalNotification(userToUpdate);
        }
    }

    private void sendApprovalNotification(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@diretoriodeprojetos.com");
            message.setTo(user.getEmail());
            message.setSubject("✅ Sua conta no Diretório de Projetos foi Aprovada");

            String text = String.format(
                    "Olá, %s!\n\nBoas notícias! Sua conta no Diretório de Projetos da UFRN foi revisada e aprovada por um administrador.\n\n"
                            +
                            "Você já pode fazer login para acessar a plataforma.\n\n" +
                            "Atenciosamente,\nEquipe do Diretório de Projetos - IMD/UFRN",
                    user.getName());

            message.setText(text);
            emailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERRO: O e-mail de aprovação para " + user.getEmail() + " não pôde ser enviado. Causa: "
                    + e.getMessage());
        }
    }

    @Transactional
    public void updateUserRole(UUID userId, Role newRole) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFound("Usuário não encontrado com o ID: " + userId));

        userToUpdate.setRole(newRole);

        userRepository.save(userToUpdate);

        sendRoleChangeNotification(userToUpdate, newRole);
    }

    private void sendRoleChangeNotification(User user, Role newRole) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@diretoriodeprojetos.com");
            message.setTo(user.getEmail());
            message.setSubject("⚠️ Alteração de Permissão da sua Conta");

            String text = String.format(
                    "Olá, %s!\n\nInformamos que a sua permissão de acesso no Diretório de Projetos foi alterada por um administrador.\n\n"
                            +
                            "Sua nova permissão é: %s.\n\n" +
                            "Se você não reconhece essa alteração ou acredita que foi um engano, por favor, entre em contato com o suporte.\n\n"
                            +
                            "Atenciosamente,\nEquipe do Diretório de Projetos - IMD/UFRN",
                    user.getName(),
                    newRole.toString() // Converte o Enum para String (ex: "ADMIN")
            );

            message.setText(text);
            emailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERRO: O e-mail de notificação de role para " + user.getEmail()
                    + " não pôde ser enviado. Causa: " + e.getMessage());
        }
    }
}
