package com.xunqi.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自定义参数校验注解 @ListValue。
 * 限制被标注字段的取值必须属于 vals() 指定的整数集合（如状态字段只能取 0 或 1）。
 * 校验逻辑由 {@link ListValueConstraintValidator} 实现，并支持分组校验（groups）。
 */

@Documented
@Constraint(validatedBy = { ListValueConstraintValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface ListValue {

    /** 校验失败时的提示信息（默认取国际化配置） */
    String message() default "{com.xunqi.common.valid.ListValue.message}";

    /** 校验分组（配合 JSR-303 分组校验使用） */
    Class<?>[] groups() default { };

    /** 负载（元数据载体，一般不用） */
    Class<? extends Payload>[] payload() default { };

    /** 允许的值集合，字段值必须在此数组中 */
    int[] vals() default { };

}
