# Backend Java - Zasady Pisania Kodu

## Lombok

### Obligatoryjne użycie Lomboka

W projekcie **ZAWSZE** używamy Lombok do eliminacji boilerplate code:

**Gettery i Settery:**
- `@Getter` - generuje gettery dla wszystkich pól
- `@Setter` - generuje settery dla wszystkich pól

**Konstruktory:**
- `@NoArgsConstructor` - konstruktor bezparametrowy
- `@AllArgsConstructor` - konstruktor ze wszystkimi parametrami
- `@RequiredArgsConstructor` - konstruktor dla pól `final` i `@NonNull`

**Inne przydatne adnotacje:**
- `@ToString` - generuje metodę toString()
- `@EqualsAndHashCode` - generuje equals() i hashCode()
- `@Data` - łączy @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
- `@Builder` - implementuje wzorzec Builder
- `@Slf4j` - wstrzykuje logger
- ⚠️ `@Value` - UNIKAJ, zamiast tego użyj **Java Records**

### Przykłady

```java
// Entity z JPA
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
}

// Domain Service
@RequiredArgsConstructor
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public User register(RegistrationCommand command) {
        // logika
    }
}
```

## Java Records - Preferowane dla Niezmiennych Klas

**ZASADA**: ZAWSZE preferuj **Java Records** dla:
- DTO (Data Transfer Objects)
- Value Objects (DDD)
- Commands i Queries
- Responses/Requests API
- Wszystkich niezmiennych klas danych

### Przykłady

```java
// DTO
public record UserDto(Long id, String username, String email) {}

// Value Object z walidacją
public record EmailAddress(String value) {
    public EmailAddress {
        if (value == null || !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email: " + value);
        }
    }
}

// Command
public record CreateUserCommand(String username, String email, String password) {
    public CreateUserCommand {
        Objects.requireNonNull(username, "username cannot be null");
        Objects.requireNonNull(email, "email cannot be null");
        Objects.requireNonNull(password, "password cannot be null");
    }
}

// Value Object z logiką biznesową
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        Objects.requireNonNull(currency);
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }
}
```

### Kiedy NIE używać Records

- **JPA Entities** - wymagają mutowalności dla Hibernate
- **Klasy wymagające dziedziczenia** - Records są final
- **Klasy z dużą ilością logiki** - lepiej użyć zwykłej klasy

## Domain-Driven Design (DDD)

### 1. Agregaty i Aggregate Roots

**Agregat** = grupa powiązanych obiektów traktowanych jako jedna jednostka.
**Aggregate Root** = główna encja agregatu, przez którą odbywa się cały dostęp.

```java
// Aggregate Root
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // Aggregate Root kontroluje dostęp do encji wewnętrznych
    public void addItem(Product product, int quantity) {
        OrderItem item = new OrderItem(this, product, quantity);
        items.add(item);
    }

    // Logika biznesowa w agregatach
    public Money calculateTotal() {
        return items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(Money.zero(Currency.PLN), Money::add);
    }

    // Aggregate Root zapewnia spójność
    public void confirm() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot confirm empty order");
        }
        this.status = OrderStatus.CONFIRMED;
    }
}

// Encja wewnętrzna - dostęp tylko przez Aggregate Root
@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    private Product product;

    private int quantity;

    OrderItem(Order order, Product product, int quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
    }

    Money getSubtotal() {
        return product.getPrice().multiply(quantity);
    }
}
```

**Zasady Agregatów:**
- Dostęp do encji wewnętrznych TYLKO przez Aggregate Root
- Transakcje operują na pojedynczych agregatach
- Referencje między agregatami przez ID, nie przez obiekty
- Małe agregaty = lepsza wydajność

### 2. Value Objects

**Value Object** = niezmienne obiekty bez tożsamości, definiowane przez wartości.

