package com.example.sampleapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyPage extends AppCompatActivity {
    //public String urlProjectName = "http://192.168.3.4/sample/downloadPDF.php";
    public String urlProjectName = "http://192.168.3.4/sample/getProjectName.php";
    public String urlFileName = "http://192.168.3.4/sample/getFileName.php";
    public String insertPDFInformation = "http://192.168.3.4/sample/insert_pdf_information.php";
    public String insertUsersInformation = "http://192.168.3.4/sample/insert_users_information.php";
    public String insertProjectsInformation = "http://192.168.3.4/sample/insert_projects_information.php";
    public String urlPDF = "http://10.20.170.52/sample/pdf/sampleProject1/lowcarbon05.pdf";
    public static ArrayList<String> projectName = new ArrayList<>();
    public static ArrayList<String> projectId = new ArrayList<>();
    public static ArrayList<String> fileName = new ArrayList<>();
    public static ArrayList<String> pdfId = new ArrayList<>();
    public static ArrayList<String> path = new ArrayList<>();
    //初期表示用
    Spinner spinner;
    Spinner spinnerFile;

    //画面遷移時のパラメータ
    public static String paramFileName;

    //public String uplode = "http://10.20.170.52/sample/uploadPDF.php";
    public Button button;

    //ユーザ登録機能用
    String editUser,editPass,editPer;
    //プロジェクト登録用
    String editProjectName;

    //図面登録機能用
    Spinner spinnerForInsertPDF;
    String editProjectPermission;
    String spinnerInfo;//選択されたプロジェクト名の取得
    String filesNameForInsert;
    String pathForInsert;

    public static String pdf_id;
    public static String projects_id;

    //事前情報の取得
    String regex_projectName = "\"projects_name\":.+?\",";
    String regex_projectId = "\"projects_id\":.+?\",";
    String regex_fileName = "\"pdf_name\":.+?\",";
    String regex_pdfId = "\"pdf_id\":.+?\",";
    String regex_path = "\"path\":.+?F\"";

    public static String selectedProjects = "test";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        TextView textViewUser = (TextView)findViewById(R.id.user);
        textViewUser.setText("User： " + MainActivity.username);

        //spinnerの定義
        spinner  = findViewById(R.id.spinner);
        spinnerFile = findViewById(R.id.spinnerFile);

        getPDFInfo gp = new getPDFInfo();
        gp.execute(urlProjectName);



        // リスナーを登録
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                String item = (String)spinner.getSelectedItem();
                getPDFInfo gp = new getPDFInfo();
                gp.execute(urlFileName, item);
                // ArrayAdapter
                /*ArrayAdapter<String> adapterFile
                        = new ArrayAdapter<String>(MyPage.this,
                        android.R.layout.simple_spinner_item, Collections.singletonList(fileName.get(0)));

                adapterFile.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // spinner に adapter をセット
                spinnerFile.setAdapter(adapterFile);
*/
            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        button = (Button) findViewById(R.id.upDate);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                return;
            }
        }
        enable_button();

    }

    private void enable_button(){
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final EditText editTextProject = new EditText(MyPage.this);
                editTextProject.setHint("権限（１、２、３)");
                spinnerForInsertPDF = new Spinner(MyPage.this);
                LinearLayout layout = new LinearLayout(MyPage.this);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MyPage.this,
                        android.R.layout.simple_spinner_item, projectName);//Collections.singletonList(projectName.get(0)));
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                System.out.println("--------test------"+projectName);

                spinnerForInsertPDF.setAdapter(arrayAdapter);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(spinnerForInsertPDF);
                layout.addView(editTextProject);

                AlertDialog.Builder builder = new AlertDialog.Builder(MyPage.this);
                builder.setMessage("プロジェクトを選択し、権限を入力してください")
                        .setView(layout)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //権限情報の取得
                                editProjectPermission = editTextProject.getText().toString();
                                spinnerInfo = spinner.getSelectedItem().toString();
                                System.out.println("##########"+spinnerInfo);
                                System.out.println("###########"+editProjectPermission);
                                new MaterialFilePicker()
                                        .withActivity(MyPage.this)
                                        .withRequestCode(10)
                                        .start();
                            }
                        })
                        .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                            }
                        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            enable_button();
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
            }
        }
    }

    ProgressDialog progress;

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {

            progress = new ProgressDialog(MyPage.this);
            progress.setTitle("Uploading");
            progress.setMessage("Please wait...");
            progress.show();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    File f = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                    //System.out.println(f);
                    //Insert用のファイル名を格納する
                    filesNameForInsert = f.getName();
                    System.out.println(f.getName());
                    String content_type = getMimeType(f.getPath());

                    String file_path = f.getAbsolutePath();
                    System.out.println(file_path);
                    OkHttpClient client = new OkHttpClient();
                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type), f);

                    RequestBody request_body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("type", content_type)
                            .addFormDataPart("uploaded_file", file_path.substring(file_path.lastIndexOf("/") + 1), file_body)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://192.168.3.4/sample/save_file.php")
                            .post(request_body)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();

                        if (!response.isSuccessful()) {
                            throw new IOException("Error : " + response);
                        }

                        progress.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        getPDFInfo gp = new getPDFInfo();
        gp.execute(insertProjectsInformation);

    }

    private String getMimeType(String path){
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }


    private class getPDFInfo extends AsyncTask<String, Void, ArrayList<String>>{
        @Override
        public ArrayList<String> doInBackground(String... params) {
            String params0_url = params[0];
            HttpURLConnection con = null;
            //http接続のレスポンスデータとして取得するInputStreamオブジェクトを宣言（try外）
            InputStream is = null;
            //返却用の変数
            StringBuffer conResult = new StringBuffer();

            switch (params0_url) {
                //画面遷移時にプロジェクト名を取得
                case "http://192.168.3.4/sample/getProjectName.php":
                //case "http://192.168.3.4/sample/downloadPDF.php":
                    System.out.println("projectName");
                    //http接続
                    try {
                        //URLオブジェクト作成
                        URL url = new URL(params[0]);
                        //System.out.println(url);
                        //URLオブジェクトからHttpURLConnectionオブジェクトを取得
                        con = (HttpURLConnection) url.openConnection();
                        //HTTP接続メソッドを設定
                        con.setRequestMethod("GET");
                        //接続
                        con.connect();
                        final int httpStatus = con.getResponseCode();
                        System.out.println(httpStatus);
                        //HttpURLConnectionオブジェクトからレスポンスデータの取得
                        is = con.getInputStream();
                        String encoding = con.getContentEncoding();
                        if (null == encoding) {
                            encoding = "UTF-8";
                        }
                        final InputStreamReader inReader = new InputStreamReader(is, encoding);
                        final BufferedReader bufReader = new BufferedReader(inReader);
                        String line = bufReader.readLine();
                        //1行ずつ読み込み
                        while (line != null) {
                            conResult.append(line);
                            line = bufReader.readLine();
                        }
                        bufReader.close();
                        inReader.close();
                        is.close();
                    } catch (MalformedURLException ex) {
                    } catch (IOException ex) {
                    } finally {
                        //HttpURLConnectionオブジェクトがnull出ないならば解散
                        if (con != null) {
                            con.disconnect();
                        }
                        //InputStreamオブジェクトがnull出ないならば解散
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException ex) {
                            }
                        }
                    }
                    //JSONからデータの取得
                    Pattern p_projectName = Pattern.compile(regex_projectName);
                    checkProjectName(p_projectName, conResult.toString());
                    Pattern p_projectId = Pattern.compile(regex_projectId);
                    checkProjectId(p_projectId, conResult.toString());
                    //System.out.println(projectName);
                    //Pattern p_fileName = Pattern.compile(regex_fileName);
                    //checkFileName(p_fileName, conResult.toString());
                    //System.out.println(fileName);
                    //Pattern p_path = Pattern.compile(regex_path);
                    //checkPath(p_path, conResult.toString());
                    //System.out.println(path);
                    //result = conResult.toString();
                    System.out.println(conResult);
                    System.out.println(projectName);
                    System.out.println(path);
                    System.out.println(fileName);
                    return projectName;

                //ユーザー登録
                case "http://192.168.3.4/sample/insert_users_information.php":
                    //System.out.println("*********************");
                    try {
                        //String project_information = params[0];
                        //String dates = "2020-05-13";
                        URL url = new URL(params0_url);
                        con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        OutputStream outputStream = con.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        //POSTデータの編集
                        String post_data = URLEncoder.encode("users_name", "UTF-8")
                                + "=" + URLEncoder.encode(editUser, "UTF-8")
                                + "&" + URLEncoder.encode("password", "UTF-8")
                                + "=" + URLEncoder.encode(editPass, "UTF-8")
                                + "&" + URLEncoder.encode("permission", "UTF-8")
                                + "=" + URLEncoder.encode(editPer, "UTF-8");
                        System.out.println(post_data);
                        bufferedWriter.write(post_data);
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        outputStream.close();
                        InputStream inputStream = con.getInputStream();
                        String encoding = con.getContentEncoding();
                        if (null == encoding) {
                            encoding = "UTF-8";
                        }
                        InputStreamReader inReader = new InputStreamReader(inputStream, encoding);
                        BufferedReader bufferedReader = new BufferedReader(inReader);
                        String line = bufferedReader.readLine();
                        while (line != null) {
                            conResult.append(line);
                            line = bufferedReader.readLine();
                        }
                        bufferedReader.close();
                        inputStream.close();
                        con.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;


                //図面登録
                case "http://10.20.170.52/sample/uploadPDF.php":

                    //http接続
                    //try {
                    //URLオブジェクト作成
                    //URL url = new URL(params[0]);
                    //System.out.println(url);
                    File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/lowcarbon05.pdf");
                    //File pdfFile = new File(dir.getAbsolutePath()+"/lowcarbon05.pdf");
                    System.out.println(pdfFile);
                    break;

                //図面登録ボタン押下PDFデータの追加
                case "http://192.168.3.4/sample/insert_pdf_information":
                    System.out.println("*********************");
                    try {
                        //String project_information = params[0];
                        //String dates = "2020-05-13";
                        URL url = new URL(params0_url);
                        con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        OutputStream outputStream = con.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        //POSTデータの編集
                        String post_data = URLEncoder.encode("projects_name", "UTF-8")
                                + "=" + URLEncoder.encode(spinnerInfo, "UTF-8");
                        System.out.println(post_data);
                        bufferedWriter.write(post_data);
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        outputStream.close();
                        InputStream inputStream = con.getInputStream();
                        String encoding = con.getContentEncoding();
                        if (null == encoding) {
                            encoding = "UTF-8";
                        }
                        InputStreamReader inReader = new InputStreamReader(inputStream, encoding);
                        BufferedReader bufferedReader = new BufferedReader(inReader);
                        String line = bufferedReader.readLine();
                        while (line != null) {
                            conResult.append(line);
                            line = bufferedReader.readLine();
                        }
                        bufferedReader.close();
                        inputStream.close();
                        con.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                //プロジェクト登録ボタン押下
                case "http://192.168.3.4/sample/insert_projects_information.php":
                    System.out.println("*********************");
                    try {
                        //String project_information = params[0];
                        //String dates = "2020-05-13";
                        URL url = new URL(params0_url);
                        con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        OutputStream outputStream = con.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        //POSTデータの編集
                        String post_data = URLEncoder.encode("projects_name", "UTF-8")
                                + "=" + URLEncoder.encode(editProjectName, "UTF-8");
                                System.out.println(post_data);
                        bufferedWriter.write(post_data);
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        outputStream.close();
                        InputStream inputStream = con.getInputStream();
                        String encoding = con.getContentEncoding();
                        if (null == encoding) {
                            encoding = "UTF-8";
                        }
                        InputStreamReader inReader = new InputStreamReader(inputStream, encoding);
                        BufferedReader bufferedReader = new BufferedReader(inReader);
                        String line = bufferedReader.readLine();
                        while (line != null) {
                            conResult.append(line);
                            line = bufferedReader.readLine();
                        }
                        bufferedReader.close();
                        inputStream.close();
                        con.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                    //画面遷移後プロジェクト選択
                case "http://192.168.3.4/sample/getFileName.php":
                    try {
                        URL url = new URL(params0_url);
                        con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        OutputStream outputStream = con.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        //POSTデータの編集
                        String post_data = URLEncoder.encode("projects_name", "UTF-8")
                                + "=" + URLEncoder.encode("1", "UTF-8");
                        System.out.println(post_data);
                        bufferedWriter.write(post_data);
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        outputStream.close();
                        InputStream inputStream = con.getInputStream();
                        String encoding = con.getContentEncoding();
                        if (null == encoding) {
                            encoding = "UTF-8";
                        }
                        InputStreamReader inReader = new InputStreamReader(inputStream, encoding);
                        BufferedReader bufferedReader = new BufferedReader(inReader);
                        String line = bufferedReader.readLine();
                        while (line != null) {
                            conResult.append(line);
                            line = bufferedReader.readLine();
                        }
                        bufferedReader.close();
                        inputStream.close();
                        con.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //JSONからデータの取得
                    Pattern p_fileName = Pattern.compile(regex_fileName);
                    checkFileName(p_fileName, conResult.toString());
                    Pattern p_pdfId = Pattern.compile(regex_pdfId);
                    checkPdfId(p_pdfId, conResult.toString());
                    return fileName;

            }//switch終わり
            return null;
        }

        @Override
        public void onPostExecute(ArrayList<String> stringArrayList){

            System.out.println("****************" + stringArrayList);
            if (projectName.equals(stringArrayList)) {// ArrayAdapter
                ArrayAdapter<String> adapter
                        = new ArrayAdapter<String>(MyPage.this,
                        android.R.layout.simple_spinner_item, stringArrayList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // spinner に adapter をセット
                spinner.setAdapter(adapter);
            }
            else if (stringArrayList == null) {
                System.out.println("nullに入って何もしない");
            }
            else if (stringArrayList == fileName){
                ArrayAdapter<String> adapterFile
                        = new ArrayAdapter<String>(MyPage.this,
                        android.R.layout.simple_spinner_item, Collections.singletonList(fileName.get(0)));

                adapterFile.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // spinner に adapter をセット
                spinnerFile.setAdapter(adapterFile);
            }
        }
    }

    //正規表現でJSON形式から配列に当てはめる
    private static void checkProjectName(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            projectName.add(pName.substring(18, pName.length() - 2));
            //System.out.println(m.group());
        }
    }
    private static void checkProjectId(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            projectId.add(pName.substring(16, pName.length() - 2));
            projects_id = projectId.get(0);
            System.out.println("projectsId=" + projects_id);
            //System.out.println("-------------" + projects_id);
            //System.out.println(m.group());
        }
    }
    private static void checkFileName(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            fileName.add(m.group().substring(13, m.group().length() - 2));
            //System.out.println(m.group());
        }
    }
    private static void checkPdfId(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            pdfId.add(m.group().substring(11, m.group().length() - 2));
            pdf_id = pdfId.get(0);
            System.out.println(m.group());
        }
    }
    private static void checkPath(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            path.add(m.group().substring(9, m.group().length() - 1));
            //System.out.println(m.group());
        }
    }
    public void showPDF(View view){
        //ファイル名のspinnerに選択されているものを表示する
        paramFileName = spinnerFile.getSelectedItem().toString();
        Intent intent = new Intent(this, showPDF.class);
        startActivity(intent);
    }

    public void createProjectClick(View view){
        final EditText editTextProject = new EditText(MyPage.this);
        editTextProject.setHint("プロジェクト名");
        AlertDialog.Builder builder = new AlertDialog.Builder(MyPage.this);
        builder.setMessage("ユーザー名、パスワード、権限を入力してください")
                .setView(editTextProject)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editProjectName = editTextProject.getText().toString();
                        getPDFInfo gp = new getPDFInfo();
                        gp.execute(insertProjectsInformation);
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();

        //EditText editText = findViewById(R.id.createProject);
        //String editProjectName = editText.getText().toString();
        //System.out.println(editProjectName);
    }

    public void insertUser(View view){
        final EditText editTextUser = new EditText(MyPage.this);
        editTextUser.setHint("ユーザー名");
        final EditText editTextPassword = new EditText(MyPage.this);
        editTextPassword.setHint("パスワード");
        final EditText editTextPermission = new EditText(MyPage.this);
        editTextPermission.setHint("権限（１、２、３)");
        AlertDialog.Builder builder = new AlertDialog.Builder(MyPage.this);
        LinearLayout layout = new LinearLayout(MyPage.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editTextUser);
        layout.addView(editTextPassword);
        layout.addView(editTextPermission);
        builder.setMessage("ユーザー名、パスワード、権限を入力してください")
                .setView(layout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editUser = editTextUser.getText().toString();
                        editPass = editTextPassword.getText().toString();
                        editPer = editTextPermission.getText().toString();
                        getPDFInfo dp = new getPDFInfo();
                        dp.execute(insertUsersInformation);
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();


    }

}
