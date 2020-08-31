<?php
require "conn.php";

$pictures_name  = $_POST["pictures_name"];
$pdf_id  = $_POST["pdf_id"];
$users_id  = $_POST["users_id"];
$report_place_id  = $_POST["report_place_id"];
$page = $_POST["page"];
$date = $_POST["date"];
$path = $_POST["path"];

$sql = "INSERT INTO pictures_information_1 VALUES ('7', '$pictures_name', '$pdf_id', '$users_id', '$report_place_id', '$page','$date','$path', '');";
$result  = mysqli_query($conn, $sql);
if($result){
echo "Data Inserted";
}
else{
echo "Failed";
}
?>