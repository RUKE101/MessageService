package ru.afonskiy.messenger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.afonskiy.messenger.entity.GroupEntity;
import ru.afonskiy.messenger.entity.GroupMemberEntity;
import ru.afonskiy.messenger.repository.GroupMemberServiceRepository;
import ru.afonskiy.messenger.repository.GroupServiceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupMemberServiceRepository groupMemberServiceRepository;
    private final GroupServiceRepository groupServiceRepository;
    private final SendLogsService sendLogsService;
    private final GetCurrentUIID getCurrentUIID;

    public GroupEntity createGroup(GroupEntity groupEntity,String token) {
        try {
            groupServiceRepository.save(groupEntity);
            GroupMemberEntity groupMemberEntity = new GroupMemberEntity();
            return groupEntity;
        } catch (Exception e) {
            sendLogsService.sendLogs("Create group failed", token, e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public List<String> findAllGroupsForUser(String uuid, String token) {
        try {
            GroupMemberEntity memberEntity = groupMemberServiceRepository.findByUuidOfUser(uuid);
            if (memberEntity == null || memberEntity.getGroupId() == null) {
                return List.of();
            }
            List<String> groupIds = memberEntity.getGroupId().stream()
                    .distinct()
                    .toList();
            List<GroupEntity> groupEntities = groupServiceRepository.findByGroupIdIn(groupIds);
            return groupEntities.stream()
                    .map(GroupEntity::getNameOfGroup)
                    .toList();
        } catch (Exception e) {
            sendLogsService.sendLogs("Find all groups for user failed", e.getMessage(), token);
            throw new RuntimeException(e);
        }
    }
    public GroupEntity updateGroup( String id,String  nameOfGroup,String descriptionOfGroup ,String token) {
        try{
            GroupEntity groupEntity = groupServiceRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Group id not found")
            );
            groupEntity.setNameOfGroup(nameOfGroup);
            groupEntity.setDescriptionOfGroup(descriptionOfGroup);
            groupServiceRepository.save(groupEntity);
            return groupEntity;
        } catch (Exception e){
            sendLogsService.sendLogs("Update group failed", e.getMessage(), token);
            throw new RuntimeException(e);
        }
    }
    public void deleteGroup(String id,String  token) {
        try {
            groupMemberServiceRepository.deleteById(id);
        } catch (Exception e){
            sendLogsService.sendLogs("Delete group failed", e.getMessage(), token);
            throw new RuntimeException(e);
        }
    }

}
