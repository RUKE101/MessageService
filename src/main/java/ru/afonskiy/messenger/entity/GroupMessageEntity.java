package ru.afonskiy.messenger.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document
public class GroupMessageEntity {
    @Id
    private String id;
    @CreatedDate
    private LocalDateTime createdDate;
    private String sender;
    private String message;
}
