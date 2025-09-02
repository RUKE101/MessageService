package ru.afonskiy.messenger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.afonskiy.messenger.entity.MessageEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MongoTemplate mongoTemplate;

    @Transactional
    public void createMessage(MessageEntity message) {
        mongoTemplate.save(message);
    }
    public void updateMessage(String id,MessageEntity message) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update()
                .set("message", message.getText());
    }

    public void deleteMessage(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, MessageEntity.class);
    }

    public MessageEntity getMessage(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, MessageEntity.class);
    }

    public List<MessageEntity> getMessages(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("recipient").is(username));
        return mongoTemplate.find(query, MessageEntity.class);
    }

/*    public boolean isUserExists(String username) {

    }*/
}
