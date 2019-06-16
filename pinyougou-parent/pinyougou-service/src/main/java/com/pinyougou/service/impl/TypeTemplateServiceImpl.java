package com.pinyougou.service.impl;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.service.TypeTemplateService;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.service.pojo.TbSpecificationOption;
import com.pinyougou.service.pojo.TbSpecificationOptionExample;
import com.pinyougou.service.pojo.TbTypeTemplate;
import com.pinyougou.service.pojo.TbTypeTemplateExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        TbTypeTemplateExample.Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }

        }

        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);

        //缓存处理
        saveToRedis();


        return new PageResult(page.getTotal(), page.getResult());
    }
    @Autowired
    private RedisTemplate redisTemplate;
    private void saveToRedis(){
        //查询所有的模板数据
        List<TbTypeTemplate> templateList = findAll();


        for (TbTypeTemplate template:templateList){
            //得到品牌列表
            List brandList= JSON.parseArray(template.getBrandIds(),Map.class);
            redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
            //得到规格列表
            List<Map> specList = findSpecList(template.getId());
            redisTemplate.boundHashOps("specList").put(template.getId(),specList);

        }
        System.out.println("缓存品牌列表111");

    }





    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Override
    public List<Map> findSpecList(Long id) {
        //首先查询模板
        TbTypeTemplate template = typeTemplateMapper.selectByPrimaryKey(id);
        String specIds = template.getSpecIds();
        //得到比如：[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        //将specIds转换为集合
        List<Map> list = JSON.parseArray(specIds, Map.class);
        //遍历list
        for (Map map : list) {
            //获得options:[{},{}]

            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            // tb_specification表中的id的外键是tb_specification_option中的spec_id
            //spec_id这个外键就是tb_type_template specs_ids中的id

            criteria.andSpecIdEqualTo(new Long((Integer) map.get("id")));
            //所以在tb_specification_option中根据spec_id去查出options
            List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);//条件查询

            //添加到list
            map.put("options", options);
        }

        return list;
    }

}
