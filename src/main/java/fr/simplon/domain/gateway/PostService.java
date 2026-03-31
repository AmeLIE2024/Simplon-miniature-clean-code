package fr.simplon.domain.gateway;

import java.util.List;
import fr.simplon.domain.models.User;
import fr.simplon.domain.models.Post;

public interface PostService {
    void checkExtensions();

    List<Post> getPostsByFeedType(String feedType, User currentUser, List<Post> allPosts);

    void createPost(Post post);

    void toggleLike(long postId, User currentUser);

    void addComment(long postId, User author, String content);
}
