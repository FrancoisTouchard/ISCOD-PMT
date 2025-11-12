package com.iscod.pmt.services;

import com.iscod.pmt.models.AppUser;
import com.iscod.pmt.models.Task;

public interface EmailService {
    
    /**
     * Envoie une notification par email lors de l'assignation d'une tâche
     * @param task La tâche assignée
     * @param assignee L'utilisateur assigné
     */
    void sendTaskAssignmentNotification(Task task, AppUser assignee);
    
}