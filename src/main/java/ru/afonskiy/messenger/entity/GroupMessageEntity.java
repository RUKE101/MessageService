package ru.afonskiy.messenger.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class GroupMessageEntity {
    @Id
    private String id;
    private String text;
    private String sender;
    @CreatedDate
    private String timestamp;
    private String groupId;
}
