package com.luoying.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luoying.common.BaseResponse;
import com.luoying.common.ErrorCode;
import com.luoying.common.ResultUtils;
import com.luoying.exception.BusinessException;
import com.luoying.model.dto.post.PostQueryRequest;
import com.luoying.model.dto.search.SearchRequest;
import com.luoying.model.dto.user.UserQueryRequest;
import com.luoying.model.entity.Picture;
import com.luoying.model.vo.PostVO;
import com.luoying.model.vo.SearchVO;
import com.luoying.model.vo.UserVO;
import com.luoying.service.PictureService;
import com.luoying.service.PostService;
import com.luoying.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 聚合接口
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {
    @Resource
    private PictureService pictureService;

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    /**
     * 聚合
     *
     * @param searchRequest
     * @return
     */
    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String searchText = searchRequest.getSearchText();
        CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> pictureService.searchPicture(1, 10, searchText));
        CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
            UserQueryRequest userQueryRequest = new UserQueryRequest();
            userQueryRequest.setCurrent(1);
            userQueryRequest.setPageSize(10);
            userQueryRequest.setUserName(searchText);
            return userService.listUserVOByPage(userQueryRequest);
        });

        CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
            PostQueryRequest postQueryRequest = new PostQueryRequest();
            postQueryRequest.setSearchText(searchText);
            postQueryRequest.setTitle(searchText);
            postQueryRequest.setCurrent(1);
            postQueryRequest.setPageSize(10);

            return postService.listPostVOByPage(postQueryRequest, request);
        });

        CompletableFuture.allOf(pictureTask, userTask, postTask).join();


        SearchVO searchVO = new SearchVO();
        try {
            searchVO.setPictureList(pictureTask.get().getRecords());
            searchVO.setUserList(userTask.get().getRecords());
            searchVO.setPostList(postTask.get().getRecords());
        } catch (Exception e) {
            log.error("查询异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        return ResultUtils.success(searchVO);
    }
}
