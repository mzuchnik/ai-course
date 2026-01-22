package pl.klastbit.lexpage.domain.contact;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ContactMessage Domain Entity (DDD Aggregate Root).
 * Encapsulates contact form message business logic including status management and spam detection.
 */
public class ContactMessage {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private MessageCategory category;
    private String message;
    private MessageStatus status;
    private BigDecimal recaptchaScore;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private static final BigDecimal SPAM_THRESHOLD = new BigDecimal("0.5");
    private static final int MIN_MESSAGE_LENGTH = 50;

    private ContactMessage() {}

    /**
     * Factory method to create a new contact message.
     */
    public static ContactMessage create(
            String firstName,
            String lastName,
            String email,
            String phone,
            MessageCategory category,
            String message,
            BigDecimal recaptchaScore,
            String ipAddress,
            String userAgent
    ) {
        ContactMessage contactMessage = new ContactMessage();
        contactMessage.firstName = Objects.requireNonNull(firstName, "First name cannot be null");
        contactMessage.lastName = Objects.requireNonNull(lastName, "Last name cannot be null");
        contactMessage.email = Objects.requireNonNull(email, "Email cannot be null");
        contactMessage.phone = phone;
        contactMessage.category = Objects.requireNonNull(category, "Category cannot be null");
        contactMessage.message = Objects.requireNonNull(message, "Message cannot be null");
        contactMessage.recaptchaScore = recaptchaScore;
        contactMessage.ipAddress = ipAddress;
        contactMessage.userAgent = userAgent;
        contactMessage.status = MessageStatus.NEW;
        contactMessage.createdAt = LocalDateTime.now();
        contactMessage.updatedAt = LocalDateTime.now();

        contactMessage.validateMessage();

        return contactMessage;
    }

    /**
     * Factory method to reconstruct an existing contact message from database.
     * Used by infrastructure layer mappers. No business validation applied.
     */
    public static ContactMessage ofExisting(
            Long id,
            String firstName,
            String lastName,
            String email,
            String phone,
            MessageCategory category,
            String message,
            MessageStatus status,
            BigDecimal recaptchaScore,
            String ipAddress,
            String userAgent,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        ContactMessage contactMessage = new ContactMessage();
        contactMessage.id = id;
        contactMessage.firstName = firstName;
        contactMessage.lastName = lastName;
        contactMessage.email = email;
        contactMessage.phone = phone;
        contactMessage.category = category;
        contactMessage.message = message;
        contactMessage.status = status;
        contactMessage.recaptchaScore = recaptchaScore;
        contactMessage.ipAddress = ipAddress;
        contactMessage.userAgent = userAgent;
        contactMessage.createdAt = createdAt;
        contactMessage.updatedAt = updatedAt;
        return contactMessage;
    }

    /**
     * Marks the message as read.
     */
    public void markAsRead() {
        if (status != MessageStatus.NEW) {
            throw new IllegalStateException("Only NEW messages can be marked as read");
        }
        this.status = MessageStatus.READ;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks the message as replied.
     */
    public void markAsReplied() {
        if (status == MessageStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot mark archived message as replied");
        }
        this.status = MessageStatus.REPLIED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Archives the message.
     */
    public void archive() {
        this.status = MessageStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if message is likely spam based on reCAPTCHA score.
     */
    public boolean isLikelySpam() {
        if (recaptchaScore == null) {
            return false; // If no score, assume not spam
        }
        return recaptchaScore.compareTo(SPAM_THRESHOLD) < 0;
    }

    /**
     * Checks if message is new (unread).
     */
    public boolean isNew() {
        return status == MessageStatus.NEW;
    }

    /**
     * Gets the full name of the sender.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Validates message requirements.
     */
    private void validateMessage() {
        if (message.length() < MIN_MESSAGE_LENGTH) {
            throw new IllegalArgumentException(
                "Message must be at least " + MIN_MESSAGE_LENGTH + " characters long"
            );
        }

        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    // Getters

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public MessageCategory getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public BigDecimal getRecaptchaScore() {
        return recaptchaScore;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
