package ru.afonskiy.messenger.entity.dm;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.afonskiy.messenger.entity.MessageStatus;

@Setter
@Getter
@Document
public class MessageEntity {
    @Id
    private String id;
    private String text;
    private String sender;
    @CreatedDate
    private String timestamp;
    private String recipient;
    private MessageStatus status;

}
