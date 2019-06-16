package com.pinyougou.service.pojogroup;

import com.pinyougou.service.pojo.TbGoods;
import com.pinyougou.service.pojo.TbGoodsDesc;
import com.pinyougou.service.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

//商品作和实体类
public class Goods implements Serializable {
    private TbGoods goods;//商品spu基本 信息
    private TbGoodsDesc goodsDesc;//商品spu扩展信息
    private List<TbItem> itemList;//sku列表

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
