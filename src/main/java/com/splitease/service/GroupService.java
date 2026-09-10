package com.splitease.service;

import com.splitease.dto.CreateGroupRequest;
import com.splitease.dto.GroupResponse;
import com.splitease.exception.NotFoundException;
import com.splitease.model.Group;
import com.splitease.model.User;
import com.splitease.repository.GroupRepository;
import com.splitease.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.splitease.exception.NotFoundException;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    public GroupService(GroupRepository groupRepository, UserRepository userRepository){
        this.groupRepository=groupRepository;
        this.userRepository=userRepository;
    }
    public GroupResponse createGroup(CreateGroupRequest request){
        //look for user who created the group
        User creator=userRepository.findById(request.getCreatedBy()).orElseThrow(()->new NotFoundException("User not found: "+request.getCreatedBy()));

        //dto --> entity conversion for groups table
        Group group=new Group();
        group.setName(request.getName());
        group.setCreatedBy(creator); //actual user object not id

        //save this row in postgres(created_by is FK)
        Group saved=groupRepository.save(group);

        //entity-> response dto
        return toResponse(saved);
    }
    public GroupResponse getGroup(Long id){
        Group group=groupRepository.findById(id).orElseThrow(()->new NotFoundException("Group not found:"+id));
        return toResponse(group);
    }
    private GroupResponse toResponse(Group group){
        GroupResponse resp=new GroupResponse();
        resp.setId(group.getId());
        resp.setName(group.getName());
        resp.setCreatedById(group.getCreatedBy().getId());
        resp.setCreatedByName(group.getCreatedBy().getName());
        resp.setCreatedAt(group.getCreatedAt());
        return resp;
    }
}
