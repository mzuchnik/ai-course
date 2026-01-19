package pl.klastbit.lexpage.infrastructure.adapters.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * JPA Entity for lawyer_profile table.
 * Singleton entity (only 1 record allowed by database constraint).
 */
@Entity
@Table(name = "lawyer_profile")
public class LawyerProfileEntity {

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String[] getSpecializations() {
        return specializations;
    }

    public void setSpecializations(String[] specializations) {
        this.specializations = specializations;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getGoogleMapsUrl() {
        return googleMapsUrl;
    }

    public void setGoogleMapsUrl(String googleMapsUrl) {
        this.googleMapsUrl = googleMapsUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getBarAssociationNumber() {
        return barAssociationNumber;
    }

    public void setBarAssociationNumber(String barAssociationNumber) {
        this.barAssociationNumber = barAssociationNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
