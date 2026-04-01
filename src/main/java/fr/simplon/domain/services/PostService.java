package fr.simplon.domain.services;

import java.util.List;
import fr.simplon.domain.models.User;
import fr.simplon.domain.models.AttachmentType;
import fr.simplon.domain.models.Post;

public interface PostService {

    boolean checkExtension(String extension);

    List<Post> getPostsByFeedType(String feedType, User currentUser, List<Post> allPosts);

    void createPost(String content, String mediaUrl, User owner, AttachmentType attachmentType);

    void toggleLike(long postId, long userId);

    void addComment(long postId, User author, String content);

    void followUser(long targetUserId, User currentUser);

}
