package ru.afonskiy.messenger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.afonskiy.messenger.entity.GroupMessageEntity;
import ru.afonskiy.messenger.repository.GroupMessageRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GroupMessageService {
    private final GroupMessageRepository repository;
    private final GroupService groupService;
    private final SendLogsService sendLogsService;
    private final MongoTemplate mongoTemplate;


    public GroupMessageEntity sendMessage(GroupMessageEntity message, String token) {
        try {
             repository.save(message);
            return message;
        } catch (Exception e) {
            sendLogsService.sendLogs("Create group failed", token, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<GroupMessageEntity> getMessagesFromGroup(String userId, String groupId) {
        if (!groupService.isUserInGroup(userId, groupId)) {
            throw new AccessDeniedException("User is not a member of the group");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("groupId").is(groupId));

        return mongoTemplate.find(query, GroupMessageEntity.class);
    }
}