```java
// DOBRZE - Value Object jako Record
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        Objects.requireNonNull(currency);
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}

public record EmailAddress(String value) {
    public EmailAddress {
        if (value == null || !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }
}

public record Address(String street, String city, String postalCode, String country) {
    public Address {
        Objects.requireNonNull(street);
        Objects.requireNonNull(city);
        Objects.requireNonNull(postalCode);
        Objects.requireNonNull(country);
    }
}
```

**Cechy Value Objects:**
- Immutable (niezmienne)
- Equals oparte na wartościach
- Zawierają walidację i logikę biznesową
- **ZAWSZE używaj Records**

### 3. Domain Services

**Domain Service** = operacje biznesowe nie należące do żadnej encji.

```java
// Domain Service
@RequiredArgsConstructor
public class OrderPricingService {
    private final DiscountPolicy discountPolicy;

    public Money calculateOrderTotal(Order order, Customer customer) {
        Money subtotal = order.calculateSubtotal();
        Discount discount = discountPolicy.calculateDiscount(customer, subtotal);
        return subtotal.subtract(discount.getAmount());
    }
}
```

**Domain Service vs Application Service:**
- **Domain Service**: Czysta logika biznesowa, brak zależności od infrastruktury
- **Application Service**: Orkiestracja, transakcje, wywołania repozytoriów

### 4. Domain Events

**Domain Event** = zdarzenie domenowe informujące o ważnych zmianach.

```java
// Domain Event jako Record
public record OrderConfirmedEvent(
    Long orderId,
    String orderNumber,
    LocalDateTime confirmedAt,
    Money total
) {
    public static OrderConfirmedEvent from(Order order) {
        return new OrderConfirmedEvent(
            order.getId(),
            order.getOrderNumber(),
            LocalDateTime.now(),
            order.calculateTotal()
        );
    }
}

// Aggregate publikuje eventy
@Entity
@Getter
public class Order {
    @Transient
    private final List<Object> domainEvents = new ArrayList<>();

    public void confirm() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot confirm empty order");
        }
        this.status = OrderStatus.CONFIRMED;
        domainEvents.add(OrderConfirmedEvent.from(this));
    }

    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }
}
```

### 5. Repositories (Porty)

**Repository** = interfejs dostępu do agregatów, definiowany w warstwie domenowej.

```java
// Repository Port - interfejs w domain layer
package pl.klastbit.lexpage.domain.order;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCustomerId(Long customerId);
    void delete(Order order);
}

// Implementacja w infrastructure layer (Adapter)
package pl.klastbit.lexpage.infrastructure.adapters.persistence;

@Repository
@RequiredArgsConstructor
class JpaOrderRepository implements OrderRepository {
    private final SpringDataOrderRepository springRepo;

    @Override
    public Order save(Order order) {
        return springRepo.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return springRepo.findById(id);
    }
    // ...
}

interface SpringDataOrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCustomerId(Long customerId);
}
```

**Zasady Repositories:**
- Interfejs w `domain/` pakiecie
- Implementacja w `infrastructure/adapters/` pakiecie
- Operacje na Aggregate Roots, nie na encjach wewnętrznych
- Używaj języka domenowego (findByOrderNumber, nie findByColumn)

### 6. Ubiquitous Language

Używaj tego samego języka w kodzie, rozmowach i dokumentacji:

```java
// DOBRZE - język domenowy
public class Order {
    public void confirm() { ... }
    public void cancel(CancellationReason reason) { ... }
    public void ship(TrackingNumber trackingNumber) { ... }
}

// ŹLE - język techniczny
public class Order {
    public void setStatus(String status) { ... }
    public void updateState(int state) { ... }
}
```

### 7. Domain Layer - Zasady Zależności

**KRYTYCZNA ZASADA**: Warstwa domenowa **NIE MOŻE** mieć zależności od zewnętrznych bibliotek!

**JEDYNY WYJĄTEK**: Lombok (dla eliminacji boilerplate)

