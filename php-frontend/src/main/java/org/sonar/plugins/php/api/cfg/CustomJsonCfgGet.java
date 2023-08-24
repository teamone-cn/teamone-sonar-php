package org.sonar.plugins.php.api.cfg;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.util.*;


public class CustomJsonCfgGet {

  // json 中定义项目规则的key
  private static final String JSON_RULE_KEY = "ruleKey";

  // json 中定义项目规则内容
  private static final String JSON_RULE_CONTENT = "ruleContent";

  // json中定义项目规则是否激活
  private static final String JSON_RULE_ACTIVE = "ruleActive";

  /**
   * 通过json字符串，获取到用于sonar-php的配置
   *
   * @param json
   * @return
   */
  public static HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> getCustomJson() {
    String json = "{\"tunefabjp\":[{\"ruleKey\":\"S2001\",\"ruleContent\":[\"mktime\",\"split\"],\"ruleActive\":\"N\"},{\"ruleKey\":\"S2000\",\"ruleContent\":[\"xxxx\"],\"ruleActive\":\"Y\"}],\"tunefabde\":[{\"ruleKey\":\"S2001\",\"ruleContent\":[\"create_function\"],\"ruleActive\":\"Y\"},{\"ruleKey\":\"S2000\",\"ruleContent\":[],\"ruleActive\":\"N\"}],\"wp-data\":[{\"ruleKey\":\"S2001\",\"ruleContent\":[\"mcrypt_encrypt\"],\"ruleActive\":\"Y\"}],\"wp-order\":[{\"ruleKey\":\"S2001\",\"ruleContent\":[\"mcrypt_decrypt\"],\"ruleActive\":\"Y\"}]}";
    // 构建映射关系，项目内容
    HashSet<String> contents;

    // 构建映射关系，对应的顺序为：项目规则启用标志 -- 项目内容
    HashMap<String, HashSet<String>> activeContentMap;

    // 构建映射关系，对应的顺序为：项目规则key --- 项目规则启用标志 -- 项目内容
    HashMap<String, HashMap<String, HashSet<String>>> ruleKeyActiveContentMap;

    // 构建映射关系，对应的顺序为：项目名称 -- 项目规则key --- 项目规则启用标志 -- 项目内容
    HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> projectRuleMap = new HashMap<>();

    // 获取到jsonObject
    JSONObject jsonObject = JSONObject.parseObject(json);

    // 获取最外层的key
    Iterator<String> keySet = jsonObject.keySet().iterator();

    while (keySet.hasNext()) {
      ruleKeyActiveContentMap = new HashMap<>();

      // 对应项目的项目名称，具体为：
      // 1、主题项目的 /wp-content/themes/xxxx 的具体主题的名称
      // 2、插件项目的 xxxx.php 的具体插件的名称
      // 3、内部项目的 /xxxx 的具体项目的空目录的名称
      String projectName = keySet.next();
//      System.out.println(projectName);

      // 获取到每个项目的具体的配置
      JSONArray jsonArray = jsonObject.getJSONArray(projectName);

      for (int i = 0; i < jsonArray.size(); i++) {
        activeContentMap = new HashMap<>();

        contents = new HashSet<>();
        JSONObject projectRule = jsonArray.getJSONObject(i);
        // 对应项目规则下的key，可以对于到 sonar-php 的key，也是页面上有展示的 key
        String projectRuleKey = projectRule.getString(JSON_RULE_KEY);
//        System.out.println("projectRuleKey---" + projectRuleKey);
        // 对应项目规则下具体的内容，例如可以进行 包含这些字段，不包含这些字段，这里就是用来定义这些字段的地方
        JSONArray projectRuleContents = projectRule.getJSONArray(JSON_RULE_CONTENT);
        // 对应项目规则是否启用，Y 为启用，N 为未启用
        String projectActiveFlag = projectRule.getString(JSON_RULE_ACTIVE);
//        System.out.println("projectRuleActive---" + projectActiveFlag);


        for (int j = 0; j < projectRuleContents.size(); j++) {
          // 获取到每个规则具体内容
          String projectRuleContent = projectRuleContents.getString(j);
//          System.out.println("projectRuleContent---" + projectRuleContent);
          contents.add(projectRuleContent);
        }
        activeContentMap.put(projectActiveFlag, contents);
        ruleKeyActiveContentMap.put(projectRuleKey, activeContentMap);
      }
      projectRuleMap.put(projectName, ruleKeyActiveContentMap);
    }

    return projectRuleMap;
  }
}


