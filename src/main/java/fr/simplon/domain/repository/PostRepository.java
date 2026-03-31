package fr.simplon.domain.repository;

import java.util.List;

import fr.simplon.domain.models.Post;
import fr.simplon.domain.models.User;

public interface PostRepository {
    List<Post> getPostsByFeedType(String feedType, User currentUser, List<Post> allPosts);
}
