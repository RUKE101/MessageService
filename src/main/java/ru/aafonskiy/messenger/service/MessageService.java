package ru.aafonskiy.messenger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import ru.aafonskiy.messenger.entity.MessageEntity;

@Service
public class MessageService {
    MongoTemplate mongoTemplate;

    @Autowired
    public MessageService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void createdMessage(MessageEntity message) {
        mongoTemplate.save(message);
    }

    public void updatedMessage(String id,MessageEntity message) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update()
                .set("message", message.getMessage());
    }

    public void deletedMessage(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, MessageEntity.class);
    }

    public MessageEntity getMessage(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, MessageEntity.class);
    }
}
