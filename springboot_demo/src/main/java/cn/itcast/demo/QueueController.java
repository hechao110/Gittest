package cn.itcast.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//消息生产者
@RestController
public class QueueController {
    //注入模板  作用发送模板
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @RequestMapping("/send")
    public void send(String text){

        jmsMessagingTemplate.convertAndSend("hecaho",text);

    }

    //发送Map消息
    @RequestMapping("/sendMap")
    public void sendMap(){
        Map map =new HashMap();
        map.put("name","何超");
        map.put("age",24);
        jmsMessagingTemplate.convertAndSend("map集合",map);
    }

    @RequestMapping("/sendList")
    public void sendList(){
        List list=new ArrayList();
        list.add("刘备");
        list.add("关羽");
        list.add("张飞");
        jmsMessagingTemplate.convertAndSend("List集合",list);


    }




}
