package ru.afonskiy.messenger.entity;

import com.mongodb.lang.NonNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
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
    @Builder.Default
    private List<String> participantsIds = new ArrayList<>();

    public void  addParticipantId(String uuid) {
        if (this.getParticipantsIds() == null) {
            this.setParticipantsIds(new ArrayList<>());
        }
        this.participantsIds.add(uuid);
    }
}

