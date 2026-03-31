package fr.simplon.domain.gateway;

import java.util.List;
import fr.simplon.domain.models.User;
import fr.simplon.domain.models.Post;

public interface PostService {
    void checkExtensions();

    List<Post> getPostsByFeedType(String feedType, User currentUser, List<Post> allPosts);
}
