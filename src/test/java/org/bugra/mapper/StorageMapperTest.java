package org.bugra.mapper;

import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
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
        String csvLine = "10,20,Cardio_Blast_Session,2026-06-26,Fitness,45";

        Training training = storageMapper.parseTraining(csvLine);

        assertNotNull(training);
        assertEquals(10L, training.getTraineeId());
        assertEquals(20L, training.getTrainerId());
        assertEquals("Cardio_Blast_Session", training.getTrainingName());
        assertEquals(LocalDate.of(2026, 6, 26), training.getTrainingDate());
        assertEquals("Fitness", training.getTrainingType().getTrainingTypeName());
        assertEquals(45, training.getTrainingDuration());
    }

    @Test
    @DisplayName("Should throw ArrayIndexOutOfBoundsException when columns are missing for Trainer")
    void shouldThrowExceptionWhenTrainerColumnsAreMissing() {
        // Missing specialization field (only 6 columns instead of 7)
        String malformedLine = "1,Alice,Smith,alice.smith,securePassword123,true";

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            storageMapper.parseTrainer(malformedLine);
        }, "Expected ArrayIndexOutOfBoundsException due to missing columns");
    }

    @Test
    @DisplayName("Should throw ArrayIndexOutOfBoundsException when columns are missing for Trainee")
    void shouldThrowExceptionWhenTraineeColumnsAreMissing() {
        // Missing address field (only 7 columns instead of 8)
        String malformedLine = "2,Robert,Johnson,robert.johnson,pass1234,false,1995-10-25";

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            storageMapper.parseTrainee(malformedLine);
        }, "Expected ArrayIndexOutOfBoundsException due to missing columns");
    }

    @Test
    @DisplayName("Should throw NumberFormatException when ID is not a valid long value")
    void shouldThrowNumberFormatExceptionForInvalidId() {
        // ID is given as "INVALID_ID" instead of a number
        String malformedLine = "INVALID_ID,Alice,Smith,alice.smith,securePassword123,true,Bodybuilding";

        assertThrows(NumberFormatException.class, () -> {
            storageMapper.parseTrainer(malformedLine);
        }, "Expected NumberFormatException due to non-numeric ID");
    }

    @Test
    @DisplayName("Should throw DateTimeParseException when date format is invalid for Trainee")
    void shouldThrowDateTimeParseExceptionForInvalidDateFormat() {
        // Date is in bad format (DD-MM-YYYY) instead of ISO format (YYYY-MM-DD)
        String malformedLine = "2,Robert,Johnson,robert.johnson,pass1234,false,25-10-1995,London/UK";

        assertThrows(java.time.format.DateTimeParseException.class, () -> {
            storageMapper.parseTrainee(malformedLine);
        }, "Expected DateTimeParseException due to incorrect date format");
    }

    @Test
    @DisplayName("Should throw NumberFormatException when training duration is not an integer")
    void shouldThrowNumberFormatExceptionForInvalidDuration() {
        // Duration is given as "45mins" instead of a pure integer "45"
        String malformedLine = "10,20,Cardio_Blast_Session,2026-06-26,Fitness,45mins";

        assertThrows(NumberFormatException.class, () -> {
            storageMapper.parseTraining(malformedLine);
        }, "Expected NumberFormatException due to alphabetic characters in duration");
    }
}