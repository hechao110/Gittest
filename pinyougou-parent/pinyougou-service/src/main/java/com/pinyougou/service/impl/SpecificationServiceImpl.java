package com.pinyougou.service.impl;

import java.util.List;
import java.util.Map;

import com.pinyougou.service.SpecificationService;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.service.pojo.TbSpecificationOption;
import com.pinyougou.service.pojo.TbSpecificationOptionExample;
import com.pinyougou.service.pojogroup.Specification;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.service.pojo.TbSpecification;
import com.pinyougou.service.pojo.TbSpecificationExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import entity.PageResult;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Specification specification) {
        //获取规格的实体
        TbSpecification tbSpecification = specification.getSpecification();

        specificationMapper.insert(tbSpecification);
        //获取集合
        List<TbSpecificationOption> specificationOptionsList = specification.getSpecificationOptionList();
        for (TbSpecificationOption option : specificationOptionsList) {

            option.setSpecId(tbSpecification.getId());//设置规格ID

            specificationOptionMapper.insert(option);//新增规格
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(Specification specification) {
        //获取规格的实体
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);
        //删除原来规格对应的规格选项
        TbSpecificationOptionExample example=new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(tbSpecification.getId());
        specificationOptionMapper.deleteByExample(example);


         //获取集合
        List<TbSpecificationOption> specificationOptionsList = specification.getSpecificationOptionList();
        for (TbSpecificationOption option : specificationOptionsList) {

            option.setSpecId(tbSpecification.getId());//设置规格ID

            specificationOptionMapper.insert(option);//新增规格
        }

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {
        Specification specification = new Specification();
        //获取规格实体
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        specification.setSpecification(tbSpecification);
        //获取规格选项的列表

        TbSpecificationOptionExample example=new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(example);
        specification.setSpecificationOptionList(specificationOptionList);
        return  specification;//
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //不仅要删除规格表数据
            specificationMapper.deleteByPrimaryKey(id);
            //还要删除规格选项数据
            TbSpecificationOptionExample example=new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
        }
    }


    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        TbSpecificationExample.Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }

        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return specificationMapper.selectOptionList();
    }

}
