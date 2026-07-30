package com.xunqi.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @ListValue 注解的校验器实现。
 * 在 initialize 阶段收集允许的值集合，在 isValid 阶段判断待校验值是否包含其中。
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

    /** 允许的值集合（由注解 vals() 初始化） */
    private Set<Integer> set = new HashSet<>();

    /**
     * 初始化：读取注解上配置的允许值，存入 set 供校验使用。
     * @param constraintAnnotation 标注在字段上的 @ListValue 注解
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {

        int[] vals = constraintAnnotation.vals();

        for (int val : vals) {
            set.add(val);
        }

    }

    /**
     * 判断被校验值是否合法（是否在允许的值集合中）。
     * @param value  待校验的整数值
     * @param context 校验上下文
     * @return true 表示校验通过（值在允许集合中）
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        // 判断值是否包含在允许集合中
        boolean contains = set.contains(value);

        return contains;
    }

}
