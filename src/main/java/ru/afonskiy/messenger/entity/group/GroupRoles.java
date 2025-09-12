package ru.afonskiy.messenger.entity.group;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupRoles {
    private String groupId;
    private List<String> roles;
}
