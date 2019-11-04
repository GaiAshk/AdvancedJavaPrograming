package smarticulous.db;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * Holds the data for a single submission.
 */
public class Submission {
    /**
     * The submission id.
     * May be -1 if submission hasn't been inserted into DB yet.
     */
    public int id;

    /**
     * The submitting user.
     */
    public User user;

    /**
     * The associated exercise.
     */
    public Exercise exercise;

    /**
     * Time of submission.
     */
    public Date submissionTime;

    /**
     * The grades for the questions in this exercise.
     * (questionGrades[i] is the grade for question i)
     *
     * Grades are given in points  (e.g., if question 3 received 8.5 points out of 10,
     * then questionGrades[2] should be 8.5).
     */
    public float[] questionGrades;

    public Submission(int id, User user, Exercise exercise, Date submissionTime, float[] questionGrades) {
        this.id = id;
        this.user = user;
        this.exercise = exercise;
        this.submissionTime = submissionTime;
        this.questionGrades = questionGrades;
    }

    public Submission(User user, Exercise exercise, Date submissionTime, float[] questionGrades) {
        this(-1, user, exercise, submissionTime, questionGrades);
    }
}
