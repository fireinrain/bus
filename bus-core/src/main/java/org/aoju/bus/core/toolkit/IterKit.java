/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.core.toolkit;

import org.aoju.bus.core.collection.CopiedIter;
import org.aoju.bus.core.collection.EnumerationIter;
import org.aoju.bus.core.lang.Filter;
import org.aoju.bus.core.lang.Func;
import org.aoju.bus.core.lang.exception.InstrumentException;

import java.util.*;
import java.util.function.Function;

/**
 * {@link Iterable} 和 {@link Iterator} 相关工具类
 *
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
public class IterKit {

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterable<?> iterable) {
        return null == iterable || isEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterator<?> iterator) {
        return null == iterator || false == iterator.hasNext();
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isNotEmpty(Iterable<?> iterable) {
        return null != iterable && isNotEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isNotEmpty(Iterator<?> iterator) {
        return null != iterator && iterator.hasNext();
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param iterable 被检查的{@link Iterable}对象,如果为{@code null} 返回true
     * @return 是否包含{@code null}元素
     */
    public static boolean hasNull(Iterable<?> iterable) {
        return hasNull(null == iterable ? null : iterable.iterator());
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param iterator 被检查的{@link Iterator}对象,如果为{@code null} 返回true
     * @return 是否包含{@code null}元素
     */
    public static boolean hasNull(Iterator<?> iterator) {
        if (null == iterator) {
            return true;
        }
        while (iterator.hasNext()) {
            if (null == iterator.next()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否全部元素为null
     *
     * @param iterable iter 被检查的{@link Iterable}对象,如果为{@code null} 返回true
     * @return 是否全部元素为null
     */
    public static boolean isAllNull(Iterable<?> iterable) {
        return isAllNull(null == iterable ? null : iterable.iterator());
    }

    /**
     * 是否全部元素为null
     *
     * @param iterator iter 被检查的{@link Iterator}对象,如果为{@code null} 返回true
     * @return 是否全部元素为null
     */
    public static boolean isAllNull(Iterator<?> iterator) {
        if (null == iterator) {
            return true;
        }

        while (iterator.hasNext()) {
            if (null != iterator.next()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据集合返回一个元素计数的 {@link Map}
     * 所谓元素计数就是假如这个集合中某个元素出现了n次,那将这个元素做为key,n做为value
     * 例如：[a,b,c,c,c] 得到：
     * a: 1
     * b: 1
     * c: 3
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable},如果为null返回一个空的Map
     * @return {@link Map}
     */
    public static <T> Map<T, Integer> countMap(Iterable<T> iterable) {
        return countMap(null == iterable ? null : iterable.iterator());
    }

    /**
     * 根据集合返回一个元素计数的 {@link Map}
     * 所谓元素计数就是假如这个集合中某个元素出现了n次,那将这个元素做为key,n做为value
     * 例如：[a,b,c,c,c] 得到：
     * a: 1
     * b: 1
     * c: 3
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator},如果为null返回一个空的Map
     * @return {@link Map}
     */
    public static <T> Map<T, Integer> countMap(Iterator<T> iterator) {
        final HashMap<T, Integer> countMap = new HashMap<>();
        if (null != iterator) {
            Integer count;
            T t;
            while (iterator.hasNext()) {
                t = iterator.next();
                count = countMap.get(t);
                if (null == count) {
                    countMap.put(t, 1);
                } else {
                    countMap.put(t, count + 1);
                }
            }
        }
        return countMap;
    }

    /**
     * 字段值与列表值对应的Map,常用于元素对象中有唯一ID时需要按照这个ID查找对象的情况
     * 例如：车牌号 = 车
     *
     * @param <K>       字段名对应值得类型,不确定请使用Object
     * @param <V>       对象类型
     * @param iterable  对象列表
     * @param fieldName 字段名(会通过反射获取其值)
     * @return 某个字段值与对象对应Map
     */
    public static <K, V> Map<K, V> fieldValueMap(Iterable<V> iterable, String fieldName) {
        return fieldValueMap(null == iterable ? null : iterable.iterator(), fieldName);
    }

    /**
     * 字段值与列表值对应的Map,常用于元素对象中有唯一ID时需要按照这个ID查找对象的情况
     * 例如：车牌号 = 车
     *
     * @param <K>       字段名对应值得类型,不确定请使用Object
     * @param <V>       对象类型
     * @param iterator  对象列表
     * @param fieldName 字段名(会通过反射获取其值)
     * @return 某个字段值与对象对应Map
     */
    public static <K, V> Map<K, V> fieldValueMap(Iterator<V> iterator, String fieldName) {
        final Map<K, V> result = new HashMap<>();
        if (null != iterator) {
            V value;
            while (iterator.hasNext()) {
                value = iterator.next();
                result.put((K) ReflectKit.getFieldValue(value, fieldName), value);
            }
        }
        return result;
    }

    /**
     * 两个字段值组成新的Map
     *
     * @param <K>               字段名对应值得类型,不确定请使用Object
     * @param <V>               值类型,不确定使用Object
     * @param iterator          对象列表
     * @param fieldNameForKey   做为键的字段名(会通过反射获取其值)
     * @param fieldNameForValue 做为值的字段名(会通过反射获取其值)
     * @return 某个字段值与对象对应Map
     */
    public static <K, V> Map<K, V> fieldValueAsMap(Iterator<?> iterator, String fieldNameForKey, String fieldNameForValue) {
        final Map<K, V> result = new HashMap<>();
        if (null != iterator) {
            Object value;
            while (iterator.hasNext()) {
                value = iterator.next();
                result.put((K) ReflectKit.getFieldValue(value, fieldNameForKey), (V) ReflectKit.getFieldValue(value, fieldNameForValue));
            }
        }
        return result;
    }

    /**
     * 获取指定Bean列表中某个字段,生成新的列表
     *
     * @param <V>       对象类型
     * @param iterator  对象列表
     * @param fieldName 字段名(会通过反射获取其值)
     * @return 某个字段值与对象对应Map
     */
    public static <V> List<Object> fieldValueList(Iterator<V> iterator, String fieldName) {
        final List<Object> result = new ArrayList<>();
        if (null != iterator) {
            V value;
            while (iterator.hasNext()) {
                value = iterator.next();
                result.add(ReflectKit.getFieldValue(value, fieldName));
            }
        }
        return result;
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串
     *
     * @param <T>         集合元素类型
     * @param iterable    {@link Iterable}
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static <T> String join(Iterable<T> iterable, CharSequence conjunction) {
        if (null == iterable) {
            return null;
        }
        return join(iterable.iterator(), conjunction);
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串
     *
     * @param <T>         集合元素类型
     * @param iterable    {@link Iterable}
     * @param conjunction 分隔符
     * @param prefix      每个元素添加的前缀,null表示不添加
     * @param suffix      每个元素添加的后缀,null表示不添加
     * @return 连接后的字符串
     */
    public static <T> String join(Iterable<T> iterable, CharSequence conjunction, String prefix, String suffix) {
        if (null == iterable) {
            return null;
        }
        return join(iterable.iterator(), conjunction, prefix, suffix);
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator},则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterator    集合
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static <T> String join(Iterator<T> iterator, CharSequence conjunction) {
        return join(iterator, conjunction, null, null);
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator},则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterator    集合
     * @param conjunction 分隔符
     * @param prefix      每个元素添加的前缀,null表示不添加
     * @param suffix      每个元素添加的后缀,null表示不添加
     * @return 连接后的字符串
     */
    public static <T> String join(Iterator<T> iterator, CharSequence conjunction, String prefix, String suffix) {
        if (null == iterator) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        T item;
        while (iterator.hasNext()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }

            item = iterator.next();
            if (ArrayKit.isArray(item)) {
                sb.append(ArrayKit.join(ArrayKit.wrap(item), conjunction, prefix, suffix));
            } else if (item instanceof Iterable<?>) {
                sb.append(join((Iterable<?>) item, conjunction, prefix, suffix));
            } else if (item instanceof Iterator<?>) {
                sb.append(join((Iterator<?>) item, conjunction, prefix, suffix));
            } else {
                sb.append(StringKit.wrap(String.valueOf(item), prefix, suffix));
            }
        }
        return sb.toString();
    }

    /**
     * 将Entry集合转换为HashMap
     *
     * @param <K>       键类型
     * @param <V>       值类型
     * @param entryIter entry集合
     * @return Map
     */
    public static <K, V> HashMap<K, V> toMap(Iterable<Map.Entry<K, V>> entryIter) {
        final HashMap<K, V> map = new HashMap<>();
        if (isNotEmpty(entryIter)) {
            for (Map.Entry<K, V> entry : entryIter) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    /**
     * 将键列表和值列表转换为Map
     * 以键为准,值与键位置需对应 如果键元素数多于值元素,多余部分值用null代替
     * 如果值多于键,忽略多余的值
     *
     * @param <K>    键类型
     * @param <V>    值类型
     * @param keys   键列表
     * @param values 值列表
     * @return 标题内容Map
     */
    public static <K, V> Map<K, V> toMap(Iterable<K> keys, Iterable<V> values) {
        return toMap(keys, values, false);
    }

    /**
     * 将键列表和值列表转换为Map
     * 以键为准,值与键位置需对应 如果键元素数多于值元素,多余部分值用null代替
     * 如果值多于键,忽略多余的值
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param keys    键列表
     * @param values  值列表
     * @param isOrder 是否有序
     * @return 标题内容Map
     */
    public static <K, V> Map<K, V> toMap(Iterable<K> keys, Iterable<V> values, boolean isOrder) {
        return toMap(null == keys ? null : keys.iterator(), null == values ? null : values.iterator(), isOrder);
    }

    /**
     * 将键列表和值列表转换为Map
     * 以键为准,值与键位置需对应 如果键元素数多于值元素,多余部分值用null代替
     * 如果值多于键,忽略多余的值
     *
     * @param <K>    键类型
     * @param <V>    值类型
     * @param keys   键列表
     * @param values 值列表
     * @return 标题内容Map
     */
    public static <K, V> Map<K, V> toMap(Iterator<K> keys, Iterator<V> values) {
        return toMap(keys, values, false);
    }

    /**
     * 将键列表和值列表转换为Map
     * 以键为准,值与键位置需对应 如果键元素数多于值元素,多余部分值用null代替
     * 如果值多于键,忽略多余的值
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param keys    键列表
     * @param values  值列表
     * @param isOrder 是否有序
     * @return 标题内容Map
     */
    public static <K, V> Map<K, V> toMap(Iterator<K> keys, Iterator<V> values, boolean isOrder) {
        final Map<K, V> resultMap = MapKit.newHashMap(isOrder);
        if (isNotEmpty(keys)) {
            while (keys.hasNext()) {
                resultMap.put(keys.next(), (null != values && values.hasNext()) ? values.next() : null);
            }
        }
        return resultMap;
    }

    /**
     * 将列表转成值为List的HashMap
     *
     * @param iterable  值列表
     * @param keyMapper Map的键映射
     * @param <K>       键类型
     * @param <V>       值类型
     * @return HashMap
     */
    public static <K, V> Map<K, List<V>> toListMap(Iterable<V> iterable, Function<V, K> keyMapper) {
        return toListMap(iterable, keyMapper, v -> v);
    }

    /**
     * 将列表转成值为List的HashMap
     *
     * @param iterable    值列表
     * @param keyMapper   Map的键映射
     * @param valueMapper Map中List的值映射
     * @param <T>         列表值类型
     * @param <K>         键类型
     * @param <V>         值类型
     * @return HashMap
     */
    public static <T, K, V> Map<K, List<V>> toListMap(Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return toListMap(MapKit.newHashMap(), iterable, keyMapper, valueMapper);
    }

    /**
     * 将列表转成值为List的HashMap
     *
     * @param resultMap   结果Map，可自定义结果Map类型
     * @param iterable    值列表
     * @param keyMapper   Map的键映射
     * @param valueMapper Map中List的值映射
     * @param <T>         列表值类型
     * @param <K>         键类型
     * @param <V>         值类型
     * @return HashMap
     */
    public static <T, K, V> Map<K, List<V>> toListMap(Map<K, List<V>> resultMap, Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        if (null == resultMap) {
            resultMap = MapKit.newHashMap();
        }
        if (ObjectKit.isNull(iterable)) {
            return resultMap;
        }

        for (T value : iterable) {
            resultMap.computeIfAbsent(keyMapper.apply(value), k -> new ArrayList<>()).add(valueMapper.apply(value));
        }

        return resultMap;
    }

    /**
     * 将列表转成HashMap
     *
     * @param iterable  值列表
     * @param keyMapper Map的键映射
     * @param <K>       键类型
     * @param <V>       值类型
     * @return HashMap
     */
    public static <K, V> Map<K, V> toMap(Iterable<V> iterable, Function<V, K> keyMapper) {
        return toMap(iterable, keyMapper, v -> v);
    }

    /**
     * 将列表转成HashMap
     *
     * @param iterable    值列表
     * @param keyMapper   Map的键映射
     * @param valueMapper Map的值映射
     * @param <T>         列表值类型
     * @param <K>         键类型
     * @param <V>         值类型
     * @return HashMap
     */
    public static <T, K, V> Map<K, V> toMap(Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return toMap(MapKit.newHashMap(), iterable, keyMapper, valueMapper);
    }

    /**
     * 将列表转成Map
     *
     * @param resultMap   结果Map，通过传入map对象决定结果的Map类型
     * @param iterable    值列表
     * @param keyMapper   Map的键映射
     * @param valueMapper Map的值映射
     * @param <T>         列表值类型
     * @param <K>         键类型
     * @param <V>         值类型
     * @return HashMap
     */
    public static <T, K, V> Map<K, V> toMap(Map<K, V> resultMap, Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        if (null == resultMap) {
            resultMap = MapKit.newHashMap();
        }
        if (ObjectKit.isNull(iterable)) {
            return resultMap;
        }

        for (T value : iterable) {
            resultMap.put(keyMapper.apply(value), valueMapper.apply(value));
        }

        return resultMap;
    }

    /**
     * Iterator转List
     * 不判断,直接生成新的List
     *
     * @param <E>      元素类型
     * @param iterable {@link Iterator}
     * @return List
     */
    public static <E> List<E> toList(Iterable<E> iterable) {
        if (null == iterable) {
            return null;
        }
        return toList(iterable.iterator());
    }

    /**
     * Iterator转List
     * 不判断,直接生成新的List
     *
     * @param <E>      元素类型
     * @param iterator {@link Iterator}
     * @return List
     */
    public static <E> List<E> toList(Iterator<E> iterator) {
        final List<E> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    /**
     * Enumeration转换为Iterator
     * <p>
     * Adapt the specified <code>Enumeration</code> to the <code>Iterator</code> interface
     *
     * @param <E> 集合元素类型
     * @param e   {@link Enumeration}
     * @return {@link Iterator}
     */
    public static <E> Iterator<E> asIterator(Enumeration<E> e) {
        return new EnumerationIter<>(e);
    }

    /**
     * {@link Iterator} 转为 {@link Iterable}
     *
     * @param <E>  元素类型
     * @param iter {@link Iterator}
     * @return {@link Iterable}
     */
    public static <E> Iterable<E> asIterable(final Iterator<E> iter) {
        return () -> iter;
    }

    /**
     * 获取集合的第一个元素
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     * @return 第一个元素
     */
    public static <T> T getFirst(Iterable<T> iterable) {
        if (null == iterable) {
            return null;
        }
        return getFirst(iterable.iterator());
    }

    /**
     * 获取集合的第一个元素
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @return 第一个元素
     */
    public static <T> T getFirst(Iterator<T> iterator) {
        if (null != iterator && iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    /**
     * 获得{@link Iterable}对象的元素类型(通过第一个非空元素判断)
     * 注意,此方法至少会调用多次next方法
     *
     * @param iterable {@link Iterable}
     * @return 元素类型, 当列表为空或元素全部为null时, 返回null
     */
    public static Class<?> getElementType(Iterable<?> iterable) {
        if (null != iterable) {
            final Iterator<?> iterator = iterable.iterator();
            return getElementType(iterator);
        }
        return null;
    }

    /**
     * 获得{@link Iterator}对象的元素类型(通过第一个非空元素判断)
     * 注意,此方法至少会调用多次next方法
     *
     * @param iterator {@link Iterator}
     * @return 元素类型, 当列表为空或元素全部为null时, 返回null
     */
    public static Class<?> getElementType(Iterator<?> iterator) {
        final Iterator<?> iter2 = new CopiedIter<>(iterator);
        if (iter2.hasNext()) {
            final Object t = iter2.next();
            if (null != t) {
                return t.getClass();
            }
        }
        return null;
    }

    /**
     * 过滤集合，此方法在原集合上直接修改
     * 通过实现Filter接口，完成元素的过滤，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 过滤出需要的对象，{@link Filter#accept(Object)}方法
     * 返回false的对象将被使用{@link Iterator#remove()}方法移除
     * </pre>
     *
     * @param <T>      集合类型
     * @param <E>      集合元素类型
     * @param iterable 集合
     * @param filter   过滤器接口
     * @return 编辑后的集合
     */
    public static <T extends Iterable<E>, E> T filter(T iterable, Filter<E> filter) {
        if (null == iterable) {
            return null;
        }

        filter(iterable.iterator(), filter);

        return iterable;
    }

    /**
     * 过滤集合，此方法在原集合上直接修改
     * 通过实现Filter接口，完成元素的过滤，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 过滤出需要的对象，{@link Filter#accept(Object)}方法
     * 返回false的对象将被使用{@link Iterator#remove()}方法移除
     * </pre>
     *
     * @param <E>      集合元素类型
     * @param iterator 集合
     * @param filter   过滤器接口
     * @return 编辑后的集合
     */
    public static <E> Iterator<E> filter(Iterator<E> iterator, Filter<E> filter) {
        if (null == iterator || null == filter) {
            return iterator;
        }

        while (iterator.hasNext()) {
            if (false == filter.accept(iterator.next())) {
                iterator.remove();
            }
        }
        return iterator;
    }

    /**
     * Iterator转换为Map，转换规则为：
     * 按照keyFunc函数规则根据元素对象生成Key，元素作为值
     *
     * @param <K>      Map键类型
     * @param <V>      Map值类型
     * @param iterator 数据列表
     * @param map      Map对象，转换后的键值对加入此Map，通过传入此对象自定义Map类型
     * @param keyFunc  生成key的函数
     * @return 生成的map
     */
    public static <K, V> Map<K, V> toMap(Iterator<V> iterator, Map<K, V> map, Func.Func1<V, K> keyFunc) {
        return toMap(iterator, map, keyFunc, (value) -> value);
    }

    /**
     * 集合转换为Map，转换规则为：
     * 按照keyFunc函数规则根据元素对象生成Key，按照valueFunc函数规则根据元素对象生成value组成新的Map
     *
     * @param <K>       Map键类型
     * @param <V>       Map值类型
     * @param <E>       元素类型
     * @param iterator  数据列表
     * @param map       Map对象，转换后的键值对加入此Map，通过传入此对象自定义Map类型
     * @param keyFunc   生成key的函数
     * @param valueFunc 生成值的策略函数
     * @return 生成的map
     */
    public static <K, V, E> Map<K, V> toMap(Iterator<E> iterator, Map<K, V> map, Func.Func1<E, K> keyFunc, Func.Func1<E, V> valueFunc) {
        if (null == iterator) {
            return map;
        }

        if (null == map) {
            map = MapKit.newHashMap(true);
        }

        E element;
        while (iterator.hasNext()) {
            element = iterator.next();
            try {
                map.put(keyFunc.call(element), valueFunc.call(element));
            } catch (Exception e) {
                throw new InstrumentException(e);
            }
        }
        return map;
    }

    /**
     * 返回一个空Iterator
     *
     * @param <T> 元素类型
     * @return 空Iterator
     * @see Collections#emptyIterator()
     */
    public static <T> Iterator<T> empty() {
        return Collections.emptyIterator();
    }

}
