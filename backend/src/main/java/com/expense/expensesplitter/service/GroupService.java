package com.expense.expensesplitter.service;

import com.expense.expensesplitter.dto.GroupDTO;
import com.expense.expensesplitter.dto.UserDTO;
import com.expense.expensesplitter.exception.ResourceNotFoundException;
import com.expense.expensesplitter.model.Group;
import com.expense.expensesplitter.model.User;
import com.expense.expensesplitter.repository.GroupRepository;
import com.expense.expensesplitter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public GroupDTO createGroup(GroupDTO groupDTO, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", creatorId));

        Group group = Group.builder()
                .name(groupDTO.getName())
                .description(groupDTO.getDescription())
                .createdBy(creator)
                .isActive(true)
                .build();

        group.getMembers().add(creator);
        Group savedGroup = groupRepository.save(group);

        return mapToDTO(savedGroup);
    }

    public List<GroupDTO> getUserGroups(Long userId) {
        return groupRepository.findByMemberId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<GroupDTO> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public GroupDTO getGroupById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", id));
        return mapToDTO(group);
    }

    public GroupDTO getGroupByInviteCode(String inviteCode) {
        Group group = groupRepository.findByInviteCode(inviteCode.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Group", "invite code: " + inviteCode));
        return mapToDTO(group);
    }

    @Transactional
    public GroupDTO joinGroup(String inviteCode, Long userId) {
        Group group = groupRepository.findByInviteCode(inviteCode.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Group", "invite code: " + inviteCode));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (group.getMembers().contains(user)) {
            throw new IllegalArgumentException("User is already a member of this group");
        }

        group.getMembers().add(user);
        Group savedGroup = groupRepository.save(group);

        return mapToDTO(savedGroup);
    }

    @Transactional
    public GroupDTO addMemberByUsername(Long groupId, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", groupId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username: " + username));

        if (!group.getMembers().contains(user)) {
            group.getMembers().add(user);
        }

        return mapToDTO(groupRepository.save(group));
    }

    @Transactional
    public GroupDTO addMember(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!group.getMembers().contains(user)) {
            group.getMembers().add(user);
        }

        return mapToDTO(groupRepository.save(group));
    }

    @Transactional
    public GroupDTO removeMember(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        group.getMembers().remove(user);
        return mapToDTO(groupRepository.save(group));
    }

    @Transactional
    public GroupDTO updateGroup(Long id, GroupDTO groupDTO) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", id));

        if (groupDTO.getName() != null) {
            group.setName(groupDTO.getName());
        }
        if (groupDTO.getDescription() != null) {
            group.setDescription(groupDTO.getDescription());
        }

        return mapToDTO(groupRepository.save(group));
    }

    @Transactional
    public void deleteGroup(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", id));
        group.setActive(false);
        groupRepository.save(group);
    }

    private GroupDTO mapToDTO(Group group) {
        List<UserDTO> memberDTOs = group.getMembers().stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .build())
                .collect(Collectors.toList());

        return GroupDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .inviteCode(group.getInviteCode())
                .createdById(group.getCreatedBy().getId())
                .createdByName(group.getCreatedBy().getName())
                .members(memberDTOs)
                .defaultCategoryId(group.getDefaultCategory() != null ? group.getDefaultCategory().getId() : null)
                .defaultCategoryName(group.getDefaultCategory() != null ? group.getDefaultCategory().getName() : null)
                .isActive(group.isActive())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .memberCount(group.getMembers().size())
                .build();
    }
}
