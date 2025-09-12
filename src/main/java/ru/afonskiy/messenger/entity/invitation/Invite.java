package ru.afonskiy.messenger.entity.invitation;

import com.mongodb.lang.NonNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;
@Getter
@Setter
@Builder
@Document
public class Invite {

    @Id
    private String id;
    private UUID invitationUuid;
    @NonNull
    private Boolean isCountable;  // is_countable определяет будет ли ограничено число людей, которые могут перейти по
    // приглашению (это не ИИ, это Вася ком оставил😎😎😎😎😎)
    private Long joinPlaces; // это соответственно отвечает за количество людей, которые могут перейти, если
    // is_countable является true, оно должно убавлятся после каждого нового пользователя перешедшего по ссылке (не ИИ)
    @NonNull
    private String groupId;


}