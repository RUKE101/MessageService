package ru.afonskiy.messenger.service;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.afonskiy.messenger.entity.GroupMessageEntity;
import ru.afonskiy.messenger.entity.MessageEntity;
import ru.afonskiy.messenger.repository.GroupMessageRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GroupMessageService {
    private final GroupMessageRepository repository;
    private final GroupService groupService;
    private final MongoTemplate mongoTemplate;


    @Transactional
    public GroupMessageEntity sendMessage(GroupMessageEntity message) {
        /*
        Метод для сохранения сообщения в mongoDB
         */
        isUserInGroup(message.getSender(), message.getGroupId());
        repository.save(message);
        return message;
    }

    @Transactional
    public void updateGroupMessage(String messageId, String groupId, String userId, String newText) {
        /*
        Метод для Обновления сообщения в mongoDB
         */
        isUserInGroup(userId, groupId);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is((messageId)));
        Update update = new Update()
                .set("text", newText);
        UpdateResult result = mongoTemplate.updateFirst(query, update, MessageEntity.class);

        if (result.getMatchedCount() == 0) {
            throw new RuntimeException("Message not found or user is not sender, update failed");
        }
    }

    public void deleteGroupMessage(String messageId, String groupId, String userId) {
        /*
        Метод для удаления сообщения
         */
        isUserInGroup(userId, groupId);
        repository.deleteById(messageId);
    }

    public List<GroupMessageEntity> getMessagesFromGroup(String userId, String groupId) {
        /*
        Метод для получения всех сообщений в группе
         */
        isUserInGroup(userId, groupId);
        return repository.findByGroupId(groupId);
    }

    public void isUserInGroup(String userId, String groupId) {
        /*
        Проверка участника в группе
         */
        if (!groupService.isUserInGroup(userId, groupId)) {
            throw new AccessDeniedException("User is not a member of the group");
        }
    }
}
