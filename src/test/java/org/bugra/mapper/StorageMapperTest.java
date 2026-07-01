package org.bugra.mapper;

import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.bugra.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StorageMapperTest {

    private StorageMapper storageMapper;

    @BeforeEach
    void setup(){
        storageMapper = new StorageMapper();
    }

    @Test
    @DisplayName("Trainer should successfully parse the CSV line with gym specialization")
    void shouldParseTrainerSuccessfully(){
        String csvLine = "1,Alice,Smith,alice.smith,securePassword123,true,Bodybuilding";

        Trainer trainer = storageMapper.parseTrainer(csvLine);

        assertEquals(1L, trainer.getId());
        assertEquals("Alice", trainer.getFirstName());
        assertEquals("Smith", trainer.getLastName());
        assertEquals("alice.smith", trainer.getUsername());
        assertEquals("securePassword123", trainer.getPassword());
        assertTrue(trainer.isActive());
        assertEquals("Bodybuilding", trainer.getSpecialization());
    }

    @Test
    @DisplayName("Trainer parsing should handle and trim spaces in gym input data")
    void shouldParseSpacedParts(){
        String csvLine = "   1,  Alice  , Smith,alice.smith ,  securePassword123, true ,CrossFit  ";

        Trainer trainer = storageMapper.parseTrainer(csvLine);

        assertEquals(1L, trainer.getId());
        assertEquals("Alice", trainer.getFirstName());
        assertEquals("Smith", trainer.getLastName());
        assertEquals("alice.smith", trainer.getUsername());
        assertEquals("securePassword123", trainer.getPassword());
        assertTrue(trainer.isActive());
        assertEquals("CrossFit", trainer.getSpecialization());
    }

    @Test
    @DisplayName("Trainee should successfully parse the CSV line with personal gym profile")
    void shouldParseTraineeSuccessfully() {
        String csvLine = "2,Robert,Johnson,robert.johnson,pass1234,false,1995-10-25,London/UK";

        Trainee trainee = storageMapper.parseTrainee(csvLine);

        assertNotNull(trainee);
        assertEquals(2L, trainee.getId());
        assertEquals("Robert", trainee.getFirstName());
        assertEquals("Johnson", trainee.getLastName());
        assertEquals("robert.johnson", trainee.getUsername());
        assertEquals("pass1234", trainee.getPassword());
        assertFalse(trainee.isActive());
        assertEquals(LocalDate.of(1995, 10, 25), trainee.getDateOfBirth());
        assertEquals("London/UK", trainee.getAddress());
    }

    @Test
    @DisplayName("Training should successfully parse the CSV line with specific workout session details")
    void shouldParseTrainingSuccessfully() {
        String csvLine = "1, 10, 20, Cardio_Blast_Session, Fitness, 2026-06-26, 45";

        Training training = storageMapper.parseTraining(csvLine);

        assertNotNull(training);
        assertEquals(1L, training.getId());
        assertEquals(10L, training.getTraineeId());
        assertEquals(20L, training.getTrainerId());
        assertEquals("Cardio_Blast_Session", training.getTrainingName());
        assertEquals("Fitness", training.getTrainingType().getTrainingTypeName());
        assertEquals(LocalDate.of(2026, 6, 26), training.getTrainingDate());
        assertEquals(45, training.getTrainingDuration());
    }

    @Test
    @DisplayName("Should throw ArrayIndexOutOfBoundsException when columns are missing for Trainer")
    void shouldThrowExceptionWhenTrainerColumnsAreMissing() {
        String malformedLine = "1,Alice,Smith,alice.smith,securePassword123,true";

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            storageMapper.parseTrainer(malformedLine);
        });
    }

    @Test
    @DisplayName("Should throw ArrayIndexOutOfBoundsException when columns are missing for Trainee")
    void shouldThrowExceptionWhenTraineeColumnsAreMissing() {
        String malformedLine = "2,Robert,Johnson,robert.johnson,pass1234,false,1995-10-25";

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            storageMapper.parseTrainee(malformedLine);
        });
    }

    @Test
    @DisplayName("Should throw NumberFormatException when ID is not a valid long value")
    void shouldThrowNumberFormatExceptionForInvalidId() {
        String malformedLine = "INVALID_ID,Alice,Smith,alice.smith,securePassword123,true,Bodybuilding";

        assertThrows(NumberFormatException.class, () -> {
            storageMapper.parseTrainer(malformedLine);
        });
    }

    @Test
    @DisplayName("Should throw DateTimeParseException when date format is invalid for Trainee")
    void shouldThrowDateTimeParseExceptionForInvalidDateFormat() {
        String malformedLine = "2,Robert,Johnson,robert.johnson,pass1234,false,25-10-1995,London/UK";

        assertThrows(java.time.format.DateTimeParseException.class, () -> {
            storageMapper.parseTrainee(malformedLine);
        });
    }

    @Test
    @DisplayName("Should throw NumberFormatException when training duration is not an integer")
    void shouldThrowNumberFormatExceptionForInvalidDuration() {
        String malformedLine = "1,10,20,Cardio_Blast_Session,Fitness,2026-06-26,45mins";

        assertThrows(NumberFormatException.class, () -> {
            storageMapper.parseTraining(malformedLine);
        });
    }

    @Test
    @DisplayName("Should format Trainer correctly to CSV string")
    void shouldFormatTrainerCorrectly() {
        Trainer t = new Trainer();
        t.setId(1L);
        t.setFirstName("Alice");
        t.setLastName("Smith");
        t.setUsername("alice.smith");
        t.setPassword("pass123");
        t.setActive(true);
        t.setSpecialization("Yoga");

        String formatted = storageMapper.formatTrainer(t);
        assertEquals("1,Alice,Smith,alice.smith,pass123,true,Yoga", formatted);
    }

    @Test
    @DisplayName("Should format Trainee correctly to CSV string")
    void shouldFormatTraineeCorrectly() {
        Trainee t = new Trainee();
        t.setId(2L);
        t.setFirstName("Bob");
        t.setLastName("Brown");
        t.setUsername("bob.brown");
        t.setPassword("pass456");
        t.setActive(true);
        t.setDateOfBirth(LocalDate.of(2000, 1, 1));
        t.setAddress("Street 1");

        String formatted = storageMapper.formatTrainee(t);
        assertEquals("2,Bob,Brown,bob.brown,pass456,true,2000-01-01,Street 1", formatted);
    }

    @Test
    @DisplayName("Should format Training correctly to CSV string")
    void shouldFormatTrainingCorrectly() {
        Training t = new Training();
        t.setId(3L);
        t.setTraineeId(10L);
        t.setTrainerId(20L);
        t.setTrainingName("HIIT");
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Cardio");
        t.setTrainingType(type);
        t.setTrainingDate(LocalDate.of(2026, 1, 1));
        t.setTrainingDuration(30);

        String formatted = storageMapper.formatTraining(t);
        assertEquals("3,10,20,HIIT,Cardio,2026-01-01,30", formatted);
    }
}