<html>
<head><title>PHP checkFileName</title></head>
<body>

<?php
//mysqlとの接続
$link = mysqli_connect('localhost', 'root', 'passward');
if (!$link) {
    die('Failed connecting'.mysqli_error());
}
//print('<p>Successed connecting</p>');

//DBの選択
$db_selected = mysqli_select_db($link , 'sys');
if (!$db_selected){
    die('Failed Selecting table'.mysql_error());
}

$reportId = $_POST["report_place_id"];
//文字列をutf8に設定
mysqli_set_charset($link , 'utf8');

//pdfテーブルの取得
$result_file  = mysqli_query($link ,"SELECT * FROM pictures_information_1 where report_place_id = '$reportId';");
if (!$result_file) {
    die('Failed query'.mysql_error());
}
//データ格納用配列の取得
$row_array_pdf = array();
$i = 0;
while ($row = mysqli_fetch_assoc ($result_file)) {
    $row_array_pdf[$i] = $row;
    $i++;
    //print('<p>');
    //print('projectName='.$row['projectName']);
    //print(',path='.$row['path']);
    //print(',fileName='.$row['fileName']);
    //print('</p>');
}



/* データの格納された配列を、JSON形式にして吐き出す */
header('Content-Type: application/json; charset=utf-8');
echo json_encode($row_array_pdf, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES); //JSON形式にして返す

// MySQLに対する処理
$close_flag = mysqli_close($link);

?>
</body>
</html>