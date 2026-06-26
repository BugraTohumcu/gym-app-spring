package org.bugra.persistence.storage;

import org.bugra.mapper.StorageMapper;
import org.bugra.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StorageInitializerTest {


    private StorageMapper storageMapper;

    @BeforeEach
    void setup(){
        storageMapper = new StorageMapper();
    }

    @Test
    @DisplayName("Trainer should successfully parse the CSV line")
    void shouldParseTrainer(){
        String csvLine = "   1,  John  , Doe,John.Doe ,  secure123, true ,swimming  ";

        Trainer trainer = storageMapper.parseTrainer(csvLine);

        assertEquals(1L, trainer.getId());
        assertEquals("John", trainer.getFirstName());
        assertEquals("Doe", trainer.getLastName());
        assertEquals("John.Doe", trainer.getUsername());
        assertEquals("secure123", trainer.getPassword());
        assertTrue(trainer.isActive());
        assertEquals("swimming", trainer.getSpecialization());

    }
}