package ru.afonskiy.messenger.repository.group;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.afonskiy.messenger.entity.group.GroupEntity;

import java.util.List;

public interface GroupRepository extends MongoRepository<GroupEntity, String> {
    List<GroupEntity> findByParticipantsIdsContaining(String userId);
}
