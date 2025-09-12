package ru.afonskiy.messenger.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.afonskiy.messenger.entity.MessageEntity;

import java.util.Optional;

public interface MessageServiceRepository extends MongoRepository<MessageEntity, String> {
    Optional<MessageEntity> findByIdAndSender(String id , String sender);

}
