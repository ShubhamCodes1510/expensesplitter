package com.expense.expensesplitter.repository;

import com.expense.expensesplitter.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByInviteCode(String inviteCode);

    @Query("SELECT g FROM Group g JOIN g.members m WHERE m.id = :userId")
    List<Group> findByMemberId(Long userId);

    @Query("SELECT g FROM Group g WHERE g.createdBy.id = :userId")
    List<Group> findByCreatedById(Long userId);

    boolean existsByInviteCode(String inviteCode);
}
