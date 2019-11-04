package smarticulous;

import smarticulous.db.Exercise;
import smarticulous.db.Submission;
import smarticulous.db.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Smarticulous {

    /**
     * The connection to the underlying DB.
     * <p>
     * null if the db has not yet been opened.
     */
    Connection db;

    /**
     * Open the smarticulous.Smarticulous SQLite database.
     * <p>
     * This should open the database, creating a new one if necessary, and set the {@link #db} field
     * to the new connection.
     * <p>
     * The open method should make sure the database contains the following tables, creating them if necessary:
     * <p>
     * - A ``User`` table containing the following columns (with their types):
     * <p>
     * =========  =====================
     * Column          Type
     * =========  =====================
     * UserId     Integer (Primary Key)
     * Username   Text
     * Firstname  Text
     * Lastname   Text
     * Password   Text
     * =========  =====================
     * <p>
     * - An ``smarticulous.db.Exercise`` table containing the following columns:
     * <p>
     * ============  =====================
     * Column          Type
     * ============  =====================
     * ExerciseId    Integer (Primary Key)
     * Name          Text
     * DueDate       Integer
     * ============  =====================
     * <p>
     * - A ``Question`` table containing the following columns:
     * <p>
     * ============  =====================
     * Column          Type
     * ============  =====================
     * ExerciseId     Integer
     * QuestionId     Integer
     * Name           Text
     * Desc           Text
     * Points         Integer
     * ============  =====================
     * <p>
     * In this table the combination of ``ExerciseId``,``QuestionId`` together comprise the primary key.
     * <p>
     * - A ``smarticulous.db.Submission`` table containing the following columns:
     * <p>
     * ===============  =====================
     * Column             Type
     * ===============  =====================
     * SubmissionId      Integer (Primary Key)
     * UserId           Integer
     * ExerciseId        Integer
     * SubmissionTime    Integer
     * ===============  =====================
     * <p>
     * - A ``QuestionGrade`` table containing the following columns:
     * <p>
     * ===============  =====================
     * Column             Type
     * ===============  =====================
     * SubmissionId      Integer
     * QuestionId        Integer
     * Grade            Real
     * ===============  =====================
     * <p>
     * In this table the combination of ``SubmissionId``,``QuestionId`` together comprise the primary key.
     *
     * @param dburl The JDBC url of the database to open (will be of the form "jdbc:sqlite:...")
     * @return the new connection
     */
    public Connection openDB(String dburl) throws SQLException {
        // TODO: Implement
        return null;
    }


    /**
     * Close the DB if it is open.
     */
    public void closeDB() throws SQLException {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    // =========== User Management =============

    /**
     * Add a user to the database / modify an existing user.
     * <p>
     * Add the user to the database if they don't exist. If a user with user.username does exist,
     * update their password and firstname/lastname in the database.
     *
     * @param user
     * @return the userid.
     */
    public int addOrUpdateUser(User user, String password) throws SQLException {
        // TODO: Implement
        return -1;
    }


    /**
     * Verify a user's login credentials.
     *
     * @param username
     * @param password
     * @return true if the user exists in the database and the password matches; false otherwise.
     * <p>
     * Note: this is totally insecure. For real-life password checking, it's important to store only
     * a password hash
     * @see <a href="https://crackstation.net/hashing-security.htm">How to Hash Passwords Properly</a>
     */
    public boolean verifyLogin(String username, String password) throws SQLException {
        // TODO: Implement
        return false;
    }

    // =========== Exercise Management =============

    /**
     * Add an exercise to the database.
     *
     * @param exercise
     * @return the new exercise id, or -1 if an exercise with this id already existed in the database.
     */
    public int addExercise(Exercise exercise) throws SQLException {
        // TODO: Implement
        return -1;
    }


    /**
     * Return a list of all the exercises in the database.
     * <p>
     * The list should be sorted by exercise id.
     *
     * @return
     */
    public List<Exercise> loadExercises() throws SQLException {
        // TODO: Implement
        return null;
    }

    // ========== Submission Storage ===============

    /**
     * Store a submission in the database.
     * The id field of the submission will be ignored if it is -1.
     * <p>
     * Return -1 if the corresponding user doesn't exist in the database.
     *
     * @param submission
     * @return the submission id.
     */
    public int storeSubmission(Submission submission) throws SQLException {
        // TODO: Implement
        return -1;
    }


    // ============= Submission Query ===============


    /**
     * Return a prepared SQL statement that, when executed, will
     * return one row for every question of the latest submission for the given exercise by the given user.
     * <p>
     * The rows should be sorted by QuestionId, and each row should contain:
     * - A column named "SubmissionId" with the submission id.
     * - A column named "QuestionId" with the question id,
     * - A column named "Grade" with the grade for that question.
     * - A column named "SubmissionTime" with the time of submission.
     * <p>
     * Parameter 1 of the prepared statement will be set to the User's username, Parameter 2 to the Exercise Id, and
     * Parameter 3 to the number of questions in the given exercise.
     * <p>
     * This will be used by {@link #getLastSubmission(User, Exercise)}
     *
     * @return
     */
    PreparedStatement getLastSubmissionGradesStatement() throws SQLException {
        // TODO: Implement
        return null;
    }

    /**
     * Return a prepared SQL statement that, when executed, will
     * return one row for every question of the <i>best</i> submission for the given exercise by the given user.
     * The best submission is the one whose point total is maximal.
     * <p>
     * The rows should be sorted by QuestionId, and each row should contain:
     * - A column named "SubmissionId" with the submission id.
     * - A column named "QuestionId" with the question id,
     * - A column named "Grade" with the grade for that question.
     * - A column named "SubmissionTime" with the time of submission.
     * <p>
     * Parameter 1 of the prepared statement will be set to the User's username, Parameter 2 to the Exercise Id, and
     * Parameter 3 to the number of questions in the given exercise.
     * <p>
     * This will be used by {@link #getBestSubmission(User, Exercise)}
     *
     */
    PreparedStatement getBestSubmissionGradesStatement() throws SQLException {
        // TODO: Implement
        return null;
    }

    /**
     * Return a submission for the given exercise by the given user that satisfies
     * some condition (as defined by an SQL prepared statement).
     * <p>
     * The prepared statement should accept the user name as parameter 1, the exercise id as parameter 2 and a limit on the
     * number of rows returned as parameter 3, and return a row for each question corresponding to the submission, sorted by questionId.
     * <p>
     * Return null if the user has not submitted the exercise (or is not in the database).
     *
     * @param user
     * @param exercise
     * @return
     */
    Submission getSubmission(User user, Exercise exercise, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, user.username);
        stmt.setInt(2, exercise.id);
        stmt.setInt(3, exercise.questions.size());

        ResultSet res = stmt.executeQuery();

        boolean hasNext = res.next();
        if (!hasNext)
            return null;

        int sid = res.getInt("SubmissionId");
        Date submissionTime = new Date(res.getLong("SubmissionTime"));

        float[] grades = new float[exercise.questions.size()];

        for (int i = 0; hasNext; ++i, hasNext = res.next()) {
            grades[i] = res.getFloat("Grade");
        }

        return new Submission(sid, user, exercise, submissionTime, (float[]) grades);
    }

    /**
     * Return the latest submission for the given exercise by the given user.
     * <p>
     * Return null if the user has not submitted the exercise (or is not in the database).
     *
     * @param user
     * @param exercise
     * @return
     */
    public Submission getLastSubmission(User user, Exercise exercise) throws SQLException {
        return getSubmission(user, exercise, getLastSubmissionGradesStatement());
    }


    /**
     * Return the submission with the highest total grade
     *
     * @param user
     * @param exercise
     * @return
     */
    public Submission getBestSubmission(User user, Exercise exercise) throws SQLException {
        return getSubmission(user, exercise, getBestSubmissionGradesStatement());
    }
}
