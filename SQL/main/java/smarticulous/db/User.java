package smarticulous.db;

/**
 * A Smarticulous user.
 */
public class User {
    public String username;
    public String firstname;
    public String lastname;

    // The password is stored only in the database!

    public User(String username, String firstname, String lastname) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}
