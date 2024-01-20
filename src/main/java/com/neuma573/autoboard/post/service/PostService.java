package com.neuma573.autoboard.post.service;

import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.board.repository.BoardRepository;
import com.neuma573.autoboard.global.exception.BoardNotAccessibleException;
import com.neuma573.autoboard.global.exception.UserNotFoundException;
import com.neuma573.autoboard.post.model.dto.PostRequest;
import com.neuma573.autoboard.post.model.dto.PostResponse;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.post.repository.PostRepository;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    @Transactional
    public PostResponse savePost(String email, PostRequest postRequest) {
        Board destination = boardRepository.findById(postRequest.getBoardId()).orElseThrow(() -> new UserNotFoundException(""));
        User writer = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(""));
        if (!destination.isAccessible(writer)) {
            throw new BoardNotAccessibleException("이용할 수 없는 게시판입니다.");
        }
        Post post = postRepository.save(postRequest.of(
                destination,
                writer)
        );
        return PostResponse.of(post);
    }

}
