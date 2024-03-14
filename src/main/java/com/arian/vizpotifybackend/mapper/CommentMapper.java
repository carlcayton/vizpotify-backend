package com.arian.vizpotifybackend.mapper;


import com.arian.vizpotifybackend.dto.CommentDTO;
import com.arian.vizpotifybackend.model.Comment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Mapping(target = "authorImageUrl", ignore = true)
    public abstract CommentDTO toDTO(Comment entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    public abstract Comment toEntity(CommentDTO dto);
}
