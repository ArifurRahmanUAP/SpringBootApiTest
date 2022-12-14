package com.arif.testapi.services.implamantation;

import com.arif.testapi.entities.Category;
import com.arif.testapi.entities.User;
import com.arif.testapi.entities.Post;
import com.arif.testapi.exceptions.ResourceNotFoundException;
import com.arif.testapi.payloads.PostDto;
import com.arif.testapi.payloads.Response.PostResponse;
import com.arif.testapi.repositories.CategoryRepo;
import com.arif.testapi.repositories.UserPostRepo;
import com.arif.testapi.repositories.UserRepo;
import com.arif.testapi.services.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private UserPostRepo userPostRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Override
    public PostDto createPost(PostDto postDto, int userId, int categoryId) {

        User user = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "user id", userId));
        Category category = this.categoryRepo.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("User", "user id", categoryId));

        Post post = this.modelMapper.map(postDto, Post.class);
        post.setPostDate(new Date());
        post.setUser(user);
        post.setCategory(category);

        Post newPost = this.userPostRepo.save(post);

        return this.modelMapper.map(newPost, PostDto.class);
    }

    @Override
    public PostDto updatePost(PostDto postDto, int postId, int userId) {
        Post post = this.userPostRepo.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
        User user = post.getUser();
        if (user.getId()== userId){
            post.setPostTitle(postDto.getPostTitle());
            post.setPostContent(postDto.getPostContent());
            post.setPostImage(postDto.getPostImage());
            Post updatedPost = this.userPostRepo.save(post);

            return this.modelMapper.map(updatedPost, PostDto.class);

        }
        else return this.modelMapper.map("error", PostDto.class);


    }

    @Override
    public void deletePost(int postId) {

        Post post = this.userPostRepo.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
        this.userPostRepo.delete(post);

    }

    @Override
    public PostResponse getAllPost(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Post> page = this.userPostRepo.findAll(pageable);

        List<PostDto> postDto = page.stream().map((post -> this.modelMapper.map(post, PostDto.class))).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(postDto);
        postResponse.setPageNumber(page.getNumber());
        postResponse.setPageSize(page.getSize());
        postResponse.setTotalElements(page.getTotalElements());
        postResponse.setTotalPages(page.getTotalPages());
        postResponse.setLastPage(page.isLast());

        return postResponse;
    }

    @Override
    public List<PostDto> getPostByPostId(int postId) {

        return this.userPostRepo.findById(postId).stream().map((post -> this.modelMapper.map(post, PostDto.class))).collect(Collectors.toList());
    }

    @Override
    public PostResponse getPostByUser(int userId, int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Post> page = this.userPostRepo.findAll(pageable);

        User user = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "user Id", userId));

        List<PostDto> collect = this.userPostRepo.findByUser(user).stream().map((post) -> this.modelMapper.map(post, PostDto.class)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setPageNumber(page.getNumber());
        postResponse.setPageSize(page.getSize());
        postResponse.setTotalElements(page.getTotalElements());
        postResponse.setTotalPages(page.getTotalPages());
        postResponse.setLastPage(page.isLast());
        postResponse.setPosts(collect);

        return postResponse;
    }

    @Override
    public List<PostDto> getPostByCategory(int categoryId) {

        Category category = this.categoryRepo.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));
        return this.userPostRepo.findByCategory(category).stream().map((post) -> this.modelMapper.map(post, PostDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<PostDto> searchPost(String keyword) {

        return this.userPostRepo.findByPostTitleContaining(keyword).stream().map((post -> this.modelMapper.map(post, PostDto.class))).collect(Collectors.toList());
    }
}