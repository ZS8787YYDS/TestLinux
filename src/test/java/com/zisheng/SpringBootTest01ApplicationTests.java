package com.zisheng;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class SpringBootTest01ApplicationTests {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(SpringBootTest01ApplicationTests.class);
    @Test
    public void test_create_file()
    {
        File file = new File("D:/sh");
        boolean mkdir = file.mkdir();
        if(mkdir) log.info("文件创建成功！！！");
        else log.info("文件创建失败");
    }

    /**
     * 使用Jedis操作redis数据库
     */
    @Test
    public void test_jedis()
    {
        log.info("开始采用jedis操作redis数据库！！！");
        try (
                // 获取链接,放在try里面，会自己释放连接
                Jedis jedis = new Jedis("192.168.59.130",6379);
                ){
            log.info("连接成功！！！");
            // 设置密码，这一步是可以进行选择的，如何没有设置密码的话，这一步不需要进行设置
            jedis.auth("8787521");
            // 执行操作
            jedis.set("username","zishengyyds");
            String result = jedis.get("username");
            log.info("result:" + result);
            log.info("===================获取指定key的全部字段和值=================");
            Map<String, String> myMap = jedis.hgetAll("2002");
            myMap.forEach((k,v) -> log.info(k + "-->" + v));
            log.info("==========================================================");
        } catch (Exception e) {
            String message = e.getMessage();
            log.error(message);
        }
    }

    /**
     * 操作简单的String类型的数据
     */
    @Test
    public void test_opsForValue()
    {
        log.info("测试spring_data_redis开始启动！！！");
        // 获取操作对应数据类型的对象
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 设置值
        valueOperations.set("address","China");
        valueOperations.set("hobby","Java");
        // 获取值
        String address = (String) valueOperations.get("address");
        log.info("address:{}",address);
        // 设置key的值，且设置key的过期时间
        valueOperations.set("animal","Dog",25L, TimeUnit.SECONDS);
        // 当key不存在时设置key的值
        Boolean aBoolean = valueOperations.setIfAbsent("address", "America");
        log.info(aBoolean.toString());
        Boolean aBoolean1 = valueOperations.setIfAbsent("state", "America");
        log.info(aBoolean1.toString());
    }
    /**
     * 测试Hash类型的数据
     */
    @Test
    public void test_opsForHash()
    {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        // 向哈希表中添加值
        hashOperations.put("2002","score","99.9");
        hashOperations.put("2002","sc","啥也不是");
        // 获取指定键对应的值
        String scoreValue = (String) hashOperations.get("2002", "score");
        log.info("score:" + scoreValue);
        log.info("==========================");
        // 删除值
        Long sc = hashOperations.delete("2002", "sc");
        log.info("sc:{}",sc);
        log.info("==========================");
        // 获取哈希表所有字段
        Set<Object> keys = hashOperations.keys("2002");
        keys.forEach(o -> log.info(o.toString()));
        log.info("==========================");
        // 获取键为2002的哈希表中所有子字段的值
        List<Object> value = hashOperations.values("2002");
        value.forEach(o -> log.info(o.toString()));
        log.info("==========================");
        // 获取哈希表所有字段和值
        Map<Object, Object> entries = hashOperations.entries("2002");
        entries.forEach((k,v) -> log.info(k + "-->" + v));
        log.info("==========================");
    }
    /**
     * 操作List列表类型的数据
     */
    @Test
    public void test_opsForList()
    {
        log.info("操作list列表类型的数据");
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        // 向列表当中插入单个值
        listOperations.leftPush("myList","1");
        // 向列表当中插入多个值
        listOperations.leftPushAll("myList","2","3","4");
        // 获取列表当中指定范围内的元素，返回一个List集合。末尾索引设置为-1表示获取全部的元素
        List<Object> myList = listOperations.range("myList", 0, -1);
        if (myList != null) {
            myList.forEach(o -> log.info(o.toString()));
        }
        else log.info("列表为空！！！");
        log.info("==========================");
        // 移除并且获取列表当中最后一个元素
        String finalValue = (String) listOperations.leftPop("myList");
        log.info("最后一个元素为：" + finalValue);
        log.info("==========================");
        // 获取列表的长度
        Long length = listOperations.size("myList");
        log.info("列表的长度为：{}",length);
        log.info("==========================");
        // 移出并且获取列表的最后一个元素，如果列表没有元素会进行阻塞直至等待超时或者发现可弹出的元素为止。
        String ultimateValue = (String) listOperations.leftPop("myList", 10, TimeUnit.SECONDS);
        log.info("最后一个元素为：{}",ultimateValue);
        log.info("==========================");
        //出队操作
        // 获取列表的长度
        Long lenOfMyList = listOperations.size("myList");
        if(lenOfMyList != null)
        {
            for(int i = 0;i < lenOfMyList; i++)
            {
                String value = (String) listOperations.rightPop("myList");
                log.info("出队的元素为：{}",value);
            }
            return;
        }
        log.info("列表为空");
    }
    /**
     * 操作无序集合Set集合类型的数据
     */
    @Test
    public void test_opsForSet()
    {
        log.info("操作无序set集合中的数据");
        // 得到操作无序集合的对象
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        // 添加元素
        setOperations.add("mySet_01","a","b","c","d");
        // 获取集合的元素大小
        Long lenOfSet = setOperations.size("mySet_01");//4
        log.info("集合中元素的大小为：{}",lenOfSet);
        log.info("==========================");
        // 获取集合中的全部元素
        Set<Object> mySet = setOperations.members("mySet_01");
        if(mySet != null)
        {
            for(Object value : mySet)
            {
                log.info(value.toString());
            }
        }// a b c d
        log.info("==========================");
        setOperations.add("mySet_02","a","b","d","e");
        Set<Object> intersect = setOperations.intersect("mySet_01", "mySet_02");
        log.info("==========================");
        log.info("求交集");
        if(!ObjectUtils.isEmpty(intersect))
        {
            Iterator<Object> iterator = intersect.iterator();
            // 迭代器遍历
            while(iterator.hasNext())
            {
                String value = (String) iterator.next();
                log.info(value);
            }
        }// a b d
        log.info("求并集");
        log.info("==========================");
        Set<Object> union = setOperations.union("mySet_01", "mySet_02");
        if(!ObjectUtils.isEmpty(union))
        {
            for(Object value : union)
            {
                log.info(value.toString());
            }
        }// a b c d e
        log.info("求差集");
        Set<Object> difference = setOperations.difference("mySet_01", "mySet_02");
        log.info("==========================");
        if(!ObjectUtils.isEmpty(difference))
        {
            difference.forEach( o -> log.info(o.toString())); // c
        }
        // 删除对应的键，避免下次进行追加数据
        Collection<String> collection =  new ArrayList<>();
        Collections.addAll(collection,"mySet_01","mySet_02");
        redisTemplate.delete(collection);
    }
    /**
     * 操作有序集合Sorted Set类型的数据
     */
    @Test
    public void test_sorted_set()
    {
        log.info("操作有序集合：sortedSet ");
        // 获取操作无序集合的对象
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        // 向集合当中插入元素，并且指定关联分数
        zSetOperations.add("mySortedSet","zhangsan",1.1);
        zSetOperations.add("mySortedSet","lisi",1.0);
        zSetOperations.add("mySortedSet","wangwu",1.2);
        // 获取集合的元素大小
        Long size = zSetOperations.size("mySortedSet");
        log.info("集合元素大小为：{}",size);
        // 获取集合当中指定范围的元素
        Set<Object> mySortedSet1 = zSetOperations.range("mySortedSet", 0, -1);
        log.info("==========================");
        if(!ObjectUtils.isEmpty(mySortedSet1))
        {
            mySortedSet1.forEach(o -> log.info(o.toString()));
        }
        log.info("==========================");
        // 为集合中某个元素添加分值
        zSetOperations.incrementScore("mySortedSet","lisi",1.0);
        // 获取集合当中指定范围的元素,并且输出其分数
        Set<ZSetOperations.TypedTuple<Object>> mySortedSet2 = zSetOperations.rangeWithScores("mySortedSet", 0, -1);
        log.info("==========================");
        if(!ObjectUtils.isEmpty(mySortedSet2))
        {
            mySortedSet2.forEach(o -> log.info(o.toString()));
        }
        // 移除有序集合中一个或者多个成员
        Long remove = zSetOperations.remove("mySortedSet", "zhangsan", "lisi");
        log.info(zSetOperations.size("mySortedSet") + " ");
        // 删除集合，避免下次堆积
        Boolean mySortedSet = redisTemplate.delete("mySortedSet");
        log.info(mySortedSet+" ");
    }

    /**
     * 测试通用的操作
     */
    @Test
    public void testRedisTemplate()
    {
        Set<String> keys = redisTemplate.keys("*");
        // 获取redis中所有的键
        log.info("==========================");
        if(!ObjectUtils.isEmpty(keys))
        {
            for(String value : keys)
            {
                log.info(value);
            }
        }
        log.info("==========================");
        // 判断某个键是否存在
        Boolean exists = redisTemplate.hasKey("2004");
        log.info(exists + " ");
        log.info("==========================");
        // 删除某个键
        Boolean flag_01 = redisTemplate.delete("set_01");
        Boolean flag_02 = redisTemplate.delete("set_02");
        log.info(flag_01 + " ");
        log.info(flag_02 + " ");
        // 查看某个键的类型
        DataType type = redisTemplate.type("2002");
        log.info("键为2002的数据的类型为：{}",type);
    }
    /**
     * java中执行命令行中的可执行文件
     */
    @Test
    public void test_File_Exe()
    {
        log.info("在java程序中运行命令行中可以执行的可执行文件");
        String command = "QQMusic.exe";
//        // 第一种方式：通过RunTime类
//        
//        try {
//            Process process = Runtime.getRuntime().exec(command);
//            int exitCode = process.waitFor();
//            log.info(exitCode == 0 ? "命令执行成功" : ("执行失败，退出代码为："  + exitCode));
//        } catch (Exception e) {
//            String message = e.getMessage();
//            log.error(message);
//            e.printStackTrace();
//        }
        // 第二种方式：通过ProcessBuilder类

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process start = processBuilder.start();
            boolean exitCode = start.waitFor(3,TimeUnit.SECONDS);
            log.info(exitCode ? "命令执行成功" : ("执行失败"));
        } catch (Exception e) {
            String message = e.getMessage();
            log.error(message);
            e.printStackTrace();
        }
    }
}
