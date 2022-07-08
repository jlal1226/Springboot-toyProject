package com.toyproject.competition.service;

import com.toyproject.competition.domain.Member;
import com.toyproject.competition.domain.Post;
import com.toyproject.competition.dto.PostDto;
import com.toyproject.competition.dto.PostResponseDto;
import com.toyproject.competition.dto.PostViewResponseDto;
import com.toyproject.competition.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberService memberService;
    private Authentication authentication;

    /**
     * 게시글 등록
     */
    @Transactional
    public void savePost(PostDto postDto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserDetails userDetails = (UserDetails) principal;
        String username = userDetails.getUsername();

        Member member = memberService.findMember(username);


        Post post = Post.builder()
                .content(postDto.getContent())
                .title(postDto.getTitle())
                .build();

        post.setMember(member);

        postRepository.save(post);
    }

    /**
     * 게시글 전체 조회
     */
    public List<PostResponseDto> postList() {
        List<Post> postList = postRepository.findAll();
        List<PostResponseDto> responseDtoList = new ArrayList<>();
        for (Post post : postList) {
            responseDtoList.add(PostResponseDto.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .username(post.getMember().getUsername())
                    .build()
            );
        }
        return responseDtoList;
    }

    /**
     * 게시글 상세 내용 조회
     */
    public PostViewResponseDto getPostView(Long id) {
        Optional<Post> post = postRepository.findById(id);
        PostViewResponseDto dto = null;
        if (post.isPresent()) {
            dto = PostViewResponseDto.builder()
                    .title(post.get().getTitle())
                    .content(post.get().getContent())
                    .username(post.get().getMember().getUsername())
                    .date(post.get().getModifiedDate())
                    .build();
        } else {
            dto = getPostViewResponseDto();
        }
        return dto;
    }

    public PostViewResponseDto getPostViewResponseDto() {
        PostViewResponseDto dto = PostViewResponseDto.builder()
                .title("title error")
                .content("content error")
                .username("no username")
                .build();

        return dto;
    }

    /**
     * 게시글 수정
     */

    /**
     * 게시글 삭제
     */

    /**
     * 유효성 처리 메서드
     */
    public Map<String, String> validateHandling(BindingResult bindingResult) {
        Map<String, String> validatorResult = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", fieldError.getField());
            validatorResult.put(validKeyName, fieldError.getDefaultMessage());
        }
        return validatorResult;
    }


}