```java
// DOBRZE - Domain bez zewnętrznych zależności
package pl.klastbit.lexpage.domain.order;

@RequiredArgsConstructor  // Lombok OK
public class OrderPricingService {
    private final DiscountPolicy discountPolicy;

    public Money calculateTotal(Order order) {
        // czysta logika biznesowa
    }
}

// ŹLE - Domain z zależnościami od Spring/infrastruktury
package pl.klastbit.lexpage.domain.order;

@Service  // ŹLE! Spring w domain!
public class OrderPricingService {
    @Autowired  // ŹLE!
    private EmailSender emailSender;  // ŹLE! Infrastruktura w domain!
}
```

**Dozwolone w Domain Layer:**
- JPA adnotacje na encjach (`@Entity`, `@Id`, etc.)
- Lombok (`@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@Slf4j`)
- Java stdlib (Collections, Optional, etc.)

**ZABRONIONE w Domain Layer:**
- Spring Framework (`@Service`, `@Component`, `@Autowired`)
- Zewnętrzne biblioteki HTTP/REST
- JSON/XML parsery (Jackson, JAXB)
- Bazy danych poza JPA
- Biblioteki do wysyłki email, SMS, etc.

## Architektura Heksagonalna (Ports & Adapters)

### Podstawowe Zasady

1. **Domena w centrum** - nie zależy od niczego (poza Lombok)
2. **Porty** - interfejsy definiujące komunikację
3. **Adaptery** - implementacje portów
4. **Kierunek zależności**: Infrastructure → Application → Domain

### Struktura Pakietów

```
pl.klastbit.lexpage/
├── domain/                          # Warstwa domenowa
│   ├── order/
│   │   ├── Order.java              # Aggregate Root
│   │   ├── OrderItem.java          # Encja
│   │   ├── OrderStatus.java        # Enum
│   │   ├── OrderRepository.java    # Port (interfejs)
│   │   ├── Money.java              # Value Object (Record)
│   │   └── OrderConfirmedEvent.java # Domain Event (Record)
│   └── customer/
│       ├── Customer.java
│       └── CustomerRepository.java
│
├── application/                     # Warstwa aplikacji
│   ├── order/
│   │   ├── CreateOrderUseCase.java      # Use Case
│   │   ├── CreateOrderCommand.java      # Command (Record)
│   │   └── OrderApplicationService.java # Application Service
│   └── ports/
│       ├── NotificationPort.java        # Outbound Port
│       └── PaymentPort.java             # Outbound Port
│
└── infrastructure/                  # Warstwa infrastruktury
    ├── web/                        # Inbound Adapters
    │   ├── OrderController.java   # REST Controller
    │   └── dto/
    │       ├── CreateOrderRequest.java  # DTO (Record)
    │       └── OrderResponse.java       # DTO (Record)
    │
    ├── adapters/                   # Outbound Adapters
    │   ├── persistence/
    │   │   ├── JpaOrderRepository.java
    │   │   └── JpaCustomerRepository.java
    │   ├── notification/
    │   │   └── EmailNotificationAdapter.java
    │   └── payment/
    │       └── StripePaymentAdapter.java
    │
    └── config/
        └── SpringConfiguration.java
```

### Porty (Ports)

#### Inbound Ports (Primary/Driving)

Use Cases definiują co aplikacja może robić:

```java
// Use Case interface
package pl.klastbit.lexpage.application.order;

public interface CreateOrderUseCase {
    OrderResponse execute(CreateOrderCommand command);
}

// Command jako Record
public record CreateOrderCommand(
    Long customerId,
    List<OrderItemRequest> items
) {
    public record OrderItemRequest(Long productId, int quantity) {}
}

// Application Service implementuje Use Case
@Service
@RequiredArgsConstructor
@Transactional
public class OrderApplicationService implements CreateOrderUseCase {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final NotificationPort notificationPort;

    @Override
    public OrderResponse execute(CreateOrderCommand command) {
        Customer customer = customerRepository.findById(command.customerId())
            .orElseThrow(() -> new CustomerNotFoundException(command.customerId()));

        Order order = new Order(customer);

        for (var itemReq : command.items()) {
            Product product = productRepository.findById(itemReq.productId())
                .orElseThrow(() -> new ProductNotFoundException(itemReq.productId()));
            order.addItem(product, itemReq.quantity());
        }

        Order savedOrder = orderRepository.save(order);
        notificationPort.sendOrderConfirmation(customer.getEmail(), savedOrder);

        return OrderResponse.from(savedOrder);
    }
}
```

