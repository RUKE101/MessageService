package ru.afonskiy.messenger.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.afonskiy.messenger.entity.GroupMessageEntity;

import java.util.List;

public interface GroupMessageRepository extends MongoRepository<GroupMessageEntity, String> {
    List<GroupMessageEntity> findByGroupId(String groupId);

}
