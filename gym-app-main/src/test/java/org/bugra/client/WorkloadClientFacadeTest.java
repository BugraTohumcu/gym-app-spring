package org.bugra.client;

import org.bugra.dto.client.SaveTrainerWorkload;
import org.bugra.enums.ActionType;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.bugra.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class WorkloadClientFacadeTest {


    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private WorkloadClientFacade workloadClientFacade;


    @Test
    @DisplayName("Should send to workload-service")
    void sendWorkload_shouldSenToWorkloadClient(){
        SaveTrainerWorkload workload = SaveTrainerWorkload.builder().username("john.doe").build();

        workloadClientFacade.sendWorkload(workload);

        verify(jmsTemplate, times(1)).convertAndSend((String) any(), (Object) any());
    }

    @Test
    @DisplayName("Should build SaveTrainerWorkload from training")
    void buildWorkloadRequest(){
        Trainer trainer = new Trainer();
        trainer.setUser(new User());
        trainer.getUser().setFirstName("Jane");
        trainer.getUser().setLastName("Smith");
        trainer.getUser().setUsername("jane.smith");
        trainer.getUser().setActive(true);

        Training training = new Training();
        LocalDate now = LocalDate.now();

        training.setTrainingDate(now);
        training.setTrainingDuration(120);
        training.setTrainer(trainer);


        SaveTrainerWorkload res = workloadClientFacade.buildWorkloadRequest(training, ActionType.ADD);

        assertEquals("jane.smith", res.username());
        assertEquals("Jane", res.firstName());
        assertEquals("Smith", res.lastName());
        assertTrue(res.isActive());
        assertEquals(now, res.trainingDate());
        assertEquals(120, res.duration());
        assertEquals(ActionType.ADD, res.actionType());


    }
    @Test
    @DisplayName("Should throw exception when client throws")
    void sendWorkload_shouldThrowWhenClientThrows() {
        SaveTrainerWorkload workload = SaveTrainerWorkload.builder()
                .username("john.doe")
                .actionType(ActionType.ADD)
                .build();

        doThrow(new RuntimeException("service down"))
                .when(jmsTemplate).convertAndSend(workload);

        assertThrows(RuntimeException.class,
                () -> workloadClientFacade.sendWorkload(workload));
    }
}