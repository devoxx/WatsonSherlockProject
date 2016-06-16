package com.devoxx.watson.service;

import com.ibm.watson.developer_cloud.dialog.v1.DialogService;
import com.ibm.watson.developer_cloud.dialog.v1.model.Conversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Stephan Janssen
 */
@Component
public class ConversationService {

    private DialogService dialogService;

    @Autowired
    public void setDialogService(final DialogService dialogService) {
        this.dialogService = dialogService;
    }

    public Conversation initDialog(final boolean firstTimeUser, final String dialogId) {

        Conversation conversation = dialogService.createConversation(dialogId).execute();

        if (!firstTimeUser) {
            Map<String, String> profile = new HashMap<>();
            profile.put("First_Time", "No");

            dialogService.updateProfile(dialogId, conversation.getClientId(), profile);
        }

        return conversation;
    }
}
