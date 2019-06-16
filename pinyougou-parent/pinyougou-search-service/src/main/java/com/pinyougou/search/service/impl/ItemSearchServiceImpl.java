package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.service.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 8000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map search(Map searchMap) {

        Map map = new HashMap();
//        Map searchList = searchList(searchMap);
//        map.putAll(searchList);
        //1查询列表
        map.putAll(searchList(searchMap));
        //2分组查询商品分类列表
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);
        //3根据商品分类名称  去  查询品牌和规格列表
        //首先从前端searchaMap中获取商品名称  然后zai 执行查询
        String category = (String) searchMap.get("category");
        if (!category.equals("")) {
            map.putAll(searchBrandAndSpecList(category));
        } else {
            if (categoryList.size() > 0) {

                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }


        return map;


    }


    //查询列表
    private Map searchList(Map searchMap) {
        Map map = new HashMap();
        //设置高亮
        HighlightQuery query = new SimpleHighlightQuery();
        //构建高亮选项  对象
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//高亮域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//前缀
        highlightOptions.setSimplePostfix("</em>");//后缀
        query.setHighlightOptions(highlightOptions);//为查询对象设置高亮选项


        //关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //商品分类过滤
        if (!"".equals(searchMap.get("category"))) {
            FilterQuery filterQuery = new SimpleFacetQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //品牌分类过滤
        if (!"".equals(searchMap.get("brand"))) {
            FilterQuery filterQuery = new SimpleFacetQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //规格过滤
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                FilterQuery filterQuery = new SimpleFacetQuery();
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //价格过滤
        if (!"".equals(searchMap.get("price"))) {
            String priceStr = (String) searchMap.get("price");//格式   0-500,=，  比如x-y  x=price[0]  y=price[1]
            //拆分
            String[] price = priceStr.split("-");
            if (!price[0].equals("0")) {      //如果最低价格不等于0
                FilterQuery filterQuery = new SimpleFacetQuery();//.greaterThanEqual(price[0]);大于等于price[0]的意思
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);

            }
            if (!price[1].equals("*")) {      //如果最高价格不等于*
                FilterQuery filterQuery = new SimpleFacetQuery();//.lessThanEqual(price[1]);小于等于price[1]的意思
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);

            }


        }
        //分页
        //获取页码
        Integer pageNo = (Integer) searchMap.get("pageNo");
        //设置默认页数
        if (pageNo == null) {
            pageNo = 1;
        }
        //获取每页数据的数量
        Integer pageSize = (Integer) searchMap.get("pageSize");
        //设置每页数据的默认数量
        if (pageSize == null) {
            pageSize = 10;
        }

        query.setOffset((pageNo - 1) * pageSize);//每页数据 的起始索引
        query.setRows(pageSize);//每页数量总记录数

        //排序
        // ASC上升的意思   Direction排序的意思  DESC下降的意思

        //获取升序  降序
        String sortValue = (String) searchMap.get("sort");
        //获取排序字段
        String sortField = (String) searchMap.get("sortField");

        if (sortValue != null && !sortValue.equals("")) {// 如果不是空字符就执行以下方法  如果是空的 就不执行 那就 乱排序
            if (sortValue.equals("ASC")) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }


        }


        //高亮页对象
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);//返回一个高亮页对象

        //高亮入口集合
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
        for (HighlightEntry<TbItem> entry : entryList) {
            List<HighlightEntry.Highlight> highlightList = entry.getHighlights();
            /*
            for (HighlightEntry.Highlight h:highlightList){
                List<String> sns = h.getSnipplets();
                System.out.println(sns);
                }
                */
            //获取实体
            if (highlightList.size() > 0 && highlightList.get(0).getSnipplets().size() > 0) {
                TbItem item = entry.getEntity();
                item.setTitle(highlightList.get(0).getSnipplets().get(0));
            }


        }

        map.put("rows", page.getContent());
        map.put("total", page.getTotalElements());//总记录数
        map.put("totalPages", page.getTotalPages());//总页数

        return map;
    }


    //分组查询（查询分类列表）
    private List<String> searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList();
        Query query = new SimpleQuery("*:*");
        //关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");

        query.setGroupOptions(groupOptions);
        //获取分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);

        //这一步很重要
        //获取分组结果对象
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");//必须是域名称
        //获取分组入口页  得到的是一个对象 得到具体的数据
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //获取分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {


            System.out.println(entry.getGroupValue());

            list.add(entry.getGroupValue());//将分组的结果添加到返回值中


        }


        return list;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    //根据商品分类名称  去  查询品牌和规格列表
    private Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();

        //根据商品分类名称得到模板ID
        Long templateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (templateId != null) {
            //根据模板ID得到品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
            map.put("brandList", brandList);
            //根据模板ID获取规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
            map.put("specList", specList);
        }


        return map;
    }

    //导入sku列表数据
    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIds) {

        Query query = new SimpleQuery("");
        Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();

    }


}
