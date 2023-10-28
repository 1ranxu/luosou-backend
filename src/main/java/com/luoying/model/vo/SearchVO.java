package com.luoying.model.vo;

import com.luoying.model.entity.Picture;
import lombok.Data;

import java.util.List;

/**
 * 聚合搜索
 */
@Data
public class SearchVO {
    private List<UserVO> userList;
    private List<PostVO> postList;
    private List<Picture> pictureList;
}
