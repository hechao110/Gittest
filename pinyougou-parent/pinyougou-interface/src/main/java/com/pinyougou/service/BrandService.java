package com.pinyougou.service;



import com.pinyougou.service.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

//品牌接口
public interface BrandService {
    //返回列表
    public List<TbBrand> finAll();
    public PageResult findPage(int pageNum, int pageSize);

    //增加
    public void add(TbBrand brand);
    //修改
    public void update(TbBrand brand);


    //根据ID获取实体
    public TbBrand findOne(Long id);
    //批量删除
    public void delect(Long [] ids);
    //查询方法重载
    public PageResult findPage( TbBrand brand  ,int pageNum,int pageSize);
       //返回下拉列表数据
    public List<Map> selectOptionList();


}
