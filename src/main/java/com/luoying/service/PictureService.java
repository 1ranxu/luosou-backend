package com.luoying.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luoying.model.entity.Picture;

import java.util.List;

/**
 * 图片服务
 */
public interface PictureService {
    /**
     * 搜搜图片
     * @param current
     * @param pageSize
     * @param searchText
     * @return
     */
    Page<Picture> searchPicture(long current, long pageSize, String searchText);
}
