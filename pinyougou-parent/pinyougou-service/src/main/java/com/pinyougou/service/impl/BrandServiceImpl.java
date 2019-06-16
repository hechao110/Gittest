package com.pinyougou.service.impl;



import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.service.pojo.TbBrand;
import com.pinyougou.service.pojo.TbBrandExample;
import com.pinyougou.service.BrandService;

import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> finAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
//分页插件  SqlMapConfig.xml下<plugin interceptor="com.github.pagehelper.PageHelper">
        PageHelper.startPage(pageNum,pageSize);
        //查询所有的数据
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);

        return new PageResult(page.getTotal(),page.getResult());
    }
  //实现增加方法
    @Override
    public void add(TbBrand brand) {

        brandMapper.insert(brand);
    }
//修改
    @Override
    public void update(TbBrand brand) {

        brandMapper.updateByPrimaryKey(brand);
    }
//根据实体获取ID
    @Override
    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }
//批量删除
    @Override
    public void delect(Long[] ids) {
        //遍历id
        for (Long id:ids){
            //得到id后进行删除操作
            brandMapper.deleteByPrimaryKey(id);
        }

    }
//查询
    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {

        //分页插件  SqlMapConfig.xml下<plugin interceptor="com.github.pagehelper.PageHelper">
        PageHelper.startPage(pageNum,pageSize);
        //查询所有的数据
        TbBrandExample example=new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if(brand!=null){
            if(brand.getName()!=null && brand.getName().length()>0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if(brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);

        return new PageResult(page.getTotal(),page.getResult());
    }
    //返回下拉列表数据
    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
