package com.pinyougou.page.sevice;

public interface ItemPageService {
    //生成商品详细页
    public boolean genItemHtml(Long goodsId);


    public boolean deleteItemHtml(Long[] goodsIds);
}
