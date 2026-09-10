package com.splitease.service;

import com.splitease.dto.CreateExpenseRequest;
import com.splitease.dto.ExpenseResponse;
import com.splitease.dto.ShareResponse;
import com.splitease.dto.SplitInput;
import com.splitease.exception.BadRequestException;
import com.splitease.exception.NotFoundException;
import com.splitease.model.*;
import com.splitease.repository.GroupRepository;
import com.splitease.repository.UserRepository;
import com.splitease.repository.ExpenseRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

        List<ExpenseShare> shares=buildShares(expense, request);
        expense.getShares().addAll(shares);

        //save once
        Expense saved=expenseRepository.save(expense);

        //entity->DTO
        return toResponse(saved);
    }
    //split logic
    private List<ExpenseShare> buildShares(Expense expense, CreateExpenseRequest request){
        List<SplitInput> splits=request.getSplits();

        //1. compute the rupee amount for each person based on split type
        List<BigDecimal> amounts=switch (request.getSplitType()){
            case EQUAL -> splitEqually(request.getAmount(), splits.size());
            case EXACT -> splitExact(request.getAmount(), splits);
            case PERCENT -> splitPercent(request.getAmount(), splits);
        };

        //2. turn each (user, amount) into an expenseshare linked to expense
        List<ExpenseShare> shares=new ArrayList<>();
        for(int i=0; i<splits.size(); i++){
            Long userId=splits.get(i).getUserId();
            User member=userRepository.findById(userId).orElseThrow(()-> new NotFoundException("user not found: "+userId));

            ExpenseShare share=new ExpenseShare();
            share.setExpense(expense);
            share.setUser(member);
            share.setShareAmount(amounts.get(i));
            shares.add(share);
        }
        return shares;
    }

    private List<BigDecimal> splitEqually(@NotNull @Positive BigDecimal amount, int n) {
        BigDecimal each=amount.divide(BigDecimal.valueOf(n), 2, RoundingMode.DOWN);
        List<BigDecimal> result=new ArrayList<>();
        BigDecimal running=BigDecimal.ZERO;
        for (int i=0; i<n; i++){
            result.add(each);
            running=running.add(each);
        }
        //add the leftover paisa to the last guy so total matches the exact amount.
        BigDecimal leftOver=amount.subtract(running);
        result.set(n-1, result.get(n-1).add(leftOver));
        return result;
    }

    private List<BigDecimal> splitExact(BigDecimal amount, List<SplitInput> splits) {
        List<BigDecimal> result=new ArrayList<>();
        BigDecimal sum=BigDecimal.ZERO;
        for (SplitInput s: splits){
            if(s.getValue()==null){
                throw new BadRequestException("Each split needs a value (amount) for exact split");
            }
            result.add(s.getValue());
            sum=sum.add(s.getValue());
        }
        if(sum.compareTo(amount)!=0){
            throw new BadRequestException("Exact shares mush sum to "+amount+"but summed to "+sum);
        }
        return result;
    }

    private List<BigDecimal> splitPercent(BigDecimal amount, List<SplitInput> splits) {
        BigDecimal pctSum=BigDecimal.ZERO;
        for (SplitInput s: splits){
            if(s.getValue()==null){
                throw new BadRequestException("Each split needs a value (percentage) for percent split");
            }
            pctSum=pctSum.add(s.getValue());
        }
        if(pctSum.compareTo(BigDecimal.valueOf(100))!=0){
            throw new BadRequestException("Percentages must sum to 100 but summed to "+pctSum);
        }
        List<BigDecimal> result=new ArrayList<>();
        BigDecimal running=BigDecimal.ZERO;
        for (SplitInput s:splits){
            BigDecimal share=amount.multiply(s.getValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);
            result.add(share);
            running=running.add(share);
        }
        BigDecimal leftOver=amount.subtract(running);
        result.set(result.size()-1, result.get(result.size() -1).add(leftOver));
        return result;
    }

    //entity -> DTO conversion
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
