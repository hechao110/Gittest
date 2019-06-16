package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.pojo.TbGoods;
import com.pinyougou.service.GoodsService;
import com.pinyougou.service.pojogroup.Goods;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
        //获取商家Id
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //设置商家Id
        goods.getGoods().setSellerId(sellerId);
        try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
	    //当前登录的商家ID
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();

        //判断 商品是否是该商家的商品
        Goods goods2 = goodsService.findOne(goods.getGoods().getId());
        //goods2得到的是要修改的商品，在从页面得到sellerId  判断你要修改的商品的商家是否和你当前登陆的
        //商家是否一致   在判断前端传来的goods的sellerID是否和当前商家ID是否一致
        if(!goods2.getGoods().getSellerId().equals(sellerId) || !goods.getGoods().getSellerId().equals(sellerId)){
            return new Result(false, "非法操作");
        }

        try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){

	    return goodsService.findOne(id);
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){

        //获取商家ID      提供对SecurityContext的访问   获得上下文   获得安全认证   获得ID
        String sellerId=SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);



		return goodsService.findPage(goods, page, rows);		
	}
	
}
