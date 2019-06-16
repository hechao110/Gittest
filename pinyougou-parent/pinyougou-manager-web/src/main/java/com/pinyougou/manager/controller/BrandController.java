package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.pojo.TbBrand;
import com.pinyougou.service.BrandService;


import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        return brandService.finAll();
    }


    @RequestMapping("/findPage")
    public PageResult findPage(int page, int size) {

        return brandService.findPage(page, size);
    }


    //新增方法
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand) {

        try {

            brandService.add(brand);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }

    }


    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }


    //获取实体类
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
            return   brandService.findOne(id);
    }


    //批量删除
    @RequestMapping("delete")
    public Result delete(Long [] ids){
           try{
            brandService.delect(ids);
            return new Result(true,"删除成功");
           }catch (Exception e){
                e.printStackTrace();
                return new Result(false,"删除失败");
           }
    }
    //查询
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand, int page, int size  ){
        return brandService.findPage(brand, page, size);
    }

    //返回下拉列表数据

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() {
        return brandService.selectOptionList();
    }
}
