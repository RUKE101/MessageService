package ru.afonskiy.messenger.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.afonskiy.messenger.entity.GroupMessageEntity;

public interface GroupMessageRepository extends MongoRepository<GroupMessageEntity, String> {
}
