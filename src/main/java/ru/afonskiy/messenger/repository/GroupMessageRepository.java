package ru.afonskiy.messenger.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.afonskiy.messenger.entity.GroupMessageEntity;

import java.util.Optional;

public interface GroupMessageRepository extends MongoRepository<GroupMessageEntity, String> {
    Optional<GroupMessageEntity> findByIdAndSender(String messageId, String username);
}
