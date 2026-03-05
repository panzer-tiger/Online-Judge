package com.oj.friend.service.exam.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oj.common.core.domain.TableDataInfo;
import com.oj.friend.domain.exam.dto.ExamQueryDTO;
import com.oj.friend.domain.exam.vo.ExamVO;
import com.oj.friend.manager.ExamCacheManager;
import com.oj.friend.mapper.exam.ExamMapper;
import com.oj.friend.service.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class ExamServiceImpl implements ExamService {
    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private ExamCacheManager examCacheManager;
    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(),examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }

    @Override
    public TableDataInfo redisList(ExamQueryDTO examQueryDTO) {
        Long total = examCacheManager.getListSize(examQueryDTO.getType(), null);
        List<ExamVO> examVOList=new ArrayList<>();
        if(total==null||total<=0){
            //redis查不到数据时就往其中添加数据库中的数据
            examVOList = list(examQueryDTO);
            //查完之后更新redis中的信息
            examCacheManager.refreshCache(examQueryDTO.getType(),null);
        }
        else {
            //从redis中获取竞赛列表
            examVOList=examCacheManager.getExamVOList(examQueryDTO,null);
            //重新获取总数,防止之前redis存储的是错误数据
            total=examCacheManager.getListSize(examQueryDTO.getType(),null);
        }
        if(CollectionUtil.isEmpty(examVOList)){
            //无查询结果返回一个空的列表
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(examVOList,total);
    }


}
