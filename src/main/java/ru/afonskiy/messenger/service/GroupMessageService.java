package ru.afonskiy.messenger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.afonskiy.messenger.entity.GroupMessageEntity;
import ru.afonskiy.messenger.repository.GroupMessageRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GroupMessageService {
    private final GroupMessageRepository repository;
    private final GroupService groupService;
    private final SendLogsService sendLogsService;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public GroupMessageEntity sendMessage(GroupMessageEntity message, String token) {
        try {
            isUserInGroup(message.getSender(), message.getGroupId());
             repository.save(message);
            return message;
        } catch (Exception e) {
            sendLogsService.sendLogs("Create group failed", token, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void updateGroupMessage(String messageId, String groupId, String userId, String username, String newText) {
        isUserInGroup(userId, groupId);

        Optional<GroupMessageEntity> optionalMessage = repository.findByIdAndSender(messageId, username);
        if (optionalMessage.isEmpty()) {
            throw new RuntimeException("Message not found or user is not sender, update failed");
        }
        GroupMessageEntity message = optionalMessage.get();
        message.setText(newText);
        repository.save(message);
    }

    @Transactional
    public void deleteGroupMessage(String messageId, String groupId, String userId, String username) {
        isUserInGroup(userId, groupId);

        Optional<GroupMessageEntity> optionalMessage = repository.findById(messageId);

        if (optionalMessage.isEmpty() || !optionalMessage.get().getSender().equals(username)) {
            throw new RuntimeException("Message not found or user not authorized to delete");
        }

        repository.deleteById(messageId);
    }

    public List<GroupMessageEntity> getMessagesFromGroup(String userId, String groupId) {
        isUserInGroup(userId, groupId);
        Query query = new Query();
        query.addCriteria(Criteria.where("groupId").is(groupId));

        return mongoTemplate.find(query, GroupMessageEntity.class);
    }

    public void isUserInGroup(String userId, String groupId) {
        if (!groupService.isUserInGroup(userId, groupId)) {
            throw new AccessDeniedException("User is not a member of the group");
        }
    }
}
