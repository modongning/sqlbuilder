package com.x.sqlbuilder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模糊查询注解
 *
 * @Author modongning
 * @updateBy modongning
 * @updateBy 2019/1/7 下午12:18
 */
@Target(value={ElementType.FIELD})//注解可以被添加在属性上
@Retention(value=RetentionPolicy.RUNTIME)//注解保存在JVM运行时刻,能够在运行时刻通过反射API来获取到注解的信息
public @interface Like {
	boolean value() default false;

	LikeType type() default LikeType.POST;

	enum LikeType {
		//前匹配
		PRE,
		//后匹配
		POST,
		//全匹配
		ALL;
	}
}
