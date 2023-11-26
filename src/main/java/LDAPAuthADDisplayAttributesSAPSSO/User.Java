import org.springframework.ldap.odm.annotations.*;

@Entry(base = "ou=people", objectClasses = { "person" })
public class User {
    @Id
    private Name id;

    @Attribute(name = "cn")
    private String username;

    @Attribute(name = "givenName")
    private String firstName;

    @Attribute(name = "sn")
    private String lastName;

    // Getters and setters
}
