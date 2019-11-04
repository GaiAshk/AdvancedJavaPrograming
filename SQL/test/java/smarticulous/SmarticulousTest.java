package smarticulous;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import smarticulous.db.Exercise;
import smarticulous.db.Submission;
import smarticulous.db.User;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class SmarticulousTest {
    Random rand = new Random();
    DBUtil db = new DBUtil(1);
    File tmpdb = null;

    Smarticulous smarticulous = new Smarticulous();

    String getRandomString(int len) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < len; ++i) {
            s.append(Character.toString((char) (rand.nextInt('z' - 'a') + 'a')));
        }
        return s.toString();
    }

    @Before
    public void setUp() throws Exception {
        tmpdb = db.open(null);
        db.fillRandomDB();
    }

    @After
    public void tearDown() throws Exception {
        db.close();
        if (tmpdb != null)
            tmpdb.delete();
    }

    /**
     * Test opening an existing database.
     */
    @Test
    public void create_openDB() {
        try {
            smarticulous.openDB(db.getDbUrl());
            smarticulous.closeDB();
        } catch (Exception e) {
            fail("openDB threw an exception when opening an existing file DB: " + e);
        }

        // All is good!
    }

    /**
     * Check that openDB can create a database even if it doesn't exist.
     */
    @Test
    public void create_createNewDB() throws Exception  {
        if (tmpdb != null)
            tmpdb.delete();

        tmpdb = File.createTempFile("testCreateDB", "sqlite");
        tmpdb.delete();
        try {
            smarticulous.openDB(db.convertFileToURL(tmpdb));
            smarticulous.closeDB();
        } finally {
            if (tmpdb != null)
                tmpdb.delete();
        }
    }

    /**
     * Test creating tables in an existing but empty database.
     */
    @Test
    public void create_createTables() throws Exception {
        db.close();
        if (tmpdb != null)
            tmpdb.delete();

        tmpdb = db.open(null);
        try {
            smarticulous.openDB(db.getDbUrl());

            db.checkTableStructure();

            smarticulous.closeDB();
        } catch (Exception e) {
            fail("openDB threw an exception when trying to create tables: " + e);
        }
    }

    @Test
    public void user_addUser() {
        try {

            smarticulous.openDB(db.getDbUrl());

            String username = getRandomString(10); // Make sure it isn't already in the db w.h.p.
            User user = new User(username, db.getRandomWord(), db.getRandomWord());
            String pass = getRandomString(10);

            int id = smarticulous.addOrUpdateUser(user, pass);
            db.checkUser(id, user, pass);

            smarticulous.closeDB();
        } catch (Exception e) {
            fail("openDB threw an exception when attempting to add a user: " + e);
        }
    }

    @Test
    public void user_updateUser() throws Exception{
        User user = db.getUser(1);
        String pass = getRandomString(10);

        smarticulous.openDB(db.getDbUrl());

        int id = smarticulous.addOrUpdateUser(user, pass);
        db.checkUser(id, user, pass);

        smarticulous.closeDB();
    }

    @Test
    public void user_verifyLogin() throws Exception {
        int userId = rand.nextInt(db.getNumUsers());
        User user = db.getUser(userId);
        String pass = db.getPassword(userId);

        smarticulous.openDB(db.getDbUrl());

        assertTrue("Your code rejects a valid user!", smarticulous.verifyLogin(user.username, pass));

        assertFalse("Your code accepts a valid user with a bad password!",
                smarticulous.verifyLogin(user.username, getRandomString(10)));

        assertFalse("Your code accepts an invalid user!",
                smarticulous.verifyLogin(getRandomString(10), getRandomString(10)));

        assertFalse("Your code appears to be vulnerable to SQL injection!",
                smarticulous.verifyLogin(getRandomString(10), "' OR '1'='1"));

        assertFalse("Your code appears to be vulnerable to SQL injection!",
                smarticulous.verifyLogin("' OR UserId=1 OR Username='", "Lorem"));

        smarticulous.closeDB();
    }

    private Exercise createRandomExercise() throws Exception {
        int id = db.getNumExercises() + 1;
        String name = db.getRandomWord();
        Date dueDate = new Date(System.currentTimeMillis() + rand.nextInt(60*60*24*1000));

        Exercise e = new Exercise(id, name, dueDate);
        int numQuestions = rand.nextInt(10) + 3; // between 3 and 12 questions

        for (int i = 0; i < numQuestions; ++i) {
            e.addQuestion(db.getRandomWord(), db.getRandomDesc(), rand.nextInt(30) + 5);
        }
        return e;
    }

    private Submission createRandomSubmission() throws Exception {
        int exid = rand.nextInt(db.getNumExercises()) + 1;
        int uid = rand.nextInt(db.getNumUsers()) + 1;

        User user = db.getUser(uid);
        Exercise ex = db.getExercise(exid);

        float[] grades = new float[ex.questions.size()];
        for (int i = 0; i < grades.length; ++i)
            grades[i] = rand.nextFloat();

        return new Submission(user, ex, new Date(System.currentTimeMillis() - rand.nextInt(60*60*24*1000)), grades);
    }

    @Test
    public void exercise_addExercise() throws Exception {
        Exercise ex = createRandomExercise();

        smarticulous.openDB(db.getDbUrl());
        smarticulous.addExercise(ex);

        db.checkExercise(ex);

        smarticulous.closeDB();
    }

    @Test
    public void exercise_loadExercises() throws Exception {
        smarticulous.openDB(db.getDbUrl());

        List<Exercise> exs = smarticulous.loadExercises();

        assertEquals("You didn't return all the exercises!", db.getNumExercises(), exs.size());

        int i = 1;
        for (Exercise ex : exs) {
            assertEquals("Exercises are not sorted by id", i++, ex.id);
            db.checkExercise(ex);
        }

        smarticulous.closeDB();
    }

    @Test
    public void submission_storeSubmission() throws Exception  {
        smarticulous.openDB(db.getDbUrl());


        Submission sub = createRandomSubmission();
        sub.id = smarticulous.storeSubmission(sub);

        db.checkSubmission(sub);

        smarticulous.closeDB();
    }

    @Test
    public void submission_getLastSubmissionStatement() throws Exception  {
        smarticulous.openDB(db.getDbUrl());

        PreparedStatement st = smarticulous.getLastSubmissionGradesStatement();
        assertNotNull("Not implemented", st);

        db.checkLastSubmissionStatement(st);

        st.close();
        smarticulous.closeDB();
    }

    @Test
    public void submission_getLastSubmissionStatement_withGetSubmission() throws Exception  {
        smarticulous.openDB(db.getDbUrl());

        PreparedStatement st = smarticulous.getLastSubmissionGradesStatement();
        assertNotNull("Not implemented", st);

        List<DBUtil.MultiSubmissions> candidates = db.getSubmissionSortingCandidates(3);

        DBUtil.MultiSubmissions test = candidates.get(0);
        User user = db.getUser(test.getUid());
        Exercise ex = db.getExercise(test.getEid());

        Submission expected = test.getSubs().get(0);
        for (Submission s : test.getSubs()) {
            if (s.submissionTime.after(expected.submissionTime))
                expected = s;
        }

        Submission actual = smarticulous.getSubmission(user, ex, st);

        assertEquals("Wrong submission returned", expected.id, actual.id);

        st.close();
        smarticulous.closeDB();
    }

    @Test
    public void getBestSubmissionStatement()  throws Exception {
        smarticulous.openDB(db.getDbUrl());

        PreparedStatement st = smarticulous.getBestSubmissionGradesStatement();
        assertNotNull("Not implemented", st);

        db.checkBestSubmissionStatement(st);

        st.close();
        smarticulous.closeDB();
    }
}