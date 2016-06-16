package com.devoxx.watson.service;

import com.ibm.watson.developer_cloud.dialog.v1.DialogService;
import com.ibm.watson.developer_cloud.dialog.v1.model.Conversation;
import org.junit.Test;

/**
 * @author Stephan Janssen
 */
public class ConversationServiceTest {

    @Test
    public void initDialog() {

        final ConversationService conversationService = new ConversationService();

        final DialogService dialogService = new DialogService();

        // dialog.username=2ef1aa4a-6c45-4134-9368-a05889e43cac
        // dialog.password=KyBi7i5XYuah

        dialogService.setUsernameAndPassword("2ef1aa4a-6c45-4134-9368-a05889e43cac", "KyBi7i5XYuah");
        conversationService.setDialogService(dialogService);

        final Conversation conversation = conversationService.initDialog(true, "test");

        assert(conversation != null);
    }
}
