<?php

$chatWindow = $_POST["ChatWindow"];


$chatWindowFile = fopen($chatWindow, "a") or die("Unable to open file!");

fclose($chatWindowFile);

 ?>