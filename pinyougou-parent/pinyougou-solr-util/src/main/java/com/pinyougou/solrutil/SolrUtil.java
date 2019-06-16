package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.service.pojo.TbItem;
import com.pinyougou.service.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component//默认 （"solrUtil"）context中加入Bean
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;
    public void importItemData(){

        //条件查询
        TbItemExample example= new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过的才导入
        List<TbItem> itemList = itemMapper.selectByExample(example);

        System.out.println("--商品列表--");
        for (TbItem item:itemList){
            System.out.println(item.getId()+"  "+item.getTitle()+" "+item.getPrice());
            Map specMap= JSON.parseObject(item.getSpec());//从数据库中提取规格json字符串转换为map
            item.setSpecMap(specMap);//给注解的字段赋值


        }
        //导入 把数据从数据库导入 solr数据库
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
        System.out.println("--结束--");



    }


    public static void main(String[] args) {
        //加载spring环境                                                //加* 的原因是得去 Dao配置文件 查询数据
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
        //执行方法
        solrUtil.importItemData();


    }
}
