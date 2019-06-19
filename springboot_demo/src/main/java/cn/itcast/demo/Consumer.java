package cn.itcast.demo;
//消息消费者

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class Consumer {
//监听   active 的那个名字
    @JmsListener(destination = "hecaho")
    public void  readMessage(String text){
        System.out.println("接收到消息"+text);
    }


    @JmsListener(destination = "map集合")
    public void readSendMap(Map map){
        System.out.println("Map的消息"+map);
    }

    @JmsListener(destination = "List集合")
    public void readSendList(List list){
        System.out.println("list的消息"+list);
    }

}
