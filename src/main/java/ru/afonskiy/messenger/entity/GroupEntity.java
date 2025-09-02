package ru.afonskiy.messenger.entity;

import com.mongodb.lang.NonNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document
public class GroupEntity {
    @Id
    private String id;
    @NonNull
    private String nameOfGroup;
    private String DescriptionOfGroup;
    @CreatedDate
    private String dateOfCreation;

}