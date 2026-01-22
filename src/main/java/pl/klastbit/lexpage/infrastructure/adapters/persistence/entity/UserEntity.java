package pl.klastbit.lexpage.infrastructure.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * JPA Entity for users table (Spring Security authentication).
 * Maps to the database users table in the infrastructure layer.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;
}
