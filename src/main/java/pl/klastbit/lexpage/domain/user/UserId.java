package pl.klastbit.lexpage.domain.user;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public record UserId(UUID userid) {

    public UserId {
        if (userid == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
    }

    public static UserId createNew() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId of(UUID uuid) {
        return new UserId(uuid);
    }
}
