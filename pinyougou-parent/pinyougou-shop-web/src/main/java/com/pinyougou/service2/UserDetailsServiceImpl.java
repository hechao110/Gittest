package com.pinyougou.service2;



import com.pinyougou.service.SellerService;
import com.pinyougou.service.pojo.TbSeller;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

//认证类
public class UserDetailsServiceImpl implements UserDetailsService {
    private SellerService sellerService;


    public void setSellerService(SellerService sellerService) {

        this.sellerService = sellerService;
    }

    @Override                             //这个username是用户在登录页面写的名字
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("已经经过了认证类");

        //构建一个角色列表
        List<GrantedAuthority> grantAuths = new ArrayList();
        grantAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //得到一个商家对象
        TbSeller seller = sellerService.findOne(username);
        if (seller != null) {
            if (seller.getStatus().equals("1")) {
                return new User(username, seller.getPassword(), grantAuths);
            }
            return null;
        } else {
            return null;
        }


    }
}
