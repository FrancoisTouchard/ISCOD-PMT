package com.iscod.pmt.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.iscod.pmt.models.AppUser;
import com.iscod.pmt.models.Task;
import com.iscod.pmt.services.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.mail.from:noreply@pmt.com}")
    private String fromEmail;
    
    @Value("${app.mail.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    @Async
    public void sendTaskAssignmentNotification(Task task, AppUser assignee) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(assignee.getEmail());
            helper.setSubject("Nouvelle tâche assignée : " + task.getName());
            
            String htmlContent = buildTaskAssignmentEmail(task, assignee);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
        } catch (MessagingException | MailException e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }
    
    private String buildTaskAssignmentEmail(Task task, AppUser assignee) {
        String taskUrl = baseUrl + "/projects/" + task.getProject().getId() + "/tasks/" + task.getId();
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { background-color: #f9f9f9; padding: 20px; margin: 20px 0; }
                    .task-details { background-color: white; padding: 15px; border-left: 4px solid #4CAF50; }
                    .detail-row { margin: 10px 0; }
                    .label { font-weight: bold; color: #555; }
                    .button { 
                        display: inline-block; 
                        padding: 10px 20px; 
                        background-color: #4CAF50; 
                        color: white; 
                        text-decoration: none; 
                        border-radius: 5px;
                        margin-top: 15px;
                    }
                    .footer { text-align: center; color: #777; font-size: 12px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Nouvelle tâche assignée</h2>
                    </div>
                    <div class="content">
                        <p>Bonjour %s,</p>
                        <p>Une nouvelle tâche vous a été assignée dans le projet <strong>%s</strong>.</p>
                        
                        <div class="task-details">
                            <div class="detail-row">
                                <span class="label">Tâche :</span> %s
                            </div>
                            <div class="detail-row">
                                <span class="label">Description :</span> %s
                            </div>
                            <div class="detail-row">
                                <span class="label">Priorité :</span> %s
                            </div>
                            <div class="detail-row">
                                <span class="label">Échéance :</span> %s
                            </div>
                            <div class="detail-row">
                                <span class="label">Statut :</span> %s
                            </div>
                        </div>
                        
                        <a href="%s" class="button">Voir la tâche</a>
                    </div>
                    <div class="footer">
                        <p>Ceci est un email automatique, merci de ne pas y répondre.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                assignee.getName(),
                task.getProject().getName(),
                task.getName(),
                task.getDescription() != null ? task.getDescription() : "Aucune description",
                task.getPriority() != null ? task.getPriority().toString() : "Non définie",
                task.getDueDate() != null ? task.getDueDate().toString() : "Non définie",
                task.getStatus() != null ? task.getStatus().toString() : "Non défini",
                taskUrl
            );
    }
    
}