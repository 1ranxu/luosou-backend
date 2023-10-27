package com.luoying.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.luoying.annotation.AuthCheck;
import com.luoying.common.BaseResponse;
import com.luoying.common.DeleteRequest;
import com.luoying.common.ErrorCode;
import com.luoying.common.ResultUtils;
import com.luoying.constant.UserConstant;
import com.luoying.exception.BusinessException;
import com.luoying.exception.ThrowUtils;
import com.luoying.model.dto.picture.PictureQueryRequest;
import com.luoying.model.dto.post.PostAddRequest;
import com.luoying.model.dto.post.PostEditRequest;
import com.luoying.model.dto.post.PostQueryRequest;
import com.luoying.model.dto.post.PostUpdateRequest;
import com.luoying.model.entity.Picture;
import com.luoying.model.entity.Post;
import com.luoying.model.entity.User;
import com.luoying.model.vo.PostVO;
import com.luoying.service.PictureService;
import com.luoying.service.PostService;
import com.luoying.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 图片接口
 */
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {
    @Resource
    private PictureService pictureService;

    /**
     * 分页获取图片
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                           HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long pageSize = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<Picture> pictureList = pictureService.searchPicture(current, pageSize, pictureQueryRequest.getSearchText());
        return ResultUtils.success(pictureList);
    }
}
