package ru.afonskiy.messenger.repository.dm;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.afonskiy.messenger.entity.dm.MessageEntity;

import java.util.Optional;

public interface MessageServiceRepository extends MongoRepository<MessageEntity, String> {
    Optional<MessageEntity> findByIdAndSender(String id , String sender);

}
