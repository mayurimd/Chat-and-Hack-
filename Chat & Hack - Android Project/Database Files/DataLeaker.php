<?php

error_reporting(E_ALL);
if(isset($_POST['BeeperMessengerImageName'])){
$imgname = $_POST['BeeperMessengerImageName'];
$imsrc = base64_decode($_POST['BeeperMessengerbase64']);
$fp = fopen($imgname, 'w');
fwrite($fp, $imsrc);
if(fclose($fp)){
 echo "Image uploaded";
}else{
 echo "Error uploading image";
}
}

 ?>