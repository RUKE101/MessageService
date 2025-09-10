package ru.afonskiy.messenger.service;

import com.mongodb.client.result.DeleteResult;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MongoTemplate mongoTemplate;

    @Transactional
    public void createMessage(MessageEntity message) {
        message.setStatus(MessageStatus.UNREAD);
        mongoTemplate.save(message);
    }
    public void updateMessage(String id,String text, String username) {
        Query query = new Query(Criteria.where("id").is(id));
        query.addCriteria(Criteria.where("sender").is(username));
        Update update = new Update()
                .set("text", text);
        UpdateResult result = mongoTemplate.updateFirst(query, update, MessageEntity.class);

        if (result.getMatchedCount() == 0) {
            throw new RuntimeException("Message not found or user is not sender, update failed");
        }
    }

    public void deleteMessage(String id, String username) {
        Query query = new Query(Criteria.where("id").is(id)
                .orOperator(
                        Criteria.where("sender").is(username),
                        Criteria.where("receiver").is(username)
                ));
        DeleteResult result = mongoTemplate.remove(query, MessageEntity.class);

        if (result.getDeletedCount() == 0) {
            throw new RuntimeException("Message not found or user not authorized to delete");
        }

        mongoTemplate.remove(query, MessageEntity.class);
    }

    public MessageEntity getMessage(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, MessageEntity.class);
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
