package cn.itcast.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HollerWordController {


    @Autowired
    private Environment env;

    @RequestMapping("/info")
    public String info() {
        return "你好,大哥别杀我大哥别杀我888？" + env.getProperty("url");
    }


}