#### Outbound Ports (Secondary/Driven)

```java
// Port w application/ports lub domain
package pl.klastbit.lexpage.application.ports;

public interface NotificationPort {
    void sendOrderConfirmation(EmailAddress email, Order order);
    void sendShippingNotification(EmailAddress email, TrackingNumber tracking);
}

// Adapter w infrastructure/adapters
package pl.klastbit.lexpage.infrastructure.adapters.notification;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationAdapter implements NotificationPort {
    private final JavaMailSender mailSender;

    @Override
    public void sendOrderConfirmation(EmailAddress email, Order order) {
        log.info("Sending order confirmation to {}", email.value());
        // ...
    }
}
```

### Adaptery (Adapters)

#### Inbound Adapters (Web Controllers)

```java
package pl.klastbit.lexpage.infrastructure.web;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        CreateOrderCommand command = request.toCommand();
        OrderResponse response = createOrderUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

// Request DTO jako Record
public record CreateOrderRequest(
    @NotNull Long customerId,
    @NotEmpty List<OrderItemRequest> items
) {
    public record OrderItemRequest(@NotNull Long productId, @Min(1) int quantity) {}

    public CreateOrderCommand toCommand() {
        // ...
    }
}

// Response DTO jako Record
public record OrderResponse(Long id, String orderNumber, String status, String total) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getOrderNumber(),
            order.getStatus().name(),
            order.calculateTotal().toString()
        );
    }
}
```

### Zasady Architektury Heksagonalnej

**1. Domain Layer:**
- Czysta logika biznesowa
- **ZERO zależności od frameworków** (wyjątek: Lombok, JPA dla entities)
- Definiuje porty (interfejsy)
- Agregaty, Value Objects, Domain Services, Domain Events

**2. Application Layer:**
- Use Cases / Application Services
- Orkiestracja wywołań domenowych
- Zarządzanie transakcjami
- Definiuje Outbound Ports

**3. Infrastructure Layer:**
- Implementacje portów (adaptery)
- REST controllers (Inbound)
- Repozytoria JPA (Outbound)
- Klienci zewnętrznych API (Outbound)
- Konfiguracja Spring

**4. Kierunek zależności:**
```
Infrastructure → Application → Domain
(Adapters)       (Use Cases)    (Business Logic)
```

**5. Testowanie:**
- **Domain** - testy jednostkowe bez mocków
- **Application** - testy z mockami portów
- **Infrastructure** - testy integracyjne

## Logowanie

### SLF4J z Lombok

Używamy `@Slf4j`:

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    public void processPayment(Payment payment) {
        log.debug("Processing payment: {}", payment.getId());
        try {
            // logika
            log.info("Payment {} processed successfully", payment.getId());
        } catch (PaymentException e) {
            log.error("Payment {} failed: {}", payment.getId(), e.getMessage(), e);
            throw e;
        }
    }
}
```

### Poziomy logowania

- **TRACE** - bardzo szczegółowe debugowanie (rzadko)
- **DEBUG** - wejście/wyjście z metod, wartości parametrów, stany pośrednie
- **INFO** - uruchomienie aplikacji, ważne operacje biznesowe, zmiany konfiguracji
- **WARN** - deprecated API, nieoptymalny stan (cache miss), obsłużone błędy zewnętrzne
- **ERROR** - nieobsłużone wyjątki, błędy integracji, błędy krytyczne

### Dobre praktyki logowania

```java
// DOBRZE - strukturalne logowanie
log.info("User {} registered with email {}", userId, email);

