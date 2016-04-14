package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.entities.Post;
import com.andreasogeirik.model.entities.Comment;

import java.util.List;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface PostDao {
    Post findById(int id);
    Post userPost(String message, String imageUri, int writerId, int userId);
    Post eventPost(String message, String imageUri, int writerId, int eventId);
    void removePost(int id, int userId);
    Comment comment(Comment comment, int postId, int userId);
    void removeComment(int id, int userId);
    void likePost(int postId, int userId);
    void likeComment(int postId, int userId);
    void removeCommentLike(int commentId, int userId);
    void removePostLike(int postId, int userId);

    List<Post> findPostsUser(int userId, int start, int quantity);
    List<Post> findPostsEvent(int eventId, int start, int quantity);
}
