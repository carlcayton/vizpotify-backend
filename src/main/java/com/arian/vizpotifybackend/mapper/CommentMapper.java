package com.arian.vizpotifybackend.mapper;


import com.arian.vizpotifybackend.dto.CommentDTO;
import com.arian.vizpotifybackend.model.Comment;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    

    @Mapping(target = "authorImageUrl", ignore = true)
    public abstract CommentDTO toDTO(Comment entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    public abstract Comment toEntity(CommentDTO dto);

    @AfterMapping
    protected void enrichDTOWithAuthorImage(Comment entity, @MappingTarget CommentDTO dto) {
        UserDetail userDetail = userService.loadUserDetailBySpotifyId(entity.getAuthorSpotifyId());
        if (userDetail != null) {
            dto.setAuthorImageUrl(userDetail.getProfilePictureUrl());
        }
    }

}
