package org.example.base.mybatisplus;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;
/**
 * @description: mybatisplus，属性自动填充处理类
 * @author: moki
 * @date: 2024/12/26 20:15
 **/
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 生成并设置主键
        String generatedUid = UUID.randomUUID().toString();
        this.strictInsertFill(metaObject, "uid", String.class, generatedUid);

        // 捕获生成的主键
        System.out.println("Generated UID: " + generatedUid);
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }

}
