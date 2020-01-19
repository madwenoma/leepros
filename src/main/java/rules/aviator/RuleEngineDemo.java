package rules.aviator;

import com.google.common.collect.Lists;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import io.vavr.collection.Stream;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务需求：
 *
 * "1小时，userid，在ip上，触发action 100次报警"
 *
 * 表达式设计：
 *
 * "redisCount('1','hour',fields('userid,ip,action')) >= 100"
 *
 * 函数说明：
 *
 * fields() : 获取字段，校验，生成redis key
 *
 * redisCount()：使用 key进行查询，获取redis中存的量且redis +1
 */
public class RuleEngineDemo {

    public static void main(String[] args) {
        //注册自定义表达式函数
        AviatorEvaluator.addFunction(new FieldsFunction());
        AviatorEvaluator.addFunction(new RedisCountFunction());


        //用户指定规则
        String expression = "redisCount('1','hour',fields('userid,ip,action')) >= 100";
        Expression compiledExp = AviatorEvaluator.compile(expression);


        //运行时收到数据
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("userid", "9527");
        fields.put("ip", "127.0.0.1");
        fields.put("phone", "18811223344");
        fields.put("action", "click");

        Boolean needAlarm = (Boolean) compiledExp.execute(fields);

        if (needAlarm) {
            System.out.printf("报警");
        }
    }


    static class FieldsFunction extends AbstractFunction {

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject fieldsStrObj) {
            //获取可变参数
            String fieldStr = fieldsStrObj.stringValue(env);
            String[] fields = fieldStr.split(",");
            StringBuilder redisKey = new StringBuilder();

            System.out.println("FieldsFunction : " + fieldStr);

            for (String f : fields) {
                Object value = env.get(f);
                if (value != null) {
                    redisKey.append(value.toString());
                } else {
                    //TODO 参数合法性校验
                }
                redisKey.append(":");
            }

            //TODO key 长多过长，会影响redis性能
            return new AviatorString(redisKey.toString());
        }

        public String getName() {
            return "fields";
        }
    }


    static class RedisCountFunction extends AbstractFunction {

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
            String period = FunctionUtils.getStringValue(arg1, env);
            String timeUnit = FunctionUtils.getStringValue(arg2, env);
            String redisKey = FunctionUtils.getStringValue(arg3, env);

            System.out.println("FieldsFunction : " + period + " , " + timeUnit + " , " + redisKey);

            //TODO 读取redis
            int redisCount = redisGetAndIncrease(redisKey);

            return new AviatorLong(redisCount);
        }

        private int redisGetAndIncrease(String redisKey) {
            System.out.println("get redis : " + redisKey);
            //这里查询redis获得活动的值；
            return 10000;
        }

        public String getName() {
            return "redisCount";
        }
    }
}