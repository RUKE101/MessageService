package ru.afonskiy.messenger.repository.group;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.afonskiy.messenger.entity.group.GroupMessageEntity;

import java.util.Optional;

public interface GroupMessageRepository extends MongoRepository<GroupMessageEntity, String> {
    Optional<GroupMessageEntity> findByIdAndSender(String messageId, String username);
}
