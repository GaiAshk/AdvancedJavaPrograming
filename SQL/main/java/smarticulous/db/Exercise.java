package smarticulous.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An exercise definition
 */
public class Exercise {

    public class Question {
        /**
         * Question name
         */
        public String name;

        /**
         * Question description
         */
        public String desc;

        /**
         * Maximum grade (in points) for this question.
         */
        public int points;

        public Question(String name, String desc, int points) {
            this.name = name;
            this.desc = desc;
            this.points = points;
        }
    }

    /**
     * The ID of this exercise.
     */
    public int id;

    /**
     * Exercise name.
     */
    public String name;

    /**
     * Exercise due-date
     */
    public Date dueDate;


    /**
     * Questions for this exercise.
     */
    public List<Question> questions;

    public Exercise(int id, String name, Date dueDate) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.questions = new ArrayList<>();
    }

    /**
     * Add a new question.
     * @param name
     * @param desc
     * @param points
     */
    public void addQuestion(String name, String desc, int points) {
        questions.add(new Question(name, desc, points));
    }
}
