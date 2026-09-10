package com.splitease.service;

import com.splitease.dto.AddMemberRequest;
import com.splitease.dto.MembershipResponse;
import com.splitease.exception.NotFoundException;
import com.splitease.model.Group;
import com.splitease.model.Membership;
import com.splitease.model.User;
import com.splitease.repository.GroupRepository;
import com.splitease.repository.MembershipRepository;
import com.splitease.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    public MembershipService(MembershipRepository membershipRepository, GroupRepository groupRepository, UserRepository userRepository){
        this.groupRepository=groupRepository;
        this.membershipRepository=membershipRepository;
        this.userRepository=userRepository;
    }
    public MembershipResponse addMember(Long groupId, AddMemberRequest request){
        //look for the group
        Group group=groupRepository.findById(groupId).orElseThrow(()->new NotFoundException("group not found: "+groupId));
        //look for the user
        User user=userRepository.findById(request.getUserId()).orElseThrow(()->new NotFoundException("User not found: "+request.getUserId()));
        //build the membership linking them
        Membership membership=new Membership();
        membership.setGroup(group);
        membership.setUser(user);
        membership.setRole(request.getRole());

        //save the row in postgres
        Membership saved=membershipRepository.save(membership);

        //entity--> response DTO
        return toResponse(saved);
    }
    private MembershipResponse toResponse(Membership m){
        MembershipResponse resp=new MembershipResponse();
        resp.setId(m.getId());
        resp.setGroupId(m.getGroup().getId());
        resp.setUserId(m.getUser().getId());
        resp.setUserName(m.getUser().getName());
        resp.setRole(m.getRole());
        resp.setJoinedAt(m.getJoinedAt());
        return resp;
    }
}
