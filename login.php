<?php
require "conn.php";
$company_id = $_POST["company_id"];
$user_name = $_POST["users_name"];
$user_pass = $_POST["password"];
$mysql_qry = "select * from users_information_1 where companies_id like '$company_id' and users_name like '$user_name' and password like '$user_pass';";
$result = mysqli_query($conn, $mysql_qry);
if(mysqli_num_rows($result) > 0){

echo "login success!";
}
else{
echo "login not success";
}
?>
