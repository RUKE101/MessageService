package ru.afonskiy.messenger.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.afonskiy.messenger.entity.GroupEntity;
import ru.afonskiy.messenger.jwt.util.JwtUtils;
import ru.afonskiy.messenger.repository.GroupRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final JwtUtils jwtUtils;

    public List<GroupEntity> getUserGroups(String userId) {
        /*
        Метод для получения всех юзеров в группе.
         */
        return groupRepository.findByParticipantsIdsContaining(userId);
    }

    public boolean isUserInGroup(String userId, String groupId) {
        /*
        Проверка участника в группе.
         */
        GroupEntity group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return false;
        }
        return group.getParticipantsIds() != null && group.getParticipantsIds().contains(userId);
    }

    public GroupEntity getGroupById(String groupId) {
        /*
        Получение группы по ID
         */
        Optional<GroupEntity> group = groupRepository.findById(groupId);
        if (group.isEmpty()) {
            throw new NotFoundException("Not found group with this id: " + groupId);
        }
        return group.get();
    }

    public GroupEntity createGroup(GroupEntity groupEntity,String token) {
        /*
        Метод для создания группы
         */
            groupEntity.addParticipantId(jwtUtils.getCurrentUIID(token));
            groupRepository.save(groupEntity);
            return groupEntity;
    }

    public GroupEntity updateGroup(String id, String nameOfGroup, String descriptionOfGroup) {
        /*
        Метод для обновления группы
         */
        GroupEntity groupEntity = groupRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Group id not found")
        );
        groupEntity.setNameOfGroup(nameOfGroup);
        groupEntity.setDescriptionOfGroup(descriptionOfGroup);
        groupRepository.save(groupEntity);
        return groupEntity;
    }
}
