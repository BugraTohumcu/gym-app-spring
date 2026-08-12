package org.bugra.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bugra.dto.request.RegisterTrainer;
import org.bugra.dto.request.UpdateTrainer;
import org.bugra.dto.response.UserResponse;
import org.bugra.enums.UserRole;
import org.bugra.exception.TrainingTypeNotFoundException;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainer;
import org.bugra.model.TrainingType;
import org.bugra.model.User;
import org.bugra.persistence.repo.TrainerRepo;
import org.bugra.persistence.repo.TrainingTypeRepo;
import org.bugra.security.JwtTokenProvider;
import org.bugra.security.dto.TokenPayload;
import org.bugra.service.TrainerService;
import org.bugra.service.UserCredentialsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private final TrainerRepo trainerRepo;
    private final UserCredentialsService userCredentialsService;
    private final TrainingTypeRepo trainingTypeRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    @Override
    public UserResponse createTrainer(RegisterTrainer registerTrainer) {

        String trainingTypeName = registerTrainer.specialization();

        TrainingType type = trainingTypeRepo.findByTrainingTypeName(trainingTypeName)
                .orElseThrow(TrainingTypeNotFoundException::new);

        Trainer trainer = new Trainer();
        trainer.setSpecialization(type);

        User user = new User();
        user.setFirstName(registerTrainer.firstName());
        user.setLastName(registerTrainer.lastName());

        // Credentials generation
        String password = userCredentialsService.generateRandomPassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setUsername(userCredentialsService.generateUsername(
                user.getFirstName(),
                user.getLastName()
        ));

        user.setActive(true);
        user.setRole(UserRole.TRAINER);

        trainer.setUser(user);
        Trainer savedTrainer = trainerRepo.save(trainer);
        logger.info("Trainer created successfully with ID: {} and username: {}",
                savedTrainer.getId(),
                savedTrainer.getUser().getUsername());

        String accessToken = tokenProvider.generateAccessToken(new TokenPayload(user.getUsername()));

        return new UserResponse(
                savedTrainer.getUser().getUsername(),
                password,
                accessToken
        );
    }

    @Transactional
    @Override
    public Trainer updateTrainer(UpdateTrainer updateTrainer) {

        Trainer trainer = trainerRepo.findTrainerByUsername(updateTrainer.username())
                .orElseThrow(() -> new UserNotFoundException("Trainer not found with username: " + updateTrainer.username()));

        trainer.getUser().setUsername(updateTrainer.username());
        trainer.getUser().setFirstName(updateTrainer.firstName());
        trainer.getUser().setLastName(updateTrainer.lastName());
        trainer.getSpecialization().setTrainingTypeName(updateTrainer.specialization());

        Trainer updated = trainerRepo.update(trainer)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found with id: " + trainer.getId()));

        logger.info("Trainer updated successfully with ID: {}", trainer.getId());
        return updated;
    }

    @Transactional
    @Override
    public Trainer getTrainer(long trainerId) {
        Trainer trainer = trainerRepo.findById(trainerId)
                .orElseThrow(() -> {
                    logger.warn("Trainer with id: {} not found", trainerId);
                    return new UserNotFoundException("Trainer not found with id: " + trainerId);
                });

        logger.info("Trainer with id: {} successfully fetched", trainerId);
        return trainer;
    }


    @Transactional
    @Override
    public boolean existsById(long id) {
        return trainerRepo.existsById(id);
    }

    @Transactional
    @Override
    public Trainer getTrainerByUsername(String username) {
        return trainerRepo.findTrainerByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found with username: " + username));
    }

    @Transactional
    @Override
    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername) {
        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new IllegalArgumentException("Trainee username cannot be null or blank");
        }
        return trainerRepo.findAllNotAssignedToTrainee(traineeUsername);
    }
}