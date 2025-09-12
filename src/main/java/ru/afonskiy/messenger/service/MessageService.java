package ru.afonskiy.messenger.service;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.afonskiy.messenger.entity.MessageEntity;
import ru.afonskiy.messenger.entity.MessageStatus;
import ru.afonskiy.messenger.repository.MessageServiceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MongoTemplate mongoTemplate;
    private final MessageServiceRepository messageServiceRepository;

    @Transactional
    public void createMessage(MessageEntity message) {
        message.setStatus(MessageStatus.UNREAD);
        messageServiceRepository.save(message);
    }

    public void updateMessage(String id, String text, String username) {
        Query query = new Query(Criteria.where("id").is(id));
        query.addCriteria(Criteria.where("sender").is(username));
        Update update = new Update().set("text", text);
        UpdateResult result = mongoTemplate.updateFirst(query, update, MessageEntity.class);
        if (result.getMatchedCount() == 0) {
            throw new RuntimeException("Message not found or user is not sender, update failed");
        }
    }

    public void deleteMessage(String id) {
        messageServiceRepository.deleteById(id);
    }

    public MessageEntity getMessage(String id) {
        return messageServiceRepository.findById(id).orElseThrow(() -> new RuntimeException("Message not found"));
    }

    public List<MessageEntity> getMessages(String recipient, String sender) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sender").is(sender));
        query.addCriteria(Criteria.where("recipient").is(recipient));
        return mongoTemplate.find(query, MessageEntity.class);
    }

    public List<MessageEntity> getUnreadMessages(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("recipient").is(username));
        query.addCriteria(Criteria.where("status").is(MessageStatus.UNREAD));
        Update update = new Update().set("status", MessageStatus.READ);
        List<MessageEntity> messages = mongoTemplate.find(query, MessageEntity.class);
        mongoTemplate.updateMulti(query, update, MessageEntity.class);
        return messages;
    }

}
