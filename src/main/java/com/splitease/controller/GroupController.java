package com.splitease.controller;

import com.splitease.dto.*;
import com.splitease.model.Group;
import com.splitease.service.ExpenseService;
import com.splitease.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.splitease.dto.AddMemberRequest;
import com.splitease.dto.MembershipResponse;
import com.splitease.service.MembershipService;
import com.splitease.dto.ExpenseResponse;
import com.splitease.dto.CreateExpenseRequest;
import com.splitease.service.ExpenseService;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    private final GroupService groupService;
    private final MembershipService membershipService;
    private final ExpenseService expenseService;
    public GroupController(GroupService groupService, MembershipService membershipService, ExpenseService expenseService){
        this.groupService=groupService;
        this.membershipService=membershipService;
        this.expenseService = expenseService;
    }
    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest request){
        GroupResponse response= groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable Long id){
        GroupResponse response= groupService.getGroup(id);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{id}/members")
    public ResponseEntity<MembershipResponse> addMember(@PathVariable Long id, @Valid @RequestBody AddMemberRequest request){
        MembershipResponse response=membershipService.addMember(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/{id}/expenses")
    public ResponseEntity<ExpenseResponse> createExpense(@PathVariable Long id, @Valid @RequestBody CreateExpenseRequest request){
        ExpenseResponse response=expenseService.createExpense(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
