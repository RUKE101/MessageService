package ru.afonskiy.messenger.repository.invitation;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.afonskiy.messenger.entity.invitation.Invite;
@Repository
public interface InviteRepository extends MongoRepository<Invite, String> {
}
