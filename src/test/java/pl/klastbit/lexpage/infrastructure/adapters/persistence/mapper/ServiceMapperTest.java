package pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.klastbit.lexpage.domain.service.FaqItem;
import pl.klastbit.lexpage.domain.service.Service;
import pl.klastbit.lexpage.domain.service.ServiceCategory;
import pl.klastbit.lexpage.domain.user.UserId;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ServiceEntity;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ServiceMapper Unit Tests")
class ServiceMapperTest {

    private ServiceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ServiceMapper();
    }

    @Nested
    @DisplayName("toDomain() method")
    class ToDomainTests {

        @Test
        @DisplayName("should return null when entity is null")
        void shouldReturnNullWhenEntityIsNull() {
            // when
            Service result = mapper.toDomain(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields from entity to domain")
        void shouldMapAllFieldsFromEntityToDomain() {
            // given
            ServiceEntity entity = createFullServiceEntity();

            // when
            Service result = mapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Test Service");
            assertThat(result.getSlug()).isEqualTo("test-service");
            assertThat(result.getDescription()).isEqualTo("Test description");
            assertThat(result.getCategory()).isEqualTo(pl.klastbit.lexpage.domain.service.ServiceCategory.CIVIL_LAW);
            assertThat(result.getScope()).isEqualTo("Test scope");
            assertThat(result.getProcess()).isEqualTo("Test process");
            assertThat(result.getDisplayOrder()).isEqualTo(1);
            assertThat(result.getMetaTitle()).isEqualTo("Meta Title");
            assertThat(result.getMetaDescription()).isEqualTo("Meta Description");
            assertThat(result.getOgImageUrl()).isEqualTo("https://example.com/image.jpg");
            assertThat(result.getKeywords()).containsExactly("legal", "service");
            assertThat(result.getCreatedBy()).isEqualTo(UserId.of(entity.getCreatedBy().getId()));
            assertThat(result.getUpdatedBy()).isEqualTo(UserId.of(entity.getUpdatedBy().getId()));
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getUpdatedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("should parse FAQ JSON to FAQ items list")
        void shouldParseFaqJsonToFaqItemsList() {
            // given
            ServiceEntity entity = createMinimalServiceEntity();
            String faqJson = "[{\"question\":\"Q1\",\"answer\":\"A1\"},{\"question\":\"Q2\",\"answer\":\"A2\"}]";
            entity.setFaq(faqJson);

            // when
            Service result = mapper.toDomain(entity);

            // then
            assertThat(result.getFaqItems())
                    .isNotNull()
                    .hasSize(2);
            assertThat(result.getFaqItems().get(0).question()).isEqualTo("Q1");
            assertThat(result.getFaqItems().get(0).answer()).isEqualTo("A1");
            assertThat(result.getFaqItems().get(1).question()).isEqualTo("Q2");
            assertThat(result.getFaqItems().get(1).answer()).isEqualTo("A2");
        }

        @Test
        @DisplayName("should handle FAQ JSON with escaped characters")
        void shouldHandleFaqJsonWithEscapedCharacters() {
            // given
            ServiceEntity entity = createMinimalServiceEntity();
            String faqJson = "[{\"question\":\"What\\\"s this?\",\"answer\":\"It\\\"s a test\\nwith newline\"}]";
            entity.setFaq(faqJson);

            // when
            Service result = mapper.toDomain(entity);

            // then
            assertThat(result.getFaqItems())
                    .isNotNull()
                    .hasSize(1);
            assertThat(result.getFaqItems().get(0).question()).isEqualTo("What\"s this?");
            assertThat(result.getFaqItems().get(0).answer()).isEqualTo("It\"s a test\nwith newline");
        }

        @Test
        @DisplayName("should return empty list when FAQ JSON is null")
        void shouldReturnEmptyListWhenFaqJsonIsNull() {
            // given
            ServiceEntity entity = createMinimalServiceEntity();
            entity.setFaq(null);

            // when
            Service result = mapper.toDomain(entity);

            // then
            assertThat(result.getFaqItems()).isEmpty();
        }

        @Test
        @DisplayName("should return empty list when FAQ JSON is empty")
        void shouldReturnEmptyListWhenFaqJsonIsEmpty() {
            // given
            ServiceEntity entity = createMinimalServiceEntity();
            entity.setFaq("  ");

            // when
            Service result = mapper.toDomain(entity);

            // then
            assertThat(result.getFaqItems()).isEmpty();
        }

        @Test
        @DisplayName("should handle null audit references")
        void shouldHandleNullAuditReferences() {
            // given
            ServiceEntity entity = createMinimalServiceEntity();
            entity.setCreatedBy(null);
            entity.setUpdatedBy(null);

            // when
            Service result = mapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCreatedBy()).isNull();
            assertThat(result.getUpdatedBy()).isNull();
        }

        @Test
        @DisplayName("should convert keywords array to list")
        void shouldConvertKeywordsArrayToList() {
            // given
            ServiceEntity entity = createMinimalServiceEntity();
            entity.setKeywords(new String[]{"keyword1", "keyword2", "keyword3"});

            // when
            Service result = mapper.toDomain(entity);

            // then
            assertThat(result.getKeywords())
                    .isNotNull()
                    .hasSize(3)
                    .containsExactly("keyword1", "keyword2", "keyword3");
        }

        @Test
        @DisplayName("should handle null keywords array")
        void shouldHandleNullKeywordsArray() {
            // given
            ServiceEntity entity = createMinimalServiceEntity();
            entity.setKeywords(null);

            // when
            Service result = mapper.toDomain(entity);

            // then
            assertThat(result.getKeywords()).isNull();
        }
    }

    @Nested
    @DisplayName("toEntity() method")
    class ToEntityTests {

        @Test
        @DisplayName("should return null when domain is null")
        void shouldReturnNullWhenDomainIsNull() {
            // when
            ServiceEntity result = mapper.toEntity(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields from domain to entity")
        void shouldMapAllFieldsFromDomainToEntity() {
            // given
            Service domain = createFullServiceDomain();

            // when
            ServiceEntity result = mapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Test Service");
            assertThat(result.getSlug()).isEqualTo("test-service");
            assertThat(result.getDescription()).isEqualTo("Test description");
            assertThat(result.getCategory()).isEqualTo(ServiceCategory.CIVIL_LAW);
            assertThat(result.getScope()).isEqualTo("Test scope");
            assertThat(result.getProcess()).isEqualTo("Test process");
            assertThat(result.getDisplayOrder()).isEqualTo(1);
            assertThat(result.getMetaTitle()).isEqualTo("Meta Title");
            assertThat(result.getMetaDescription()).isEqualTo("Meta Description");
            assertThat(result.getOgImageUrl()).isEqualTo("https://example.com/image.jpg");
            assertThat(result.getKeywords()).containsExactly("legal", "service");
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getUpdatedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("should convert FAQ items list to JSON")
        void shouldConvertFaqItemsListToJson() {
            // given
            Service domain = Service.ofExisting(
                    1L,
                    "Test Service",
                    "test-service",
                    "Test description",
                    pl.klastbit.lexpage.domain.service.ServiceCategory.CIVIL_LAW,
                    null,
                    null,
                    Arrays.asList(
                            new FaqItem("Question 1", "Answer 1"),
                            new FaqItem("Question 2", "Answer 2")
                    ),
                    1,
                    null,
                    null,
                    null,
                    null,
                    UserId.createNew(),
                    UserId.createNew(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null
            );

            // when
            ServiceEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getFaq())
                    .isNotNull()
                    .contains("\"question\":\"Question 1\"")
                    .contains("\"answer\":\"Answer 1\"")
                    .contains("\"question\":\"Question 2\"")
                    .contains("\"answer\":\"Answer 2\"");
        }

        @Test
        @DisplayName("should escape special characters in FAQ JSON")
        void shouldEscapeSpecialCharactersInFaqJson() {
            // given
            Service domain = Service.ofExisting(
                    1L,
                    "Test Service",
                    "test-service",
                    "Test description",
                    pl.klastbit.lexpage.domain.service.ServiceCategory.CIVIL_LAW,
                    null,
                    null,
                    Arrays.asList(
                            new FaqItem("What\"s this?", "It\"s a test\nwith newline")
                    ),
                    1,
                    null,
                    null,
                    null,
                    null,
                    UserId.createNew(),
                    UserId.createNew(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null
            );

            // when
            ServiceEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getFaq())
                    .isNotNull()
                    .contains("\\\"")
                    .contains("\\n");
        }

        @Test
        @DisplayName("should return null when FAQ items list is null")
        void shouldReturnNullWhenFaqItemsListIsNull() {
            // given
            Service domain = Service.ofExisting(
                    1L,
                    "Test Service",
                    "test-service",
                    "Test description",
                    pl.klastbit.lexpage.domain.service.ServiceCategory.CIVIL_LAW,
                    null,
                    null,
                    null,
                    1,
                    null,
                    null,
                    null,
                    null,
                    UserId.createNew(),
                    UserId.createNew(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null
            );

            // when
            ServiceEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getFaq()).isNull();
        }

        @Test
        @DisplayName("should return null when FAQ items list is empty")
        void shouldReturnNullWhenFaqItemsListIsEmpty() {
            // given
            Service domain = Service.ofExisting(
                    1L,
                    "Test Service",
                    "test-service",
                    "Test description",
                    pl.klastbit.lexpage.domain.service.ServiceCategory.CIVIL_LAW,
                    null,
                    null,
                    Collections.emptyList(),
                    1,
                    null,
                    null,
                    null,
                    null,
                    UserId.createNew(),
                    UserId.createNew(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null
            );

            // when
            ServiceEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getFaq()).isNull();
        }

        @Test
        @DisplayName("should not set user entity references")
        void shouldNotSetUserEntityReferences() {
            // given
            Service domain = createFullServiceDomain();

            // when
            ServiceEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getCreatedBy()).isNull();
            assertThat(result.getUpdatedBy()).isNull();
        }

        @Test
        @DisplayName("should convert keywords list to array")
        void shouldConvertKeywordsListToArray() {
            // given
            Service domain = Service.ofExisting(
                    1L,
                    "Test Service",
                    "test-service",
                    "Test description",
                    pl.klastbit.lexpage.domain.service.ServiceCategory.CIVIL_LAW,
                    null,
                    null,
                    Collections.emptyList(),
                    1,
                    null,
                    null,
                    null,
                    Arrays.asList("keyword1", "keyword2"),
                    UserId.createNew(),
                    UserId.createNew(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null
            );

            // when
            ServiceEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getKeywords())
                    .isNotNull()
                    .hasSize(2)
                    .containsExactly("keyword1", "keyword2");
        }

        @Test
        @DisplayName("should handle null keywords list")
        void shouldHandleNullKeywordsList() {
            // given
            Service domain = Service.ofExisting(
                    1L,
                    "Test Service",
                    "test-service",
                    "Test description",
                    pl.klastbit.lexpage.domain.service.ServiceCategory.CIVIL_LAW,
                    null,
                    null,
                    Collections.emptyList(),
                    1,
                    null,
                    null,
                    null,
                    null,
                    UserId.createNew(),
                    UserId.createNew(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null
            );

            // when
            ServiceEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getKeywords()).isNull();
        }
    }

    @Nested
    @DisplayName("updateEntity() method")
    class UpdateEntityTests {

        @Test
        @DisplayName("should do nothing when entity is null")
        void shouldDoNothingWhenEntityIsNull() {
            // given
            Service domain = createFullServiceDomain();

            // when/then - no exception should be thrown
            mapper.updateEntity(null, domain);
        }

        @Test
        @DisplayName("should do nothing when domain is null")
        void shouldDoNothingWhenDomainIsNull() {
            // given
            ServiceEntity entity = createFullServiceEntity();

            // when/then - no exception should be thrown
            mapper.updateEntity(entity, null);
        }

        @Test
        @DisplayName("should update all mutable fields from domain to entity")
        void shouldUpdateAllMutableFieldsFromDomainToEntity() {
            // given
            ServiceEntity entity = createFullServiceEntity();
            LocalDateTime newUpdatedAt = LocalDateTime.now().plusDays(1);
            Service domain = Service.ofExisting(
                    1L,
                    "Updated Service",
                    "updated-service",
                    "Updated description",
                    pl.klastbit.lexpage.domain.service.ServiceCategory.CRIMINAL_LAW,
                    "Updated scope",
                    "Updated process",
                    Arrays.asList(new FaqItem("New Q", "New A")),
                    99,
                    "Updated Meta Title",
                    "Updated Meta Description",
                    "https://example.com/new-image.jpg",
                    Arrays.asList("updated", "keywords"),
                    UserId.createNew(),
                    UserId.createNew(),
                    LocalDateTime.now(),
                    newUpdatedAt,
                    null
            );

            // when
            mapper.updateEntity(entity, domain);

            // then
            assertThat(entity.getName()).isEqualTo("Updated Service");
            assertThat(entity.getSlug()).isEqualTo("updated-service");
            assertThat(entity.getDescription()).isEqualTo("Updated description");
            assertThat(entity.getCategory()).isEqualTo(ServiceCategory.CRIMINAL_LAW);
            assertThat(entity.getScope()).isEqualTo("Updated scope");
            assertThat(entity.getProcess()).isEqualTo("Updated process");
            assertThat(entity.getFaq()).contains("New Q").contains("New A");
            assertThat(entity.getDisplayOrder()).isEqualTo(99);
            assertThat(entity.getMetaTitle()).isEqualTo("Updated Meta Title");
            assertThat(entity.getMetaDescription()).isEqualTo("Updated Meta Description");
            assertThat(entity.getOgImageUrl()).isEqualTo("https://example.com/new-image.jpg");
            assertThat(entity.getKeywords()).containsExactly("updated", "keywords");
            assertThat(entity.getUpdatedAt()).isEqualTo(newUpdatedAt);
        }

        @Test
        @DisplayName("should preserve entity relationships during update")
        void shouldPreserveEntityRelationshipsDuringUpdate() {
            // given
            ServiceEntity entity = createFullServiceEntity();
            UserEntity originalCreatedBy = entity.getCreatedBy();
            UserEntity originalUpdatedBy = entity.getUpdatedBy();

            Service domain = Service.ofExisting(
                    1L,
                    "Updated Service",
                    "test-service",
                    "Test description",
                    pl.klastbit.lexpage.domain.service.ServiceCategory.CIVIL_LAW,
                    "Test scope",
                    "Test process",
                    Arrays.asList(new FaqItem("Q1", "A1")),
                    1,
                    "Meta Title",
                    "Meta Description",
                    "https://example.com/image.jpg",
                    Arrays.asList("legal", "service"),
                    UserId.createNew(),
                    UserId.createNew(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null
            );

            // when
            mapper.updateEntity(entity, domain);

            // then - relationships should not change
            assertThat(entity.getCreatedBy()).isSameAs(originalCreatedBy);
            assertThat(entity.getUpdatedBy()).isSameAs(originalUpdatedBy);
        }
    }

    @Nested
    @DisplayName("User reference extraction methods")
    class UserReferenceExtractionTests {

        @Test
        @DisplayName("getCreatedById() should return created by user ID when set")
        void getCreatedByIdShouldReturnCreatedByUserIdWhenSet() {
            // given
            ServiceEntity entity = createFullServiceEntity();

            // when
            UserId result = mapper.getCreatedById(entity);

            // then
            assertThat(result.userid()).isOfAnyClassIn(UUID.class);
        }

        @Test
        @DisplayName("getCreatedById() should return null when createdBy is not set")
        void getCreatedByIdShouldReturnNullWhenCreatedByIsNotSet() {
            // given
            ServiceEntity entity = createMinimalServiceEntity();
            entity.setCreatedBy(null);

            // when
            UserId result = mapper.getCreatedById(entity);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("getCreatedById() should return null when entity is null")
        void getCreatedByIdShouldReturnNullWhenEntityIsNull() {
            // when
            UserId result = mapper.getCreatedById(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("getUpdatedById() should return null when updatedBy is not set")
        void getUpdatedByIdShouldReturnNullWhenUpdatedByIsNotSet() {
            // given
            ServiceEntity entity = createMinimalServiceEntity();
            entity.setUpdatedBy(null);

            // when
            UserId result = mapper.getUpdatedById(entity);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("setUserReferences() method")
    class SetUserReferencesTests {

        @Test
        @DisplayName("should set user references when entity is not null")
        void shouldSetUserReferencesWhenEntityIsNotNull() {
            // given
            ServiceEntity entity = new ServiceEntity();
            UserEntity createdBy = createUserEntity(UUID.randomUUID(), "creator@example.com");
            UserEntity updatedBy = createUserEntity(UUID.randomUUID(), "updater@example.com");

            // when
            mapper.setUserReferences(entity, createdBy, updatedBy);

            // then
            assertThat(entity.getCreatedBy()).isSameAs(createdBy);
            assertThat(entity.getUpdatedBy()).isSameAs(updatedBy);
        }

        @Test
        @DisplayName("should do nothing when entity is null")
        void shouldDoNothingWhenEntityIsNull() {
            // given
            UserEntity user = createUserEntity(UUID.randomUUID(), "user@example.com");

            // when/then - no exception should be thrown
            mapper.setUserReferences(null, user, user);
        }

        @Test
        @DisplayName("should set null user references")
        void shouldSetNullUserReferences() {
            // given
            ServiceEntity entity = createFullServiceEntity();

            // when
            mapper.setUserReferences(entity, null, null);

            // then
            assertThat(entity.getCreatedBy()).isNull();
            assertThat(entity.getUpdatedBy()).isNull();
        }
    }

    @Nested
    @DisplayName("FAQ JSON conversion edge cases")
    class FaqJsonConversionEdgeCasesTests {

        @Test
        @DisplayName("should handle FAQ with all special characters")
        void shouldHandleFaqWithAllSpecialCharacters() {
            // given
            UserId.createNew();
            Service domain = Service.ofExisting(
                    1L,
                    "Test Service",
                    "test-service",
                    "Test description",
                    pl.klastbit.lexpage.domain.service.ServiceCategory.CIVIL_LAW,
                    null,
                    null,
                    Arrays.asList(
                            new FaqItem("Question with \"quotes\" and \n newlines \r\n and \t tabs",
                                    "Answer with \\ backslashes")
                    ),
                    1,
                    null,
                    null,
                    null,
                    null,
                    UserId.createNew(),
                    UserId.createNew(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null
            );

            // when
            ServiceEntity entity = mapper.toEntity(domain);
            Service result = mapper.toDomain(entity);

            // then
            assertThat(result.getFaqItems()).hasSize(1);
            assertThat(result.getFaqItems().get(0).question()).contains("\"quotes\"");
            assertThat(result.getFaqItems().get(0).answer()).contains("\\");
        }

        @Test
        @DisplayName("should handle malformed FAQ JSON gracefully")
        void shouldHandleMalformedFaqJsonGracefully() {
            // given
            ServiceEntity entity = createMinimalServiceEntity();
            entity.setFaq("not a valid json");

            // when
            Service result = mapper.toDomain(entity);

            // then
            assertThat(result.getFaqItems()).isEmpty();
        }
    }

    // Helper methods to create test objects

    private ServiceEntity createMinimalServiceEntity() {
        ServiceEntity entity = new ServiceEntity();
        entity.setId(1L);
        entity.setName("Test Service");
        entity.setSlug("test-service");
        entity.setDescription("Test description");
        entity.setCategory(ServiceCategory.CIVIL_LAW);
        entity.setDisplayOrder(1);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    private ServiceEntity createFullServiceEntity() {
        ServiceEntity entity = createMinimalServiceEntity();
        entity.setScope("Test scope");
        entity.setProcess("Test process");
        entity.setFaq("[{\"question\":\"Q1\",\"answer\":\"A1\"}]");
        entity.setMetaTitle("Meta Title");
        entity.setMetaDescription("Meta Description");
        entity.setOgImageUrl("https://example.com/image.jpg");
        entity.setKeywords(new String[]{"legal", "service"});

        UserEntity createdBy = createUserEntity(UUID.randomUUID(), "creator@example.com");
        UserEntity updatedBy = createUserEntity(UUID.randomUUID(), "updater@example.com");

        entity.setCreatedBy(createdBy);
        entity.setUpdatedBy(updatedBy);

        return entity;
    }

    private Service createMinimalServiceDomain() {
        return Service.ofExisting(
                1L,
                "Test Service",
                "test-service",
                "Test description",
                pl.klastbit.lexpage.domain.service.ServiceCategory.CIVIL_LAW,
                null,
                null,
                Collections.emptyList(),
                1,
                null,
                null,
                null,
                null,
                UserId.createNew(),
                UserId.createNew(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private Service createFullServiceDomain() {
        return Service.ofExisting(
                1L,
                "Test Service",
                "test-service",
                "Test description",
                pl.klastbit.lexpage.domain.service.ServiceCategory.CIVIL_LAW,
                "Test scope",
                "Test process",
                Arrays.asList(new FaqItem("Q1", "A1")),
                1,
                "Meta Title",
                "Meta Description",
                "https://example.com/image.jpg",
                Arrays.asList("legal", "service"),
                UserId.createNew(),
                UserId.createNew(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private UserEntity createUserEntity(UUID id, String email) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setEmail(email);
        return user;
    }
}
