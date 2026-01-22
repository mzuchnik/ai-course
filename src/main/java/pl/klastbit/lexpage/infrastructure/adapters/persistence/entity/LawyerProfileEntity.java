package pl.klastbit.lexpage.infrastructure.adapters.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * JPA Entity for lawyer_profile table.
 * Singleton entity (only 1 record allowed by database constraint).
 */
@Entity
@Table(name = "lawyer_profile")
@Getter
@Setter
@NoArgsConstructor
public class LawyerProfileEntity extends BaseEntity {

    @Id
    @Column(name = "id")
    private Integer id = 1; // Singleton - always id = 1

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "bio", nullable = false, columnDefinition = "TEXT")
    private String bio;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "specializations", columnDefinition = "text[]")
    private String[] specializations;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "office_address", columnDefinition = "TEXT")
    private String officeAddress;

    @Column(name = "google_maps_url", length = 500)
    private String googleMapsUrl;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "bar_association_number", length = 50)
    private String barAssociationNumber;
}
