# Teamone Sonar PHP
Teamone Sonar PHP 是基于 SonarPHP 修改部分规则和自定义规则，用于对PHP工程师书写的 PHP 代码进行检测和bug提示，帮助建立代码规范，养成编码习惯，提升编码质量

# 主要特性
1. 配置化读取Json配置文件，实现同一规则对于git仓库下的不同项目采取不同的规则内容，修改立即生效，只需重新执行检测即可应用到最新的规则

2. 新增了6种规则分别为：
- 函数方法即将丢弃
- 注释首行应符合注释规则（正则）
- 禁止使用危险函数
- 禁止使用已被删除的函数
- 配置信息应直接配置
- 需要有自定义的异常类（正则）

3. 修改源代码中存在的其他导致检测不到的bug，完善一些检查机制

# 安装方式
执行如下打包命令
```shell
mvn license:format

mvn clean install -DskipTests
```

将打出来的 jar 包放置在 Sonar 对应的 plugins 的 jar 包文件夹下

# 其他可参考官网readme：

# SonarPHP [![Build Status](https://travis-ci.org/SonarSource/sonar-php.svg?branch=master)](https://travis-ci.org/SonarSource/sonar-php)

SonarPHP is a [static code analyser](https://en.wikipedia.org/wiki/Static_program_analysis) for PHP language used as an extension for the [SonarQube](http://www.sonarqube.org/) platform. It will allow you to produce stable and easily supported code by helping you find and correct bugs, vulnerabilities and smells in your code.

# Features
* 140+ rules
* Support of PHP 7
* Metrics (complexity, number of lines etc.)
* Import of [unit test and coverage results](http://docs.sonarqube.org/display/PLUG/PHP+Unit+Test+and+Coverage+Results+Import)
* Support of [custom rules](http://docs.sonarqube.org/display/PLUG/Custom+Rules+for+PHP)

# Useful links

* [Project homepage](https://redirect.sonarsource.com/plugins/php.html)
* [Documentation](https://docs.sonarqube.org/display/PLUG/SonarPHP)
* [Issue tracking](http://jira.sonarsource.com/browse/SONARPHP)
* [Available rules](https://rules.sonarsource.com/php)
* [SonarSource Community Forum](https://community.sonarsource.com/)
* [Demo project analysis](https://sonarcloud.io/dashboard?id=drupal)

# More documentation

* [Control Flow Graph documentation](CFG.md)

Have question or feedback?
--------------------------

To provide feedback (request a feature, report a bug etc.) use the [SonarSource Community Forum](https://community.sonarsource.com/). Please do not forget to specify the language (PHP!), plugin version and SonarQube version.

If you have a question on how to use plugin (and the [docs](https://docs.sonarqube.org/display/PLUG/SonarPHP) don't help you), we also encourage you to use the community forum.

# Contributing

### Topic in SonarQube Community Forum

To request a new feature, please create a new thread in [SonarQube Community Forum](https://community.sonarsource.com/). Even if you plan to implement it yourself and submit it back to the community, please start a new thread first to be sure that we can follow up on it.

### Pull Request (PR)
To submit a contribution, create a pull request for this repository. Please make sure that you follow our [code style](https://github.com/SonarSource/sonar-developer-toolset) and all [tests](#testing) are passing (Travis build is created for each PR).

### Custom Rules
If you have an idea for a rule but you are not sure that everyone needs it you can implement a [custom rule](http://docs.sonarqube.org/display/PLUG/Custom+Rules+for+PHP) available only for you.

# <a name="testing"></a>Testing
To run tests locally follow these instructions.

### Build the Project and Run Unit Tests
To build the plugin and run its unit tests, execute this command from the project's root directory (you will need [Maven](http://maven.apache.org/)):
```
mvn clean install
```

### Integration Tests
To run integration tests, you will need to create a properties file like the one shown below, and set its location in an environment variable named `ORCHESTRATOR_CONFIG_URL`.
```
# version of SonarQube server
sonar.runtimeVersion=7.9

orchestrator.updateCenterUrl=http://update.sonarsource.org/update-center-dev.properties
```
Before running any of integration tests make sure the submodules are checked out:
```
  git submodule init
  git submodule update
```
#### Plugin Test
The "Plugin Test" is an additional integration test which verifies plugin features such as metric calculation, coverage etc. To launch it, execute this command from directory `its/plugin`:
```
mvn clean install
```

#### Ruling Test
The "Ruling Test" is a special integration test which launches the analysis of a large code base, saves the issues created by the plugin in report files, and then compares those results to the set of expected issues (stored as JSON files). To launch ruling test:
```
cd its/ruling
mvn clean install
```

This test gives you the opportunity to examine the issues created by each rule and make sure they're what you expect. You can inspect new/lost issues checking web-pages mentioned in the logs at the end of analysis:
```
INFO  - HTML Issues Report generated: /path/to/project/sonar-php/its/sources/src/.sonar/issues-report/issues-report.html
INFO  - Light HTML Issues Report generated: /path/to/project/sonar-php/its/sources/src/.sonar/issues-report/issues-report-light.html
```
If everything looks good to you, you can copy the file with the actual issues located at
```
sonar-php/its/ruling/target/actual/
```
into the directory with the expected issues
```
sonar-php/its/ruling/src/test/resources/expected/
```

### License

Copyright 2010-2018 SonarSource.

Licensed under the [GNU Lesser General Public License, Version 3.0](http://www.gnu.org/licenses/lgpl.txt)
