package entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Nicklas Nielsen
 */
@Entity
@Table(name = "users")
@NamedQueries({
    @NamedQuery(name = "User.deleteAllRows", query = "DELETE FROM User"),
    @NamedQuery(name = "User.getAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.getByEmail", query = "SELECT u FROM User u WHERE UPPER(u.email) = :email"),
    @NamedQuery(name = "User.getByRoleAndCompany", query = "SELECT u FROM User u JOIN u.roles r JOIN u.company c WHERE u.deleted = null AND r.type = :role AND c.id = :company"),
    @NamedQuery(name = "User.getByManager", query = "SELECT u FROM User u JOIN u.managers m WHERE m.id = :manager")
})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne
    private Company company;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    List<InterviewQuestionAnswer> interviewQuestionAnswers;

    public void setActivated(LocalDateTime activated) {
        this.activated = activated;
    }

    @ManyToMany
    @JoinTable(name = "lk_users_roles", joinColumns = {
        @JoinColumn(name = "fk_user", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "fk_role", referencedColumnName = "id")
    })
    private List<Role> roles;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String hashedPassword;

    @OneToOne
    @JoinTable(name = "lk_user_person", joinColumns = {
        @JoinColumn(name = "fk_user", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "fk_person", referencedColumnName = "id")
    })
    private Person person;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "activated", nullable = true)
    private LocalDateTime activated;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "deleted", nullable = true)
    private LocalDateTime deleted;

    @Column(name = "recovery_code", nullable = true)
    private String recoveryCode;

    @Column(name = "recovery_code_expiration", nullable = true)
    private LocalDateTime recoveryCodeExpiration;

    @ManyToMany
    @JoinTable(name = "lk_managers_employees", joinColumns = {
        @JoinColumn(name = "fk_employee", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "fk_manager", referencedColumnName = "id")
    })
    private List<User> employees;

    @ManyToMany(mappedBy = "employees")
    private List<User> managers;

    @ManyToMany
    @JoinTable(name = "lk_managers_interviews", joinColumns = {
        @JoinColumn(name = "fk_manager", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "fk_interview", referencedColumnName = "id")
    })
    private List<Interview> managerInterviews;

    @ManyToMany
    @JoinTable(name = "lk_empployees_interviews", joinColumns = {
        @JoinColumn(name = "fk_employee", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "fk_interview", referencedColumnName = "id")
    })
    private List<Interview> employeeInterviews;

    public User(String email, String password) {
        id = UUID.randomUUID().toString();
        this.email = email;
        this.hashedPassword = hashPassword(password);

        roles = new ArrayList<>();
        created = LocalDateTime.now();

        managerInterviews = new ArrayList<>();
        employeeInterviews = new ArrayList<>();
        employees = new ArrayList<>();
        managers = new ArrayList<>();
        interviewQuestionAnswers = new ArrayList<>();

        activationCode = UUID.randomUUID().toString();
    }

    public User() {
        roles = new ArrayList<>();
        managerInterviews = new ArrayList<>();
        employeeInterviews = new ArrayList<>();
        employees = new ArrayList<>();
        managers = new ArrayList<>();
        interviewQuestionAnswers = new ArrayList<>();
    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        if (!roles.contains(role)) {
            roles.add(role);
            role.addUser(this);
        }
    }

    public void removeRole(Role role) {
        if (roles.contains(role)) {
            roles.remove(role);
            role.removeUser(this);
        }
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getFullname() {
        if (person == null) {
            return "unknown";
        }

        return person.getFullname();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean verifyPassword(String password) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    public void setPassword(String password) {
        this.hashedPassword = hashPassword(password);
    }

    private String hashPassword(String password) {
        int saltLogRounds = 10;
        String salt = BCrypt.gensalt(saltLogRounds);

        return BCrypt.hashpw(password, salt);
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getActivated() {
        return activated;
    }

    public void activate() {
        activated = LocalDateTime.now();
    }

    public void deactivate() {
        activated = null;
    }

    public boolean isActivated() {
        return activated != null;
    }

    public boolean verifyActivationCode(UUID code) {
        UUID activationCodeToVerify = UUID.fromString(this.activationCode);

        return activationCodeToVerify == code;
    }

    public UUID getActivationCode() {
        return UUID.fromString(activationCode);
    }

    public LocalDateTime getDeleted() {
        return deleted;
    }

    public boolean isDelated() {
        return deleted != null;
    }

    public boolean verifyRecoveryCode(UUID code) {
        if (recoveryCodeExpiration != null && recoveryCodeExpiration.isAfter(LocalDateTime.now())) {
            return UUID.fromString(recoveryCode).equals(code);
        }

        return false;
    }

    public boolean isRecoveryCodeActive() {
        if (recoveryCodeExpiration != null) {
            return recoveryCodeExpiration.isAfter(LocalDateTime.now());
        }

        return false;
    }

    public UUID getRecoveryCode() {
        return UUID.fromString(recoveryCode);
    }

    public LocalDateTime getRecoveryCodeExpiration() {
        return recoveryCodeExpiration;
    }

    public UUID requestRecoveryCode() {
        recoveryCodeExpiration = LocalDateTime.now().plusHours(3);
        recoveryCode = UUID.randomUUID().toString();

        return UUID.fromString(recoveryCode);
    }

    public void terminateRecoveryCode() {
        recoveryCode = null;
        recoveryCodeExpiration = null;
    }

    public boolean isActive() {
        return isActivated() && !isDelated();
    }

    public List<Interview> getManagerInterviews() {
        return managerInterviews;
    }

    public void addManagerInterview(Interview interview) {
        if (!managerInterviews.contains(interview)) {
            managerInterviews.add(interview);
            interview.addManager(this);
        }
    }

    public void removeManagerInterview(Interview interview) {
        if (managerInterviews.contains(interview)) {
            managerInterviews.remove(interview);
            interview.removeManager(this);
        }
    }

    public List<Interview> getEmployeeInterviews() {
        return employeeInterviews;
    }

    public void addEmployeeInterview(Interview interview) {
        if (!employeeInterviews.contains(interview)) {
            employeeInterviews.add(interview);
            interview.addEmployee(this);
        }
    }

    public void removeEmployeeInterview(Interview interview) {
        if (employeeInterviews.contains(interview)) {
            employeeInterviews.remove(interview);
            interview.removeEmployee(this);
        }
    }

    public List<User> getEmployees() {
        return employees;
    }

    public void addEmployee(User employee) {
        if (!employees.contains(employee)) {
            employees.add(employee);
            employee.addManager(this);
        }
    }

    public void removeEmployee(User employee) {
        if (employees.contains(employee)) {
            employees.remove(employee);
            employee.removeManager(this);
        }
    }

    public List<User> getManagers() {
        return managers;
    }

    public void addManager(User manager) {
        if (!managers.contains(manager)) {
            managers.add(manager);
            manager.addEmployee(this);
        }
    }

    public void removeManager(User manager) {
        if (managers.contains(manager)) {
            managers.remove(manager);
            manager.removeEmployee(this);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
