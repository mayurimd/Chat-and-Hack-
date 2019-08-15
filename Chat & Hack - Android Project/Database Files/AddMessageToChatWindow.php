<?php

$chatWindowComplexString = $_POST["ComplexString"];
$chatWindowFileName = $_POST["FileName"];


$chatWindowFile = fopen($chatWindowFileName . ".txt", "a") or die("Unable to open file!");

fwrite($chatWindowFile, $chatWindowComplexString);
fwrite($chatWindowFile, "\n");
fclose($chatWindowFile);

 ?>