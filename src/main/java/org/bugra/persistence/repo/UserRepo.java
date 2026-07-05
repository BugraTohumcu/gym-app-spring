package org.bugra.persistence.repo;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>Abstract repository for {@link User} entities.</p>
 * Extends {@link AbstractRepository} and implements {@link UsernameCapable}.
 */

@Repository
public class UserRepo
        extends AbstractRepository<User, Long>
        implements UsernameCapable {

    public UserRepo() {
        super(User.class, User::getId);
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null) {
            return false;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<User> root = criteriaQuery.from(User.class);


        // Select the number of users in the result
        criteriaQuery.select(cb.count(root));

        // Select the users with the provided username
        criteriaQuery.where(cb.equal(root.get("username"), username));

        return entityManager.createQuery(criteriaQuery).getSingleResult() > 0;
    }

    @Override
    public List<String> findUsernameStartingWith(String baseName) {
        if(baseName == null){
            throw new IllegalArgumentException("Base name can not be null");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<User> root = cq.from(User.class);

        cq.select(root.get("username"));
        cq.where(cb.like(root.get("username"), baseName + "%"));


        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public User findByUsername(String username) {
        if(username == null) {
            throw new IllegalArgumentException("Username can not be null");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq =cb.createQuery(User.class);

        Root<User> root = cq.from(User.class);

        cq.select(root);
        cq.where(cb.equal(root.get("username"), username));

        return entityManager
                .createQuery(cq)
                .getResultStream()
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public boolean deleteByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username can not be null");
        }

        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.username = :username",
                User.class);
        query.setParameter("username", username);

        try {
            User user = query.getSingleResult();

            entityManager.remove(user);

            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

}
