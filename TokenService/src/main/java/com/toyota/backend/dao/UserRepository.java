package com.toyota.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.toyota.backend.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * @author Arman Yaycı
 * @since 01.05.2023
 * This interface extends the JpaRepository interface
 * and defines methods for managing User data in the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User,Integer>
    {/**
     * Finds a User object by "username" in the database.
     * @param username the username of the User object to find
     * @return Optional object containing the found User object, or an empty Optional if not found
     */
    Optional<User> findByusername(String username);

}
