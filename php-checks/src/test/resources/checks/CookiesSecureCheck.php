<?php

setcookie($name, $value); // Noncompliant
setcookie($name, $value, $expire, $path, $domain, false); // Noncompliant
SetCookie($name, $value, $expire, $path, $domain, false, true); // Noncompliant
setrawcookie($name, $value); // Noncompliant
  setrawcookie($name, $value, $expire, $path, $domain, false); // Noncompliant
//^^^^^^^^^^^^
  setrawcookie($name, $value, $expire, $path, $domain, false, true); // Noncompliant {{Make sure creating this cookie without the "secure" flag is safe here.}}
//^^^^^^^^^^^^

setcookie($name, $value, $expire, $path, $domain, FALSE); // Noncompliant
setcookie($name, $value, $expire, $path, $domain, False); // Noncompliant
setcookie($name, $value, $expire, $path, $domain, 0); // Noncompliant
setcookie($name, $value, $expire, $path, $domain, ""); // Noncompliant

setcookie($name, $value, $expire, $path, $domain, $secure, false);
setcookie($name, $value, $expire, $path, $domain, true);
setcookie($name, $value, $expire, $path, $domain, foo(false));
setcookie($name, $value, $expire, $path, $domain, $secure);
setcookie($name, $value, $expire, $path); // Noncompliant

setrawcookie($name, $value, $expire, $path, $domain, FALSE); // Noncompliant
setrawcookie($name, $value, $expire, $path, $domain, False); // Noncompliant
setrawcookie($name, $value, $expire, $path, $domain, 0); // Noncompliant
setrawcookie($name, $value, $expire, $path, $domain, ""); // Noncompliant

setrawcookie($name, $value, $expire, $path, $domain, $secure, false);  
setrawcookie($name, $value, $expire, $path, $domain, true);
setrawcookie($name, $value, $expire, $path, $domain, foo(false)); 
setrawcookie($name, $value, $expire, $path, $domain, $secure);
setrawcookie($name, $value, $expire, $path);  // Noncompliant

session_set_cookie_params($lifetime, $path, $domain, false); // Noncompliant
session_set_cookie_params($lifetime, $path, $domain, true);  

$params = session_get_cookie_params();
setcookie($name, $value, $params); // Compliant

$expires = 42;
setcookie($name, $value, $expires); // false negative
