package com.example.sampleapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static String loginResult = "";
    EditText UsernameEt, PasswordEt, HostEt;
    public static String host;
    public static String username;
    public static String usersId;
    public static ArrayList<String> projectsNum;
    String regex_users_id = "\"users_id\":.+?\",";
    String regex_projects_id = "\"projects_id\":.+?\",";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HostEt = (EditText)findViewById(R.id.editHost);
        UsernameEt = (EditText)findViewById(R.id.editUserName);
        PasswordEt = (EditText)findViewById(R.id.editPass);
    }
    public void loginClick(View view){
        username = UsernameEt.getText().toString();
        String password = PasswordEt.getText().toString();
        host = HostEt.getText().toString();
        String type = "login";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(host, username, password);

    }


    public class BackgroundWorker extends AsyncTask<String, Void, String> {
        Context context;
        AlertDialog alertDialog;

        BackgroundWorker(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if (params[0] == "sample1") {
                loginResult = "";
                String localHostUrl = "http://10.20.170.52/sample/EX_upload.php";
                HttpURLConnection httpURLConnection = null;
                try {
                    String project_information = params[0];
                    //String pass_word = params[2];
                    URL url = new URL(localHostUrl);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("projects_name", "UTF-8") + "=" + URLEncoder.encode(project_information, "UTF-8");// + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pass_word, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        loginResult += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(loginResult);

            } else {
                //企業IDを取得
                String localHostUrl = "http://192.168.3.4/sample/get_company_id.php";
                HttpURLConnection httpURLConnection = null;
                StringBuffer _conResult = new StringBuffer();

                try {
                    System.out.println("usersID.php");
                    URL url = new URL(localHostUrl);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    //POSTデータの編集
                    String post_data = URLEncoder.encode("name", "UTF-8")
                            + "=" + URLEncoder.encode(username, "UTF-8");
                    System.out.println(post_data);
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    String encoding = httpURLConnection.getContentEncoding();
                    if (null == encoding) {
                        encoding = "UTF-8";
                    }
                    InputStreamReader inReader = new InputStreamReader(inputStream, encoding);
                    BufferedReader bufferedReader = new BufferedReader(inReader);
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        _conResult.append(line);
                        line = bufferedReader.readLine();
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //JSONからデータの取得
                System.out.println(_conResult.toString());
                Pattern p_usersId = Pattern.compile(regex_users_id);
                checkUsersId(p_usersId, _conResult.toString());
                System.out.println(usersId);

                localHostUrl = "http://192.168.3.4/sample/get_assign_projects_id.php";
                httpURLConnection = null;
                _conResult = new StringBuffer();

                try {
                    System.out.println("projectsID.php");
                    URL url = new URL(localHostUrl);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    //POSTデータの編集
                    String post_data = URLEncoder.encode("companies_id", "UTF-8")
                            + "=" + URLEncoder.encode(host, "UTF-8")
                            + "&" + URLEncoder.encode("users_id", "UTF-8")
                            + "=" + URLEncoder.encode(String.valueOf(usersId), "UTF-8");

                    System.out.println(post_data);
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    String encoding = httpURLConnection.getContentEncoding();
                    if (null == encoding) {
                        encoding = "UTF-8";
                    }
                    InputStreamReader inReader = new InputStreamReader(inputStream, encoding);
                    BufferedReader bufferedReader = new BufferedReader(inReader);
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        _conResult.append(line);
                        line = bufferedReader.readLine();
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //JSONからデータの取得
                System.out.println(_conResult.toString());
                Pattern p_companyId = Pattern.compile(regex_projects_id);
                checkProjectsId(p_companyId, _conResult.toString());
                System.out.println(projectsNum);


                //ログイン処理
                loginResult = "";
                localHostUrl = "http://192.168.3.4/login.php";
                httpURLConnection = null;
                try {
                    String host = params[0];
                    String user_name = params[1];
                    String pass_word = params[2];
                    URL url = new URL(localHostUrl);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("company_id", "UTF-8") + "=" + URLEncoder.encode(host, "UTF-8") + "&" + URLEncoder.encode("users_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pass_word, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        loginResult += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(loginResult);
                //return loginResult;
            }
            return loginResult;
        }

        @Override
        protected void onPostExecute(String result){
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            //alertDialog.setMessage("result");
            //alertDialog.show();
            if(result.equals("login success!")) {
                Intent intent = new Intent(MainActivity.this, MyPage.class);
                startActivity(intent);
            }
            result = "";
        }
    }
    public void testClick(View view){
        String projects_name = "sample1";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(projects_name);

    }
    private static void checkUsersId(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            usersId = (pName.substring(13, pName.length() - 2));
            System.out.println(m.group());
        }
    }
    private static void checkProjectsId(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            projectsNum = new ArrayList<String>();
            projectsNum.add(pName.substring(16, pName.length() - 2));
            System.out.println(m.group());
        }
    }


}
