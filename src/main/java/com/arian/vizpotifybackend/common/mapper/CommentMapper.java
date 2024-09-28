package com.arian.vizpotifybackend.common.mapper;


import com.arian.vizpotifybackend.comment.Comment;
import com.arian.vizpotifybackend.comment.CommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Mapping(target = "authorImageUrl", ignore = true)
    public abstract CommentDto toDto(Comment entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    public abstract Comment toEntity(CommentDto dto);
}
