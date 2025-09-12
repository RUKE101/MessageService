package ru.afonskiy.messenger.entity.group;

import com.mongodb.lang.NonNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class GroupEntity {
    @Id
    private String groupId;
    private String ownerId;
    @NonNull
    private String nameOfGroup;
    private String DescriptionOfGroup;
    @CreatedDate
    private String dateOfCreation;
    private List<String> participantsIds = new ArrayList<>();

    public GroupEntity addParticipantId(String uuid) {
        if (participantsIds == null) {
            participantsIds = new ArrayList<>();
        }
        participantsIds.add(uuid);
        return this;
    }
}


