<?php

namespace app\api\controller\aipen;

use app\common\controller\Api;
use app\common\library\files\content\DocxFileReader;
use app\common\library\files\content\FileContentReader;
use app\common\library\files\content\PdfFileReader;
use app\common\library\files\content\TxtFileReader;

class Test extends Api
{

  /**
   * 禁止使用过期函数
   * @return void
   */
  public function splitDemo()
  {
    $str = "Hello,World!";
    $pattern = "/,/"; // 以逗号作为分割符
    $result = split($pattern, $str);
    print_r($result);
  }

  /**
   * 谨慎使用危险函数
   * @return void
   */
  public function evalDemo()
  {
    $code = 'echo "Hello, World!";';
    eval($code); // 输出 "Hello, World!"
  }

  /**
   * 配置信息应直接配置
   * @return void
   */
  public function dlDemo()
  {
    if (!extension_loaded('sqlite')) {
      if (strtoupper(substr(PHP_OS, 0, 3)) === 'WIN') {
        dl('php_sqlite.dll');
      } else {
        dl('sqlite.so');
      }
    }
  }

  /**
   * 函数方法即将丢弃
   * @return void
   */
  public function create_function()
  {
    $func = create_function('$a, $b', 'return $a + $b;');
    echo $func(2, 3);  // 输出：5
  }


  public function test_exception(){
    // 可以通过
    try{
      $aa = 123;
      $bb = 0;
      $cc = $aa / $bb;
    } catch (HttpResponseException|ValidateException|PDOException|TeamoneException $e) {

    }


    try{
      $aa = 123;
      $bb = 0;
      $cc = $aa / $bb;
    } catch (TeamoneException|PDOException $e) {

    }


    //不能通过
    try{
      $aa = 123;
      $bb = 0;
      $cc = $aa / $bb;
    } catch (HttpResponseException|ValidateException|PDOException|\Exception $e) {

    }
  }




}

