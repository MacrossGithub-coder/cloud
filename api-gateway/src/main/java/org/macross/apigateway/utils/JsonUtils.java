package org.macross.apigateway.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class JsonUtils {
    public static final SerializeConfig config;

    public static final SerializeConfig dateConfig;

    /**
     * 单位缩进字符串。
     */
    private static String SPACE = "   ";


    static {
        config = new SerializeConfig();
        // 使用和json-lib兼容的日期输出格式
        config.put(Date.class, new JSONLibDataFormatSerializer());
        // 使用和json-lib兼容的日期输出格式
        config.put(java.sql.Date.class, new JSONLibDataFormatSerializer());
        // 使用和json-lib兼容的日期输出格式
        config.put(Timestamp.class, new JSONLibDataFormatSerializer());

        dateConfig = new SerializeConfig();
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        dateConfig.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
        dateConfig.put(Timestamp.class, new SimpleDateFormatSerializer(dateFormat));
        dateConfig.put(java.sql.Date.class, new SimpleDateFormatSerializer(dateFormat));
    }


    public static final SerializerFeature[] features_ = {
            SerializerFeature.WriteMapNullValue, // 输出空置字段
            SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null
            SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null
            SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullStringAsEmpty // 字符类型字段如果为null，输出为""，而不是null
    };

    public static final SerializerFeature[] features = {
            SerializerFeature.WriteMapNullValue, // 输出空置字段
            SerializerFeature.WriteNullListAsEmpty // list字段如果为null，输出为[]，而不是null
    };

    @Deprecated
    public static String convertObjectToJSONMapNull(Object object) {
        return JSON.toJSONString(object, config, features_);
    }
    /**自定义格式*/
    public static String convertObject(Object object, SerializerFeature[] features, SerializeConfig config) {
        return JSON.toJSONString(object, config, features);
    }

    /**json-lib 日期输出格式*/
    public static String convertObjectToJSON(Object object) {
        return JSON.toJSONString(object, config, features);
    }

    public static String toJSONNoFeatures(Object object) {
        return JSON.toJSONString(object, config);
    }
    /**如无对日期格式有特殊要求，建议使用*/
    public static String convertFormat(Object object) {
        return JSON.toJSONString(object,features);
    }

    /**如无对格式有特殊要求，建议使用*/
    public static String convert(Object object) {
        return JSON.toJSONString(object);
    }

    /**日期格式 ：yyyy-MM-dd HH:mm:ss*/
    public static String convertFormatDate(Object object) {
        return JSON.toJSONString(object,dateConfig, features);
    }
    /**自定义日期格式*/
    public static String convert(Object object,SerializeConfig config) {
        return JSON.toJSONString(object,config, features);
    }

    public static Object toBean(String text) {
        return JSON.parse(text);
    }

    public static <T> T toBean(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    /** 转换为数组*/
    public static <T> Object[] toArray(String text) {
        return toArray(text, null);
    }

    /**转换为数组*/
    public static <T> Object[] toArray(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz).toArray();
    }

    /**转换为List*/
    public static <T> List<T> toList(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz);
    }

    /**
     * 将javabean转化为序列化的json字符串
     * @param keyvalue
     * @return
     */
    /*public static Object beanToJson(KeyValue keyvalue) {
        String textJson = JSON.toJSONString(keyvalue);
        Object objectJson  = JSON.parse(textJson);
        return objectJson;
    }*/

    /**
     * 将string转化为序列化的json字符串
     * @param
     * @return
     */
    public static Object textToJson(String text) {
        Object objectJson  = JSON.parse(text);
        return objectJson;
    }

    /**
     * json字符串转化为map
     * @param s
     * @return
     */
    public static <K, V> Map<K, V>  stringToCollect(String s) {
        Map<K, V> m = (Map<K, V>) JSONObject.parseObject(s);
        return m;
    }

    /**
     * 转换JSON字符串为对象
     * @param jsonData
     * @param clazz
     * @return
     */
    public static <T> T convertJsonToObject(String jsonData, Class<T> clazz) {
        return JSONObject.parseObject(jsonData, clazz);
    }

    /**
     * 将map转化为string
     * @param m
     * @return
     */
    public static <K, V> String collectToString(Map<K, V> m) {
        String s = JSONObject.toJSONString(m);
        return s;
    }

    public static boolean validate(String jsonStr) {
        if(StringUtils.isBlank(jsonStr)){
            return false;
        }
        try {
            JSONObject.parseObject(jsonStr);
        } catch (JSONException ex) {
            try {
                JSONObject.parseArray(jsonStr);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }


    public static String readStream(InputStream in)throws Exception {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        }finally {
            IOUtils.closeQuietly(reader);
        }
        return buffer.toString();
    }


    /**
     * 返回格式化JSON字符串。
     *
     * @param json 未格式化的JSON字符串。
     * @return 格式化的JSON字符串。
     */
    public String formatJson(String json) {
        StringBuffer result = new StringBuffer();
        int length = json.length();
        int number = 0;
        char key = 0;
        //遍历输入字符串。
        for (int i = 0; i < length; i++) {
            //1、获取当前字符。
            key = json.charAt(i);
            //2、如果当前字符是前方括号、前花括号做如下处理：
            if((key == '[') || (key == '{') ) {
                //（1）如果前面还有字符，并且字符为“：”，打印：换行和缩进字符字符串。
                if((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
                    result.append('\n');
                    result.append(indent(number));
                }
                //（2）打印：当前字符。
                result.append(key);
                //（3）前方括号、前花括号，的后面必须换行。打印：换行。
                result.append('\n');
                //（4）每出现一次前方括号、前花括号；缩进次数增加一次。打印：新行缩进。
                number++;
                result.append(indent(number));
                //（5）进行下一次循环。
                continue;
            }
            //3、如果当前字符是后方括号、后花括号做如下处理：
            if((key == ']') || (key == '}') ) {
                //（1）后方括号、后花括号，的前面必须换行。打印：换行。
                result.append('\n');
                //（2）每出现一次后方括号、后花括号；缩进次数减少一次。打印：缩进。
                number--;
                result.append(indent(number));
                //（3）打印：当前字符。
                result.append(key);
                //（4）如果当前字符后面还有字符，并且字符不为“，”，打印：换行。
                if(((i + 1) < length) && (json.charAt(i + 1) != ',')) {
                    result.append('\n');
                }
                //（5）继续下一次循环。
                continue;
            }
            //4、如果当前字符是逗号。逗号后面换行，并缩进，不改变缩进次数。
            if((key == ',')) {
                result.append(key);
                result.append('\n');
                result.append(indent(number));
                continue;
            }
            //5、打印：当前字符。
            result.append(key);
        }

        return result.toString();
    }


    /**
     * 返回指定次数的缩进字符串。每一次缩进三个空格，即SPACE。
     *
     * @param number 缩进次数。
     * @return 指定缩进次数的字符串。
     */
    private String indent(int number)
    {
        StringBuffer result = new StringBuffer();
        for(int i = 0; i < number; i++)
        {
            result.append(SPACE);
        }
        return result.toString();
    }

}
