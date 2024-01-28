package de.trxsson.userapi.service;

import de.trxsson.userapi.entity.User;
import lombok.NonNull;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a service for managing user data in a database.
 */
public class UserService {

    /**
     * Represents a service for interacting with a database.
     * <p>
     * The DatabaseService class provides methods for establishing a connection to a database,
     * executing SQL queries, and closing the connection. It uses the JDBC API to interact with the database.
     *
     * @since 1.0
     */
    private final DatabaseService databaseService = new DatabaseService();

    /**
     * Represents a DateTimeFormatter for formatting birth dates.
     * <p>
     * The birthDateFormatter variable is an instance of the DateTimeFormatter class,
     * which is used to format birth dates in the format "yyyy-MM-dd".
     * <p>
     * Example usage:
     * <pre>{@code
     *     User user = new User();
     *     LocalDate dob = LocalDate.of(1990, 5, 15);
     *     String formattedDate = birthDateFormatter.format(dob);
     *     System.out.println(formattedDate); // prints "1990-05-15"
     * }</pre>
     *
     * @since 1.0
     */
    private final DateTimeFormatter birthDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Retrieves all users from the database.
     *
     * @return an array of User objects representing all users in the database
     * @throws RuntimeException if a SQLException occurs during the retrieval process
     *
     * @since 1.0
     */
    public User[] getAllUsers() {
        List<User> userList = new ArrayList<>();
        try (var resultSet = databaseService.query("SELECT * FROM `users`")) {
            while (resultSet.next()) {
                User user = new User(
                        UUID.fromString(resultSet.getString("id")),
                        resultSet.getString("name"),
                        LocalDate.parse(resultSet.getString("date_of_birth"), birthDateFormatter)
                );
                userList.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList.toArray(new User[0]);
    }

    /**
     * Creates a new user with the given name and date of birth.
     *
     * @param name         The name of the user.
     * @param dateOfBirth  The date of birth of the user.
     * @return The created User object.
     *
     * @since 1.0
     */
    public User createUser(@NonNull String name, @NonNull LocalDate dateOfBirth) {
        User user = new User(UUID.randomUUID(), name, dateOfBirth);
        databaseService.update("INSERT INTO `users` (id, name, date_of_birth) VALUES (?, ?, ?)",
                user.getId().toString(),
                user.getName(),
                user.getDateOfBirth().format(birthDateFormatter));
        return user;
    }

    /**
     * Retrieves a user from the database based on their ID.
     *
     * @param id the ID of the user to retrieve
     * @return a User object representing the user with the specified ID, or null if no user is found
     * @throws RuntimeException if a SQLException occurs during the retrieval process
     *
     * @since 1.0
     */
    public User getUserById(@NonNull UUID id) {
        try (var resultSet = databaseService.query("SELECT * FROM `users` WHERE id = ?", id.toString())) {
            if (resultSet.next()) {
                return new User(
                        UUID.fromString(resultSet.getString("id")),
                        resultSet.getString("name"),
                        LocalDate.parse(resultSet.getString("date_of_birth"), birthDateFormatter)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Updates a user in the database with the specified ID, name, and date of birth.
     *
     * @param id          the unique identifier of the user
     * @param name        the new name of the user
     * @param dateOfBirth the new date of birth of the user
     * @throws RuntimeException if a SQLException occurs during the update process
     *
     * @since 1.0
     */
    public void updateUser(@NonNull UUID id, @NonNull String name, @NonNull LocalDate dateOfBirth) {
        databaseService.update("UPDATE `users` SET name = ?, date_of_birth = ? WHERE id = ?",
                name,
                dateOfBirth.format(birthDateFormatter),
                id.toString());
    }

    /**
     * Deletes a user from the database based on their ID.
     *
     * @param id the ID of the user to delete
     * @throws RuntimeException if a SQLException occurs during the deletion process
     *
     * @since 1.0
     */
    public void deleteUser(@NonNull UUID id) {
        databaseService.update("DELETE FROM `users` WHERE id = ?", id.toString());
    }

}