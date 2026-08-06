package org.bugra.persistence.repo;

import org.bugra.dto.request.TraineeTrainingFilter;
import org.bugra.dto.request.TrainerTrainingFilter;
import org.bugra.enums.UserRole;
import org.bugra.model.*;
import org.bugra.persistence.BaseJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainingRepoTest extends BaseJpaTest {

    private TrainingRepo trainingRepo;
    private UserRepo userRepo;
    private TraineeRepo traineeRepo;
    private TrainerRepo trainerRepo;

    @BeforeEach
    void init() {
        trainingRepo = new TrainingRepo();
        trainingRepo.setEntityManager(em);

        userRepo = new UserRepo();
        userRepo.setEntityManager(em);

        traineeRepo = new TraineeRepo();
        traineeRepo.setEntityManager(em);

        trainerRepo = new TrainerRepo();
        trainerRepo.setEntityManager(em);
    }

    private Trainee createAndSaveTrainee(String username) {
        User user = createValidUser(username, UserRole.TRAINEE);
        em.persist(user);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        em.persist(trainee);
        return trainee;
    }

    private Trainer createAndSaveTrainer(String username, TrainingType type) {
        User user = createValidUser(username, UserRole.TRAINER);
        em.persist(user);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(type);
        em.persist(trainer);
        return trainer;
    }

    private TrainingType createAndSaveTrainingType(String name) {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName(name);
        em.persist(type);
        return type;
    }

    private Training createAndSaveTraining(Trainee trainee,
                                           Trainer trainer,
                                           TrainingType type,
                                           String name,
                                           LocalDate date,
                                           int duration) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(type);
        training.setTrainingName(name);
        training.setTrainingDate(date);
        training.setTrainingDuration(duration);
        em.persist(training);
        return training;
    }


    @Test
    @DisplayName("Should return all trainings for trainee when no criteria provided")
    void findByTraineeCriteria_shouldReturnAllWhenNoCriteria() {
        TrainingType type = createAndSaveTrainingType("Yoga");
        Trainee trainee = createAndSaveTrainee("john.doe");
        Trainer trainer = createAndSaveTrainer("jane.smith", type);

        createAndSaveTraining(trainee, trainer, type,
                "Morning Yoga",
                LocalDate.of(2024, 1, 15),
                60);

        createAndSaveTraining(trainee, trainer, type, "Evening Yoga",
                LocalDate.of(2024, 2, 15),
                45);

        em.flush();
        em.clear();

        TraineeTrainingFilter filter = new TraineeTrainingFilter("john.doe", null, null, null, null);
        List<Training> result = trainingRepo.findByTraineeCriteria(filter);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should return empty list when trainee has no trainings")
    void findByTraineeCriteria_shouldReturnEmptyWhenNoTrainings() {
        createAndSaveTrainee("john.doe");
        em.flush();
        em.clear();

        TraineeTrainingFilter filter = new TraineeTrainingFilter("john.doe", null, null, null, null);
        List<Training> result = trainingRepo.findByTraineeCriteria(filter);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should filter trainings by fromDate")
    void findByTraineeCriteria_shouldFilterByFromDate() {
        TrainingType type = createAndSaveTrainingType("Yoga");
        Trainee trainee = createAndSaveTrainee("john.doe");
        Trainer trainer = createAndSaveTrainer("jane.smith", type);

        createAndSaveTraining(trainee, trainer, type,
                "Old Training",
                LocalDate.of(2024, 1, 1),
                60);

        createAndSaveTraining(trainee, trainer, type,
                "New Training", LocalDate.of(2024, 6, 1),
                60);

        em.flush();
        em.clear();

        TraineeTrainingFilter filter = new TraineeTrainingFilter(
                "john.doe",
                LocalDate.of(2024, 3, 1),
                null, null,
                null);

        List<Training> result = trainingRepo.findByTraineeCriteria(filter);

        assertEquals(1, result.size());
        assertEquals("New Training", result.get(0).getTrainingName());
    }

    @Test
    @DisplayName("Should filter trainings by toDate")
    void findByTraineeCriteria_shouldFilterByToDate() {
        TrainingType type = createAndSaveTrainingType("Yoga");
        Trainee trainee = createAndSaveTrainee("john.doe");
        Trainer trainer = createAndSaveTrainer("jane.smith", type);

        createAndSaveTraining(trainee, trainer, type,
                "Old Training",
                LocalDate.of(2024, 1, 1),
                60);

        createAndSaveTraining(trainee, trainer, type,
                "New Training",
                LocalDate.of(2024, 6, 1),
                60);

        em.flush();
        em.clear();

        TraineeTrainingFilter filter = new TraineeTrainingFilter(
                "john.doe", null, LocalDate.of(2024, 3, 1), null, null);
        List<Training> result = trainingRepo.findByTraineeCriteria(filter);

        assertEquals(1, result.size());
        assertEquals("Old Training", result.get(0).getTrainingName());
    }

    @Test
    @DisplayName("Should filter trainings by trainer name")
    void findByTraineeCriteria_shouldFilterByTrainerName() {
        TrainingType type = createAndSaveTrainingType("Yoga");
        Trainee trainee = createAndSaveTrainee("john.doe");
        Trainer trainer1 = createAndSaveTrainer("jane.smith", type);
        Trainer trainer2 = createAndSaveTrainer("bob.brown", type);

        createAndSaveTraining(trainee, trainer1, type,
                "Training 1",
                LocalDate.of(2024, 1, 1),
                60);

        createAndSaveTraining(trainee, trainer2, type, "Training 2",
                LocalDate.of(2024, 2, 1),
                60);

        em.flush();
        em.clear();

        TraineeTrainingFilter filter = new TraineeTrainingFilter(
                "john.doe", null, null, "TestFirst", null);
        List<Training> result = trainingRepo.findByTraineeCriteria(filter);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should filter trainings by training type")
    void findByTraineeCriteria_shouldFilterByTrainingType() {
        TrainingType yoga = createAndSaveTrainingType("Yoga");
        TrainingType cardio = createAndSaveTrainingType("Cardio");
        Trainee trainee = createAndSaveTrainee("john.doe");
        Trainer trainer = createAndSaveTrainer("jane.smith", yoga);

        createAndSaveTraining(trainee, trainer, yoga,
                "Yoga Session",
                LocalDate.of(2024, 1, 1), 60);

        createAndSaveTraining(trainee, trainer, cardio,
                "Cardio Session",
                LocalDate.of(2024, 2, 1),
                45);

        em.flush();
        em.clear();

        TraineeTrainingFilter filter = new TraineeTrainingFilter(
                "john.doe", null, null, null, "Yoga");
        List<Training> result = trainingRepo.findByTraineeCriteria(filter);

        assertEquals(1, result.size());
        assertEquals("Yoga Session", result.get(0).getTrainingName());
    }

    @Test
    @DisplayName("Should not return trainings of other trainees")
    void findByTraineeCriteria_shouldNotReturnOtherTraineesTrainings() {
        TrainingType type = createAndSaveTrainingType("Yoga");
        Trainee trainee1 = createAndSaveTrainee("john.doe");
        Trainee trainee2 = createAndSaveTrainee("alice.johnson");
        Trainer trainer = createAndSaveTrainer("jane.smith", type);

        createAndSaveTraining(trainee1, trainer, type,
                "John Training",
                LocalDate.of(2024, 1, 1),
                60);

        createAndSaveTraining(trainee2, trainer, type,
                "Alice Training",
                LocalDate.of(2024, 1, 1),
                60);

        em.flush();
        em.clear();

        TraineeTrainingFilter filter = new TraineeTrainingFilter("john.doe", null, null, null, null);
        List<Training> result = trainingRepo.findByTraineeCriteria(filter);

        assertEquals(1, result.size());
        assertEquals("John Training", result.get(0).getTrainingName());
    }

    @Test
    @DisplayName("Should return all trainings for trainer when no criteria provided")
    void findByTrainerCriteria_shouldReturnAllWhenNoCriteria() {
        TrainingType type = createAndSaveTrainingType("Yoga");
        Trainee trainee = createAndSaveTrainee("john.doe");
        Trainer trainer = createAndSaveTrainer("jane.smith", type);

        createAndSaveTraining(trainee, trainer, type, "Session 1",
                LocalDate.of(2024, 1, 1),
                60);

        createAndSaveTraining(trainee, trainer, type,
                "Session 2",
                LocalDate.of(2024, 2, 1),
                45);
        em.flush();
        em.clear();

        TrainerTrainingFilter filter = new TrainerTrainingFilter("jane.smith", null, null, null);
        List<Training> result = trainingRepo.findByTrainerCriteria(filter);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should filter trainer trainings by fromDate")
    void findByTrainerCriteria_shouldFilterByFromDate() {
        TrainingType type = createAndSaveTrainingType("Yoga");
        Trainee trainee = createAndSaveTrainee("john.doe");
        Trainer trainer = createAndSaveTrainer("jane.smith", type);

        createAndSaveTraining(trainee, trainer, type,
                "Old Session",
                LocalDate.of(2024, 1, 1),
                60);

        createAndSaveTraining(trainee, trainer, type,
                "New Session",
                LocalDate.of(2024, 6, 1),
                60);

        em.flush();
        em.clear();

        TrainerTrainingFilter filter = new TrainerTrainingFilter(
                "jane.smith", LocalDate.of(2024, 3, 1),
                null,
                null);
        List<Training> result = trainingRepo.findByTrainerCriteria(filter);

        assertEquals(1, result.size());
        assertEquals("New Session", result.get(0).getTrainingName());
    }

    @Test
    @DisplayName("Should filter trainer trainings by toDate")
    void findByTrainerCriteria_shouldFilterByToDate() {
        TrainingType type = createAndSaveTrainingType("Yoga");
        Trainee trainee = createAndSaveTrainee("john.doe");
        Trainer trainer = createAndSaveTrainer("jane.smith", type);

        createAndSaveTraining(trainee, trainer, type, "Old Session",
                LocalDate.of(2024, 1, 1), 60);

        createAndSaveTraining(trainee, trainer, type, "New Session",
                LocalDate.of(2024, 6, 1), 60);

        em.flush();
        em.clear();

        TrainerTrainingFilter filter = new TrainerTrainingFilter(
                "jane.smith",
                null,
                LocalDate.of(2024, 3, 1),
                null);

        List<Training> result = trainingRepo.findByTrainerCriteria(filter);

        assertEquals(1, result.size());
        assertEquals("Old Session", result.get(0).getTrainingName());
    }

    @Test
    @DisplayName("Should filter trainer trainings by trainee name")
    void findByTrainerCriteria_shouldFilterByTraineeName() {
        TrainingType type = createAndSaveTrainingType("Yoga");
        Trainee trainee1 = createAndSaveTrainee("john.doe");
        Trainee trainee2 = createAndSaveTrainee("alice.johnson");
        Trainer trainer = createAndSaveTrainer("jane.smith", type);

        createAndSaveTraining(trainee1, trainer, type, "John Session",
                LocalDate.of(2024, 1, 1),
                60);

        createAndSaveTraining(trainee2, trainer, type, "Alice Session",
                LocalDate.of(2024, 2, 1),
                60);

        em.flush();
        em.clear();

        TrainerTrainingFilter filter = new TrainerTrainingFilter(
                "jane.smith", null, null, "TestFirst");
        List<Training> result = trainingRepo.findByTrainerCriteria(filter);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should not return trainings of other trainers")
    void findByTrainerCriteria_shouldNotReturnOtherTrainersTrainings() {
        TrainingType type = createAndSaveTrainingType("Yoga");
        Trainee trainee = createAndSaveTrainee("john.doe");
        Trainer trainer1 = createAndSaveTrainer("jane.smith", type);
        Trainer trainer2 = createAndSaveTrainer("bob.brown", type);

        createAndSaveTraining(trainee, trainer1, type, "Jane Session",
                LocalDate.of(2024, 1, 1), 60);

        createAndSaveTraining(trainee, trainer2, type, "Bob Session",
                LocalDate.of(2024, 1, 1), 60);

        em.flush();
        em.clear();

        TrainerTrainingFilter filter = new TrainerTrainingFilter("jane.smith", null, null, null);
        List<Training> result = trainingRepo.findByTrainerCriteria(filter);

        assertEquals(1, result.size());
        assertEquals("Jane Session", result.get(0).getTrainingName());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when id is null")
    void deleteByTraineeId_shouldThrowWhenIdNull(){
        assertThrows(IllegalArgumentException.class,
                () -> trainingRepo.deleteByTraineeId(null));
    }

    @Test
    @DisplayName("Should return true and delete trainings when trainee has trainings")
    void deleteByTraineeId_shouldReturnTrueWhenTrainingsDeleted() {
        TrainingType type = createAndSaveTrainingType("Yoga");
        Trainee trainee = createAndSaveTrainee("john.doe");
        Trainer trainer = createAndSaveTrainer("jane.smith", type);

        createAndSaveTraining(trainee, trainer, type,
                "Morning Yoga",
                LocalDate.of(2024, 1, 15),
                60);

        em.flush();
        em.clear();

        boolean result = trainingRepo.deleteByTraineeId(trainee.getId());

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when trainee has no trainings to delete")
    void deleteByTraineeId_shouldReturnFalseWhenNoTrainingsExist() {
        Trainee trainee = createAndSaveTrainee("john.doe");

        em.flush();
        em.clear();

        boolean result = trainingRepo.deleteByTraineeId(trainee.getId());

        assertFalse(result);
    }

}