// ŹLE - konkatenacja
log.info("User " + userId + " registered with email " + email);

// DOBRZE - wyjątek jako ostatni parametr
log.error("Failed to process order {}", orderId, exception);

// DOBRZE - warunkowe logowanie kosztownych operacji
if (log.isDebugEnabled()) {
    log.debug("Complex state: {}", expensiveToStringOperation());
}
```

## Najlepsze Praktyki Kodu Java

### 1. Czystość i Czytelność

```java
// DOBRZE - metody krótkie, jednozadaniowe
public void processOrder(Order order) {
    validateOrder(order);
    calculateTotal(order);
    saveOrder(order);
    sendConfirmationEmail(order);
}

// ŹLE - zbyt długa metoda
public void processOrder(Order order) {
    // 200 linii kodu...
}
```

### 2. Immutability

```java
// DOBRZE - Record
public record Money(BigDecimal amount, Currency currency) {
    public Money add(Money other) {
        return new Money(amount.add(other.amount), currency);
    }
}

// ŹLE - mutowalne pola
public class Money {
    private BigDecimal amount; // mutable!
    public void setAmount(BigDecimal amount) { ... }
}
```

### 3. Null Safety

```java
// DOBRZE - Optional
public Optional<User> findUserByEmail(String email) {
    return userRepository.findByEmail(email);
}

// DOBRZE - @NonNull
@RequiredArgsConstructor
public class UserService {
    private final @NonNull UserRepository repository;

    public User createUser(@NonNull String username, @NonNull String email) {
        // ...
    }
}

// Objects.requireNonNull dla krytycznych parametrów
public void process(ImportantData data) {
    Objects.requireNonNull(data, "data cannot be null");
    // ...
}
```

### 4. Dependency Injection

```java
// DOBRZE - constructor injection
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
}

// ŹLE - field injection
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository; // trudne do testowania!
}
```

### 5. Walidacja i Error Handling

```java
// DOBRZE - walidacja na wejściu
public void createUser(String username, String email) {
    if (username == null || username.isBlank()) {
        throw new IllegalArgumentException("Username cannot be blank");
    }
    // ...
}

// DOBRZE - własne wyjątki domenowe
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String username) {
        super("User already exists: " + username);
    }
}
```

### 6. Naming Conventions

```java
// Klasy: PascalCase
public class UserRegistrationService {}

// Metody i zmienne: camelCase
public User findUserById(Long userId) {}

// Stałe: UPPER_SNAKE_CASE
private static final int MAX_RETRY_ATTEMPTS = 3;

// Pakiety: lowercase
package pl.klastbit.lexpage.domain.user;

// Metody boolean: is/has/can prefix
public boolean isActive() {}
public boolean hasPermission() {}
```

### 7. SOLID Principles

**Single Responsibility:**
```java
// DOBRZE
public class UserPasswordService {
    public void changePassword(User user, String newPassword) { ... }
}

// ŹLE
public class UserService {
    public void changePassword() {}
    public void sendEmail() {}
    public void generateReport() {}
}
```

**Dependency Inversion:**
```java
// DOBRZE - zależność od abstrakcji
@Service
@RequiredArgsConstructor
public class OrderService {
    private final NotificationPort notificationPort; // interface!
}

// ŹLE - konkretna implementacja
public class OrderService {
    private final EmailNotificationService emailService; // konkretna klasa!
}
```

## Najlepsze Praktyki Testów

### 1. AAA Pattern

```java
@Test
void shouldCalculateTotalPrice() {
    // Arrange
    Order order = Order.builder()
        .item("Product A", Money.of(100))
        .build();

    // Act
    Money total = order.calculateTotal();

    // Assert
    assertThat(total).isEqualTo(Money.of(100));
}
```

### 2. Naming Convention

```java
// Pattern: should[ExpectedBehavior]When[StateUnderTest]
@Test
void shouldThrowExceptionWhenEmailIsInvalid() {}

