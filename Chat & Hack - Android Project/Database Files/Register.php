<?php

$email = $_POST["Email"];
$name = $_POST["Name"];
$password = $_POST["Password"];

$chatListFile = fopen("ChatList/ChatList.txt", "a") or die("Unable to open file!");

fwrite($chatListFile, $email . "|" . $name);
fwrite($chatListFile, "\n");
fclose($chatListFile);

$credentialsFile = fopen("Credentials/Credentials.txt", "a") or die("Unable to open file!");

fwrite($credentialsFile, $email . "|" . $password . "|" . $name);
fwrite($credentialsFile, "\n");
fclose($credentialsFile);

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