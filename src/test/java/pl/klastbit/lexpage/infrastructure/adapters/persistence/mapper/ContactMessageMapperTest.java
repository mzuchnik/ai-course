package pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.klastbit.lexpage.domain.contact.ContactMessage;
import pl.klastbit.lexpage.domain.contact.MessageCategory;
import pl.klastbit.lexpage.domain.contact.MessageStatus;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ContactMessageEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ContactMessageMapper Unit Tests")
class ContactMessageMapperTest {

    private ContactMessageMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ContactMessageMapper();
    }

    @Nested
    @DisplayName("toDomain() method")
    class ToDomainTests {

        @Test
        @DisplayName("should return null when entity is null")
        void shouldReturnNullWhenEntityIsNull() {
            // when
            ContactMessage result = mapper.toDomain(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields from entity to domain")
        void shouldMapAllFieldsFromEntityToDomain() {
            // given
            ContactMessageEntity entity = createFullContactMessageEntity();

            // when
            ContactMessage result = mapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getFirstName()).isEqualTo("John");
            assertThat(result.getLastName()).isEqualTo("Doe");
            assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
            assertThat(result.getPhone()).isEqualTo("+48123456789");
            assertThat(result.getCategory()).isEqualTo(pl.klastbit.lexpage.domain.contact.MessageCategory.GENERAL);
            assertThat(result.getMessage()).isEqualTo("Test message content");
            assertThat(result.getStatus()).isEqualTo(pl.klastbit.lexpage.domain.contact.MessageStatus.NEW);
            assertThat(result.getRecaptchaScore()).isEqualByComparingTo(new BigDecimal("0.9"));
            assertThat(result.getIpAddress()).isEqualTo("192.168.1.1");
            assertThat(result.getUserAgent()).isEqualTo("Mozilla/5.0");
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should handle null optional fields")
        void shouldHandleNullOptionalFields() {
            // given
            ContactMessageEntity entity = createMinimalContactMessageEntity();
            entity.setPhone(null);
            entity.setRecaptchaScore(null);
            entity.setIpAddress(null);
            entity.setUserAgent(null);

            // when
            ContactMessage result = mapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getPhone()).isNull();
            assertThat(result.getRecaptchaScore()).isNull();
            assertThat(result.getIpAddress()).isNull();
            assertThat(result.getUserAgent()).isNull();
        }

        @Test
        @DisplayName("should convert entity status to domain status")
        void shouldConvertEntityStatusToDomainStatus() {
            // given
            ContactMessageEntity entity = createMinimalContactMessageEntity();
            entity.setStatus(MessageStatus.READ);

            // when
            ContactMessage result = mapper.toDomain(entity);

            // then
            assertThat(result.getStatus()).isEqualTo(pl.klastbit.lexpage.domain.contact.MessageStatus.READ);
        }

        @Test
        @DisplayName("should convert entity category to domain category")
        void shouldConvertEntityCategoryToDomainCategory() {
            // given
            ContactMessageEntity entity = createMinimalContactMessageEntity();
            entity.setCategory(MessageCategory.CIVIL_LAW);

            // when
            ContactMessage result = mapper.toDomain(entity);

            // then
            assertThat(result.getCategory()).isEqualTo(pl.klastbit.lexpage.domain.contact.MessageCategory.CIVIL_LAW);
        }

        @Test
        @DisplayName("should handle null status")
        void shouldHandleNullStatus() {
            // given
            ContactMessageEntity entity = createMinimalContactMessageEntity();
            entity.setStatus(null);

            // when
            ContactMessage result = mapper.toDomain(entity);

            // then
            assertThat(result.getStatus()).isNull();
        }

        @Test
        @DisplayName("should handle null category")
        void shouldHandleNullCategory() {
            // given
            ContactMessageEntity entity = createMinimalContactMessageEntity();
            entity.setCategory(null);

            // when
            ContactMessage result = mapper.toDomain(entity);

            // then
            assertThat(result.getCategory()).isNull();
        }
    }

    @Nested
    @DisplayName("toEntity() method")
    class ToEntityTests {

        @Test
        @DisplayName("should return null when domain is null")
        void shouldReturnNullWhenDomainIsNull() {
            // when
            ContactMessageEntity result = mapper.toEntity(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields from domain to entity")
        void shouldMapAllFieldsFromDomainToEntity() {
            // given
            ContactMessage domain = createFullContactMessageDomain();

            // when
            ContactMessageEntity result = mapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getFirstName()).isEqualTo("John");
            assertThat(result.getLastName()).isEqualTo("Doe");
            assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
            assertThat(result.getPhone()).isEqualTo("+48123456789");
            assertThat(result.getCategory()).isEqualTo(MessageCategory.GENERAL);
            assertThat(result.getMessage()).isEqualTo("Test message content");
            assertThat(result.getStatus()).isEqualTo(MessageStatus.NEW);
            assertThat(result.getRecaptchaScore()).isEqualByComparingTo(new BigDecimal("0.9"));
            assertThat(result.getIpAddress()).isEqualTo("192.168.1.1");
            assertThat(result.getUserAgent()).isEqualTo("Mozilla/5.0");
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should handle null optional fields")
        void shouldHandleNullOptionalFields() {
            // given
            ContactMessage domain = ContactMessage.ofExisting(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                null,
                pl.klastbit.lexpage.domain.contact.MessageCategory.GENERAL,
                "Test message content",
                pl.klastbit.lexpage.domain.contact.MessageStatus.NEW,
                null,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
            );

            // when
            ContactMessageEntity result = mapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getPhone()).isNull();
            assertThat(result.getRecaptchaScore()).isNull();
            assertThat(result.getIpAddress()).isNull();
            assertThat(result.getUserAgent()).isNull();
        }

        @Test
        @DisplayName("should convert domain status to entity status")
        void shouldConvertDomainStatusToEntityStatus() {
            // given
            ContactMessage domain = ContactMessage.ofExisting(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                null,
                pl.klastbit.lexpage.domain.contact.MessageCategory.GENERAL,
                "Test message content",
                pl.klastbit.lexpage.domain.contact.MessageStatus.REPLIED,
                null,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
            );

            // when
            ContactMessageEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getStatus()).isEqualTo(MessageStatus.REPLIED);
        }

        @Test
        @DisplayName("should convert domain category to entity category")
        void shouldConvertDomainCategoryToEntityCategory() {
            // given
            ContactMessage domain = createMinimalContactMessageDomain();
            // Category can only be changed by creating a new instance
            ContactMessage domainWithDifferentCategory = ContactMessage.ofExisting(
                domain.getId(),
                domain.getFirstName(),
                domain.getLastName(),
                domain.getEmail(),
                domain.getPhone(),
                pl.klastbit.lexpage.domain.contact.MessageCategory.CRIMINAL_LAW,
                domain.getMessage(),
                domain.getStatus(),
                domain.getRecaptchaScore(),
                domain.getIpAddress(),
                domain.getUserAgent(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
            );

            // when
            ContactMessageEntity result = mapper.toEntity(domainWithDifferentCategory);

            // then
            assertThat(result.getCategory()).isEqualTo(MessageCategory.CRIMINAL_LAW);
        }

        @Test
        @DisplayName("should handle null status")
        void shouldHandleNullStatus() {
            // given
            ContactMessage domain = ContactMessage.ofExisting(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                null,
                pl.klastbit.lexpage.domain.contact.MessageCategory.GENERAL,
                "Test message content",
                null,
                null,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
            );

            // when
            ContactMessageEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getStatus()).isNull();
        }

        @Test
        @DisplayName("should handle null category")
        void shouldHandleNullCategory() {
            // given
            ContactMessage domain = ContactMessage.ofExisting(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                null,
                null,
                "Test message content",
                pl.klastbit.lexpage.domain.contact.MessageStatus.NEW,
                null,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
            );

            // when
            ContactMessageEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getCategory()).isNull();
        }
    }

    @Nested
    @DisplayName("updateEntity() method")
    class UpdateEntityTests {

        @Test
        @DisplayName("should do nothing when entity is null")
        void shouldDoNothingWhenEntityIsNull() {
            // given
            ContactMessage domain = createFullContactMessageDomain();

            // when/then - no exception should be thrown
            mapper.updateEntity(null, domain);
        }

        @Test
        @DisplayName("should do nothing when domain is null")
        void shouldDoNothingWhenDomainIsNull() {
            // given
            ContactMessageEntity entity = createFullContactMessageEntity();

            // when/then - no exception should be thrown
            mapper.updateEntity(entity, null);
        }

        @Test
        @DisplayName("should update all mutable fields from domain to entity")
        void shouldUpdateAllMutableFieldsFromDomainToEntity() {
            // given
            ContactMessageEntity entity = createFullContactMessageEntity();
            LocalDateTime newUpdatedAt = LocalDateTime.now().plusDays(1);

            ContactMessage domain = ContactMessage.ofExisting(
                1L,
                "Jane",
                "Smith",
                "jane.smith@example.com",
                "+48987654321",
                pl.klastbit.lexpage.domain.contact.MessageCategory.CRIMINAL_LAW,
                "Updated message content",
                pl.klastbit.lexpage.domain.contact.MessageStatus.READ,
                new BigDecimal("0.8"),
                "10.0.0.1",
                "Chrome/96.0",
                LocalDateTime.now(),
                newUpdatedAt
            );

            // when
            mapper.updateEntity(entity, domain);

            // then
            assertThat(entity.getFirstName()).isEqualTo("Jane");
            assertThat(entity.getLastName()).isEqualTo("Smith");
            assertThat(entity.getEmail()).isEqualTo("jane.smith@example.com");
            assertThat(entity.getPhone()).isEqualTo("+48987654321");
            assertThat(entity.getCategory()).isEqualTo(MessageCategory.CRIMINAL_LAW);
            assertThat(entity.getMessage()).isEqualTo("Updated message content");
            assertThat(entity.getStatus()).isEqualTo(MessageStatus.READ);
            assertThat(entity.getRecaptchaScore()).isEqualByComparingTo(new BigDecimal("0.8"));
            assertThat(entity.getIpAddress()).isEqualTo("10.0.0.1");
            assertThat(entity.getUserAgent()).isEqualTo("Chrome/96.0");
            assertThat(entity.getUpdatedAt()).isEqualTo(newUpdatedAt);
        }

        @Test
        @DisplayName("should preserve createdAt timestamp during update")
        void shouldPreserveCreatedAtTimestampDuringUpdate() {
            // given
            ContactMessageEntity entity = createFullContactMessageEntity();
            LocalDateTime originalCreatedAt = entity.getCreatedAt();

            ContactMessage domain = ContactMessage.ofExisting(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+48123456789",
                pl.klastbit.lexpage.domain.contact.MessageCategory.GENERAL,
                "Updated message",
                pl.klastbit.lexpage.domain.contact.MessageStatus.NEW,
                new BigDecimal("0.9"),
                "192.168.1.1",
                "Mozilla/5.0",
                LocalDateTime.now(),
                LocalDateTime.now()
            );

            // when
            mapper.updateEntity(entity, domain);

            // then - createdAt should not change
            assertThat(entity.getCreatedAt()).isEqualTo(originalCreatedAt);
        }
    }

    @Nested
    @DisplayName("Round-trip conversion")
    class RoundTripConversionTests {

        @Test
        @DisplayName("should maintain data integrity in entity -> domain -> entity conversion")
        void shouldMaintainDataIntegrityInEntityToDomainToEntityConversion() {
            // given
            ContactMessageEntity originalEntity = createFullContactMessageEntity();

            // when
            ContactMessage domain = mapper.toDomain(originalEntity);
            ContactMessageEntity resultEntity = mapper.toEntity(domain);

            // then
            assertThat(resultEntity.getId()).isEqualTo(originalEntity.getId());
            assertThat(resultEntity.getFirstName()).isEqualTo(originalEntity.getFirstName());
            assertThat(resultEntity.getLastName()).isEqualTo(originalEntity.getLastName());
            assertThat(resultEntity.getEmail()).isEqualTo(originalEntity.getEmail());
            assertThat(resultEntity.getPhone()).isEqualTo(originalEntity.getPhone());
            assertThat(resultEntity.getCategory()).isEqualTo(originalEntity.getCategory());
            assertThat(resultEntity.getMessage()).isEqualTo(originalEntity.getMessage());
            assertThat(resultEntity.getStatus()).isEqualTo(originalEntity.getStatus());
            assertThat(resultEntity.getRecaptchaScore()).isEqualByComparingTo(originalEntity.getRecaptchaScore());
            assertThat(resultEntity.getIpAddress()).isEqualTo(originalEntity.getIpAddress());
            assertThat(resultEntity.getUserAgent()).isEqualTo(originalEntity.getUserAgent());
        }

        @Test
        @DisplayName("should maintain data integrity in domain -> entity -> domain conversion")
        void shouldMaintainDataIntegrityInDomainToEntityToDomainConversion() {
            // given
            ContactMessage originalDomain = createFullContactMessageDomain();

            // when
            ContactMessageEntity entity = mapper.toEntity(originalDomain);
            ContactMessage resultDomain = mapper.toDomain(entity);

            // then
            assertThat(resultDomain.getId()).isEqualTo(originalDomain.getId());
            assertThat(resultDomain.getFirstName()).isEqualTo(originalDomain.getFirstName());
            assertThat(resultDomain.getLastName()).isEqualTo(originalDomain.getLastName());
            assertThat(resultDomain.getEmail()).isEqualTo(originalDomain.getEmail());
            assertThat(resultDomain.getPhone()).isEqualTo(originalDomain.getPhone());
            assertThat(resultDomain.getCategory()).isEqualTo(originalDomain.getCategory());
            assertThat(resultDomain.getMessage()).isEqualTo(originalDomain.getMessage());
            assertThat(resultDomain.getStatus()).isEqualTo(originalDomain.getStatus());
            assertThat(resultDomain.getRecaptchaScore()).isEqualByComparingTo(originalDomain.getRecaptchaScore());
            assertThat(resultDomain.getIpAddress()).isEqualTo(originalDomain.getIpAddress());
            assertThat(resultDomain.getUserAgent()).isEqualTo(originalDomain.getUserAgent());
        }
    }

    // Helper methods to create test objects

    private ContactMessageEntity createMinimalContactMessageEntity() {
        ContactMessageEntity entity = new ContactMessageEntity();
        entity.setId(1L);
        entity.setFirstName("John");
        entity.setLastName("Doe");
        entity.setEmail("john.doe@example.com");
        entity.setCategory(MessageCategory.GENERAL);
        entity.setMessage("Test message content");
        entity.setStatus(MessageStatus.NEW);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    private ContactMessageEntity createFullContactMessageEntity() {
        ContactMessageEntity entity = createMinimalContactMessageEntity();
        entity.setPhone("+48123456789");
        entity.setRecaptchaScore(new BigDecimal("0.9"));
        entity.setIpAddress("192.168.1.1");
        entity.setUserAgent("Mozilla/5.0");
        return entity;
    }

    private ContactMessage createMinimalContactMessageDomain() {
        return ContactMessage.ofExisting(
            1L,
            "John",
            "Doe",
            "john.doe@example.com",
            null,
            pl.klastbit.lexpage.domain.contact.MessageCategory.GENERAL,
            "Test message content",
            pl.klastbit.lexpage.domain.contact.MessageStatus.NEW,
            null,
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    private ContactMessage createFullContactMessageDomain() {
        return ContactMessage.ofExisting(
            1L,
            "John",
            "Doe",
            "john.doe@example.com",
            "+48123456789",
            pl.klastbit.lexpage.domain.contact.MessageCategory.GENERAL,
            "Test message content",
            pl.klastbit.lexpage.domain.contact.MessageStatus.NEW,
            new BigDecimal("0.9"),
            "192.168.1.1",
            "Mozilla/5.0",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
