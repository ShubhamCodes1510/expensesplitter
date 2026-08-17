package com.expense.expensesplitter.controller;

import com.expense.expensesplitter.dto.GroupDTO;
import com.expense.expensesplitter.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupDTO> createGroup(@Valid @RequestBody GroupDTO groupDTO, @RequestParam Long userId) {
        GroupDTO createdGroup = groupService.createGroup(groupDTO, userId);
        return new ResponseEntity<>(createdGroup, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GroupDTO>> getAllGroups() {
        List<GroupDTO> groups = groupService.getAllGroups();
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroupDTO>> getUserGroups(@PathVariable Long userId) {
        List<GroupDTO> groups = groupService.getUserGroups(userId);
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDTO> getGroupById(@PathVariable Long id) {
        GroupDTO group = groupService.getGroupById(id);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @GetMapping("/join/{inviteCode}")
    public ResponseEntity<GroupDTO> getGroupByInviteCode(@PathVariable String inviteCode) {
        GroupDTO group = groupService.getGroupByInviteCode(inviteCode);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PostMapping("/join")
    public ResponseEntity<GroupDTO> joinGroup(@RequestParam String inviteCode, @RequestParam Long userId) {
        GroupDTO group = groupService.joinGroup(inviteCode, userId);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<GroupDTO> addMember(@PathVariable Long id, @RequestParam Long userId) {
        GroupDTO group = groupService.addMember(id, userId);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PostMapping("/{id}/members/by-username")
    public ResponseEntity<GroupDTO> addMemberByUsername(@PathVariable Long id, @RequestParam String username) {
        GroupDTO group = groupService.addMemberByUsername(id, username);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<GroupDTO> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        GroupDTO group = groupService.removeMember(id, userId);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDTO> updateGroup(@PathVariable Long id, @Valid @RequestBody GroupDTO groupDTO) {
        GroupDTO group = groupService.updateGroup(id, groupDTO);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
