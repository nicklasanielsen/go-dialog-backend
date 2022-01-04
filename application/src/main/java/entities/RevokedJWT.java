package entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Nicklas Nielsen
 */
@Entity
@Table(name = "revoked_jwts")
@NamedQueries({
    @NamedQuery(name = "RevokedJWT.deleteAllRows", query = "DELETE FROM RevokedJWT")
})
public class RevokedJWT implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "expire")
    private LocalDateTime expire;

    public RevokedJWT(String id, LocalDateTime expire) {
        this.id = id;
        this.expire = expire;
    }

    public RevokedJWT() {

    }

    public String getID() {
        return id;
    }

    public LocalDateTime getExpire() {
        return expire;
    }

    public void setExpire(LocalDateTime expire) {
        this.expire = expire;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.expire);
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
        final RevokedJWT other = (RevokedJWT) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.expire, other.expire)) {
            return false;
        }
        return true;
    }

}
