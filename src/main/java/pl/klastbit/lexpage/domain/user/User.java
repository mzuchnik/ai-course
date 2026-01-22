package pl.klastbit.lexpage.domain.user;

import lombok.Getter;

import java.util.Objects;

@Getter
public class User {

    private final UserId userId;

    private User(UserId userId) {
        this.userId = Objects.requireNonNull(userId, "UserId cannot be null");
    }

    public static User ofNew() {
        return new User(UserId.createNew());
    }

    public static User ofExisting(UserId userId) {
        return new User(userId);
    }
}
