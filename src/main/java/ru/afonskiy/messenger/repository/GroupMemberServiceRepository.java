package ru.afonskiy.messenger.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.afonskiy.messenger.entity.GroupMemberEntity;

import java.util.List;

@Repository
public interface GroupMemberServiceRepository extends MongoRepository<GroupMemberEntity, String> {
    GroupMemberEntity findByUuidOfUser(String uuidOfUser);

}
