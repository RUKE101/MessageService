package ru.afonskiy.messenger.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.afonskiy.messenger.entity.GroupEntity;
import ru.afonskiy.messenger.entity.GroupMemberEntity;

import java.util.List;

public interface GroupServiceRepository extends MongoRepository<GroupEntity, String> {
    List<GroupEntity> findByGroupIdIn(List<String> groupId);
}
