package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.Post;

import java.util.Date;
import java.util.Set;

/**
 * Created by eirikstadheim on 06/02/16.
 */
public class PostDtoOutSmall {
    private int id;
    private UserDtoOutSmall writer;

    public PostDtoOutSmall() {
    }

    public PostDtoOutSmall(Post post) {
        id = post.getId();

        if(post.getWriter() != null) {
            writer = new UserDtoOutSmall(post.getWriter());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
