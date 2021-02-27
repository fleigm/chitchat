package de.fleigm.chitchat.chat;

import de.fleigm.chitchat.users.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@DiscriminatorValue("group")
@Getter
@NoArgsConstructor
@NamedQuery(name = "GroupChat.byMember", query = "select m.chat from GroupMember m where m.user.id = ?1")
public class GroupChat extends Chat {

  private String name;

  @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
  private Set<GroupMember> members;

  public GroupChat(User owner, String name) {
    this.name = name;
    this.members = new HashSet<>();
    this.members.add(new GroupMember(owner, GroupMember.Role.ADMIN, this));
  }

  @Override
  public boolean canSendMessage(User user) {
    return isMember(user);
  }

  public boolean isMember(User user) {
    return members.stream().anyMatch(member -> member.is(user));
  }

  public void add(User user) {
    add(user, GroupMember.Role.USER);
  }

  public void add(User user, GroupMember.Role role) {
    if (!isMember(user)) {
      members.add(new GroupMember(user, role, this));
    }
  }

  public void remove(User user) {
    members.removeIf(members -> members.is(user));
  }

  public static PanacheQuery<GroupChat> findByMember(UUID id) {
    return find("#GroupChat.byMember", id);
  }
}
