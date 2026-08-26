package com.bugra.workloadservice.messaging.listener;

import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.messaging.router.JmsMessageRouter;
import com.bugra.workloadservice.service.TrainerService;
import jakarta.jms.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class WorkloadListenerTest {

    @Mock
    private TrainerService trainerService;

    @Mock
    private JmsMessageRouter messageRouter;

    @InjectMocks
    private WorkloadListener workloadListener;

    @Test
    @DisplayName("Should invoke router with exactly correct parameters and DTO type")
    void onWorkloadMessageReceived_shouldInvokeRouterWithExactParams() {
        Message mockMessage = mock(Message.class);
        String expectedDlq = "mock-dlq";

        ReflectionTestUtils.setField(workloadListener, "workloadDlq", expectedDlq);
        workloadListener.onWorkloadMessageReceived(mockMessage);

        verify(messageRouter, times(1)).routeMessage(
                eq(mockMessage),
                eq(TrainerDto.class),
                eq(expectedDlq),
                any()
        );
    }
}