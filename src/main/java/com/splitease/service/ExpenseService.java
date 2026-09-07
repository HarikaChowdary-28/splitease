package com.splitease.service;

import com.splitease.dto.CreateExpenseRequest;
import com.splitease.dto.ExpenseResponse;
import com.splitease.dto.ShareResponse;
import com.splitease.exception.NotFoundException;
import com.splitease.model.*;
import com.splitease.repository.GroupRepository;
import com.splitease.repository.UserRepository;
import com.splitease.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository, GroupRepository groupRepository){
        this.expenseRepository=expenseRepository;
        this.groupRepository=groupRepository;
        this.userRepository=userRepository;
    }
    public ExpenseResponse createExpense(Long groupId, CreateExpenseRequest request){
        // get group and payer
        Group group=groupRepository.findById(groupId).orElseThrow(()->new NotFoundException("Group not found: " +groupId));
        User payer=userRepository.findById(request.getPaidBy()).orElseThrow(()->new NotFoundException("user not found:"+request.getPaidBy()));

        //build the expense
        Expense expense=new Expense();
        expense.setGroup(group);
        expense.setPaidBy(payer);
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setSplitType(request.getSplitType());

        //equal split divide amount evenly
        int n=request.getParticipantIds().size();
        BigDecimal perHead=request.getAmount().divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);

        //build a share for each participant , linked to the expense
        for(Long userId: request.getParticipantIds()){
            User member=userRepository.findById(userId).orElseThrow(()->new NotFoundException("user not found:"+userId));
            ExpenseShare share=new ExpenseShare();
            share.setExpense(expense); //sets the FK
            share.setUser(member);
            share.setShareAmount(perHead);
            expense.getShares().add(share); //parent--> holds child in its list
        }
        //save once
        Expense saved=expenseRepository.save(expense);

        //entity->DTO
        return toResponse(saved);
    }
    private ExpenseResponse toResponse(Expense e){
        ExpenseResponse resp=new ExpenseResponse();
        resp.setId(e.getId());
        resp.setGroupId(e.getGroup().getId());
        resp.setPaidById(e.getPaidBy().getId());
        resp.setDescription(e.getDescription());
        resp.setPaidByName(e.getPaidBy().getName());
        resp.setAmount(e.getAmount());
        resp.setDescription(e.getDescription());
        resp.setSplitType(e.getSplitType());
        resp.setCreatedAt(e.getCreatedAt());

        List<ShareResponse> shareResponses=new ArrayList<>();
        for(ExpenseShare share: e.getShares()){
            ShareResponse sr=new ShareResponse();
            sr.setUserId(share.getUser().getId());
            sr.setUserName(share.getUser().getName());
            sr.setShareAmount(share.getShareAmount());
            shareResponses.add(sr);
        }
        resp.setShares(shareResponses);
        return resp;
    }
}
