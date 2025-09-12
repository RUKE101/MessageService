package ru.afonskiy.messenger.service.group;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.afonskiy.messenger.entity.group.GroupEntity;
import ru.afonskiy.messenger.jwt.util.JwtUtils;
import ru.afonskiy.messenger.repository.group.GroupRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final JwtUtils jwtUtils;

    public List<GroupEntity> getUserGroups(String userId) {
        /*
        Метод для получения всех групп юзера. (Влад, ты шо грибов объелся?)))
         */
        return groupRepository.findByParticipantsIdsContaining(userId);
    }

    public boolean isUserInGroup(String userId, String groupId) {
        /*
        Проверка наличия участника в группе.
         */
        GroupEntity group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return false;
        }
        return group.getParticipantsIds() != null && group.getParticipantsIds().contains(userId);
    }

    public GroupEntity getGroupById(String groupId) {
        /*
        Получение группы по ID группы
         */
        Optional<GroupEntity> group = groupRepository.findById(groupId);
        if (group.isEmpty()) {
            throw new NotFoundException("Not found group with this id: " + groupId);
        }
        return group.get();
    }

    public void createGroup(GroupEntity groupEntity, String token) {
        /*
        Метод для создания группы
         */
            groupEntity.addParticipantId(jwtUtils.getCurrentUIID(token));
            groupRepository.save(groupEntity);
    }

    public GroupEntity updateGroup(String groupId, String nameOfGroup, String descriptionOfGroup) {
        /*
        Метод для обновления полей группы
         */
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(
                () -> new RuntimeException("groupId not found")
        );
        groupEntity.setNameOfGroup(nameOfGroup);
        groupEntity.setDescriptionOfGroup(descriptionOfGroup);
        groupRepository.save(groupEntity);
        return groupEntity;
    }

    public GroupEntity getGroup(String groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Message not found"));
    }

    public GroupEntity joinGroup(String userUuid, String groupId) {
        GroupEntity group = getGroup(groupId);
        if (!isUserInGroup(groupId, userUuid)) {
            group.addParticipantId(userUuid);
            return groupRepository.save(group);
        } else throw new AccessDeniedException("You already participate this group");
    }
}