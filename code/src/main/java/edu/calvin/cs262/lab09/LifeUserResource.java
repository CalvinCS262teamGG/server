package edu.calvin.cs262.teamgg;

import com.google.api.server.spi.config.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.google.api.server.spi.config.ApiMethod.HttpMethod.GET;
import static com.google.api.server.spi.config.ApiMethod.HttpMethod.PUT;
import static com.google.api.server.spi.config.ApiMethod.HttpMethod.POST;
import static com.google.api.server.spi.config.ApiMethod.HttpMethod.DELETE;

/**
 * This Java annotation specifies the general configuration of the Google Cloud endpoint API.
 * The name and version are used in the URL: https://PROJECT_ID.appspot.com/monopoly/v1/ENDPOINT.
 * The namespace specifies the Java package in which to find the API implementation.
 * The issuers specifies boilerplate security features that we won't address in this course.
 *
 * You should configure the name and namespace appropriately.
 */
@Api(
        name = "lifemanager",
        version = "v1",
        namespace =
        @ApiNamespace(
                ownerDomain = "teamgg.cs262.calvin.edu",
                ownerName = "teamgg.cs262.calvin.edu",
                packagePath = ""
        ),
        issuers = {
                @ApiIssuer(
                        name = "firebase",
                        issuer = "https://securetoken.google.com/calvincs262-fall2018-teamgg",
                        jwksUri =
                                "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system"
                                        + ".gserviceaccount.com"
                )
        }
)

/**
 * This class implements a RESTful service for the LifeUser table of the monopoly database.
 * Only the LifeUser table is supported, not the game or Event tables.
 *
 * You can test the GET endpoints using a standard browser or cURL.
 *
 * % curl --request GET \
 *    https://calvincs262-monopoly.appspot.com/monopoly/v1/players
 *
 * % curl --request GET \
 *    https://calvincs262-monopoly.appspot.com/monopoly/v1/player/1
 *
 * You can test the full REST API using the following sequence of cURL commands (on Linux):
 * (Run get-players between each command to see the results.)
 *
 * // Add a new player (probably as unique generated ID #4).
 * % curl --request POST \
 *    --header "Content-Type: application/json" \
 *    --data '{"name":"test name...", "emailAddress":"test email..."}' \
 *    https://calvincs262-monopoly.appspot.com/monopoly/v1/player
 *
 * // Edit the new player (assuming ID #4).
 * % curl --request PUT \
 *    --header "Content-Type: application/json" \
 *    --data '{"name":"new test name...", "emailAddress":"new test email..."}' \
 *    https://calvincs262-monopoly.appspot.com/monopoly/v1/player/4
 *
 * // Delete the new player (assuming ID #4).
 * % curl --request DELETE \
 *    https://calvincs262-monopoly.appspot.com/monopoly/v1/player/4
 *
 */
public class PlayerResource {