@Test
void shouldReturnEmptyOptionalWhenUserNotFound() {}

// Dla prostych przypadków:
@Test
void shouldCalculateDiscount() {}
```

### 3. Mockowanie z Mockito

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldSendEmailAfterUserCreation() {
        // Arrange
        User user = new User("john", "john@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.createUser("john", "john@example.com");

        // Assert
        verify(emailService).sendWelcomeEmail("john@example.com");
        verify(userRepository).save(any(User.class));
    }
}
```

### 4. AssertJ dla Asercji

```java
// DOBRZE - AssertJ (fluent)
assertThat(user.getUsername()).isEqualTo("john");
assertThat(users).hasSize(3)
                 .extracting(User::getUsername)
                 .containsExactly("john", "jane", "bob");

assertThat(result).isPresent()
                  .get()
                  .extracting(User::getEmail)
                  .isEqualTo("john@example.com");

// ŹLE - JUnit assertions
assertEquals("john", user.getUsername());
```

### 5. Test Coverage

- **Priorytet**: logika biznesowa (domain services, use cases)
- **Wysoki coverage**: domain layer (>90%)
- **Średni coverage**: application layer (>80%)
- **Niski priorytet**: infrastructure adapters, konfiguracja
- **Nie testujemy**: gettery/settery, konstruktory Lombok

### 6. Izolacja Testów

```java
// DOBRZE - niezależne testy
@Test
void testA() {
    User user = createTestUser();
    // ...
}

@Test
void testB() {
    User user = createTestUser();
    // ...
}

// ŹLE - współdzielony stan
private User sharedUser;

@BeforeEach
void setUp() {
    sharedUser = createTestUser();
}
```

### 7. Testy Wyjątków

```java
// DOBRZE - AssertJ assertThatThrownBy
@Test
void shouldThrowExceptionWhenEmailAlreadyExists() {
    when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

    assertThatThrownBy(() -> userService.createUser("john", "test@example.com"))
        .isInstanceOf(UserAlreadyExistsException.class)
        .hasMessageContaining("test@example.com");
}
```

## Podsumowanie

### Podstawowe Zasady

1. **Java Records** - ZAWSZE dla DTO, Value Objects, Commands, Responses
2. **Lombok** - ZAWSZE dla getterów, setterów, konstruktorów w klasach mutowalnych
3. **@Slf4j** - ZAWSZE do logowania
4. **Czysty kod** - krótkie metody, jasne nazwy

### Domain-Driven Design

5. **Agregaty** - dostęp tylko przez Aggregate Root
6. **Value Objects** - niezmienne Records z walidacją
7. **Domain Services** - czysta logika biznesowa
8. **Domain Events** - komunikuj zmiany domenowe
9. **Ubiquitous Language** - język domenowy w kodzie
10. **Brak zewnętrznych zależności** - domain bez Spring/infrastruktury (wyjątek: Lombok)

### Architektura Heksagonalna

11. **Struktura**: domain/ → application/ → infrastructure/
12. **Porty w domenie** - Repository interfaces w domain/
13. **Adaptery w infrastrukturze** - implementacje w infrastructure/adapters/
14. **Kierunek zależności**: Infrastructure → Application → Domain

### Testowanie

15. **Coverage**: domain >90%, application >80%
16. **AssertJ** - fluent asercje
17. **Izolacja** - niezależne testy
18. **AAA Pattern** - Arrange, Act, Assert
19. **Mockuj porty** - w testach application layer

### Inne

20. **SOLID** - pojedyncze odpowiedzialności, dependency inversion
21. **Walidacja** - fail fast
22. **Constructor Injection** - final fields z @RequiredArgsConstructor
23. **Immutability** - preferuj niezmienne obiekty
