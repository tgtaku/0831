<?php
require "conn.php";

$pdf_id  = $_POST["pdf_id"];
$report_place_name = $_POST["report_place_name"];
$page = $_POST["page"];
$point_x = $_POST["point_x"];
$point_y = $_POST["point_y"];

$sql = "INSERT INTO report_places_information_1 VALUES ('3', '$pdf_id', '$report_place_name', '$page', '$point_x', '$point_y', '');";
$result  = mysqli_query($conn, $sql);
if($result){
echo "Data Inserted";
}
else{
echo "Failed";
}
?>