    /**
     * GET
     * This method gets the full list of LifeUsers from the LifeUser table.
     *
     * @return JSON-formatted list of LifeUser records (based on a root JSON tag of "items")
     * @throws SQLException
     */
    @ApiMethod(path="lifeusers", httpMethod=GET)
    public List<LifeUser> getLifeUsers() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<LifeUser> result = new ArrayList<LifeUser>();
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            resultSet = selectLifeUsers(statement);
            while (resultSet.next()) {
                LifeUser p = new LifeUser(
                        Integer.parseInt(resultSet.getString(1)),
                        resultSet.getString(2),
                        resultSet.getString(3)
                );
                result.add(p);
            }
        } catch (SQLException e) {
            throw(e);
        } finally {
            if (resultSet != null) { resultSet.close(); }
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
        return result;
    }

    /**
     * GET
     * This method gets the LifeUser from the LifeUser table with the given ID.
     *
     * @param id the ID of the requested LifeUser
     * @return if the LifeUser exists, a JSON-formatted LifeUser record, otherwise an invalid/empty JSON entity
     * @throws SQLException
     */
    @ApiMethod(path="lifeuser/{id}", httpMethod=GET)
    public LifeUser getLifeUser(@Named("id") int id) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        LifeUser result = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            resultSet = selectLifeUser(id, statement);
            if (resultSet.next()) {
                result = new LifeUser(
                        Integer.parseInt(resultSet.getString(1)),
                        resultSet.getString(2),
                        resultSet.getString(3)
                );
            }
        } catch (SQLException e) {
            throw(e);
        } finally {
            if (resultSet != null) { resultSet.close(); }
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
        return result;
    }

    /**
     * PUT
     * This method creates/updates an instance of Person with a given ID.
     * If the LifeUser doesn't exist, create a new LifeUser using the given field values.
     * If the LifeUser already exists, update the fields using the new LifeUser field values.
     * We do this because PUT is idempotent, meaning that running the same PUT several
     * times is the same as running it exactly once.
     * Any LifeUser ID value set in the passed LifeUser data is ignored.
     *
     * @param id     the ID for the LifeUser, assumed to be unique
     * @param LifeUser a JSON representation of the LifeUser; The id parameter overrides any id specified here.
     * @return new/updated LifeUser entity
     * @throws SQLException
     */
    @ApiMethod(path="lifeuser/{id}", httpMethod=PUT)
    public LifeUser putLifeUser(LifeUser user, @Named("id") int id) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            user.setId(id);
            resultSet = selectLifeUser(id, statement);
            if (resultSet.next()) {
                updateLifeUser(user, statement);
            } else {
                insertLifeUser(user, statement);
            }
        } catch (SQLException e) {
            throw (e);
        } finally {
            if (resultSet != null) { resultSet.close(); }
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
        return user;
    }

    /**
     * POST
     * This method creates an instance of Person with a new, unique ID
     * number. We do this because POST is not idempotent, meaning that running
     * the same POST several times creates multiple objects with unique IDs but
     * otherwise having the same field values.
     *
     * The method creates a new, unique ID by querying the LifeUser table for the
     * largest ID and adding 1 to that. Using a DB sequence would be a better solution.
     * This method creates an instance of Person with a new, unique ID.
     *
     * @param LifeUser a JSON representation of the LifeUser to be created
     * @return new LifeUser entity with a system-generated ID
     * @throws SQLException
     */
    @ApiMethod(path="lifeuser", httpMethod=POST)
    public LifeUser postLifeUser(LifeUser user) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT MAX(ID) FROM LifeUser");
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1) + 1);
            } else {
                throw new RuntimeException("failed to find unique ID...");
            }
            insertLifeUser(user, statement);
        } catch (SQLException e) {
            throw (e);
        } finally {
            if (resultSet != null) { resultSet.close(); }
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
        return user;
    }

    /**
     * DELETE
     * This method deletes the instance of Person with a given ID, if it exists.
     * If the LifeUser with the given ID doesn't exist, SQL won't delete anything.
     * This makes DELETE idempotent.
     *
     * @param id     the ID for the LifeUser, assumed to be unique
     * @return the deleted LifeUser, if any
     * @throws SQLException
     */
    @ApiMethod(path="lifeuser/{id}", httpMethod=DELETE)
    public void deleteLifeUser(@Named("id") int id) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            deleteLifeUser(id, statement);
        } catch (SQLException e) {
            throw (e);
        } finally {
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
    }

    /** SQL Utility Functions *********************************************/

    /*
     * This function gets the LifeUser with the given id using the given JDBC statement.
     */
    private ResultSet selectLifeUser(int id, Statement statement) throws SQLException {
        return statement.executeQuery(
                String.format("SELECT * FROM LifeUser WHERE id=%d", id)
        );
    }

    /*
     * This function gets the LifeUser with the given id using the given JDBC statement.
     */
    private ResultSet selectLifeUsers(Statement statement) throws SQLException {
        return statement.executeQuery(
                "SELECT * FROM LifeUser"
        );
    }

    /*
     * This function modifies the given LifeUser using the given JDBC statement.
     */
    private void updateLifeUser(LifeUser user, Statement statement) throws SQLException {
        statement.executeUpdate(
                String.format("UPDATE LifeUser SET emailAddress='%s', name=%s WHERE id=%d",
                        user.getEmailAddress(),
                        getValueStringOrNull(user.getName()),
                        user.getId()
                )
        );
    }

    /*
     * This function inserts the given LifeUser using the given JDBC statement.
     */
    private void insertLifeUser(LifeUser user, Statement statement) throws SQLException {
        statement.executeUpdate(
                String.format("INSERT INTO LifeUser VALUES (%d, '%s', %s)",
                        user.getId(),
                        user.getEmailAddress(),
                        getValueStringOrNull(user.getName())
                )
        );
    }

    /*
     * This function gets the LifeUser with the given id using the given JDBC statement.
     */
    private void deleteLifeUser(int id, Statement statement) throws SQLException {
        statement.executeUpdate(
                String.format("DELETE FROM LifeUser WHERE id=%d", id)
        );
    }

    /*
     * This function returns a value literal suitable for an SQL INSERT/UPDATE command.
     * If the value is NULL, it returns an unquoted NULL, otherwise it returns the quoted value.
     */
    private String getValueStringOrNull(String value) {
        if (value == null) {
            return "NULL";
        } else {
            return "'" + value + "'";
        }
    }

}
