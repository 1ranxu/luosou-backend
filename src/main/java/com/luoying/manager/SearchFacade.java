package com.luoying.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luoying.common.ErrorCode;
import com.luoying.datasource.*;
import com.luoying.exception.BusinessException;
import com.luoying.model.dto.search.SearchRequest;
import com.luoying.model.entity.Picture;
import com.luoying.model.enums.SearchTypeEnum;
import com.luoying.model.vo.PostVO;
import com.luoying.model.vo.SearchVO;
import com.luoying.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class SearchFacade {
    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;

    public SearchVO searchAll(SearchRequest searchRequest, HttpServletRequest request) {
        String type = searchRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        String searchText = searchRequest.getSearchText();
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();

        SearchVO searchVO = new SearchVO();
        if (searchTypeEnum == null) {
            CompletableFuture<Page<Picture>> pictureTask =
                    CompletableFuture.supplyAsync(() -> pictureDataSource.doSearch(searchText, current, pageSize));
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                return userDataSource.doSearch(searchText, current, pageSize);
            });

            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                return postDataSource.doSearch(searchText, current, pageSize);
            });

            CompletableFuture.allOf(pictureTask, userTask, postTask).join();

            try {
                searchVO.setPictureList(pictureTask.get().getRecords());
                searchVO.setUserList(userTask.get().getRecords());
                searchVO.setPostList(postTask.get().getRecords());
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }

        } else {
            DataSource dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page page = dataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataList(page.getRecords());
        }
        return searchVO;
    }
}
