package ru.aafonskiy.messenger.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document
public class MessageEntity {
    @Id
    private String id;
    private String message;
    private String sender;
    @CreatedDate
    private String timestamp;
    private String recipient;
}
