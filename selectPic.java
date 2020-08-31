package com.example.sampleapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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

public class selectPic extends AppCompatActivity {
    public static ArrayList<String> picturesName;// = new ArrayList<String>();
    public static ArrayList<String> path;// = new ArrayList<String>();
    public static int picNum;

    //public static ScrollView scrollView;
    public static LinearLayout linearLayout_main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pic);

        //scrollView = new ScrollView(this);
        //scrollView = findViewById(R.id.scrollView);
        //setContentView(scrollView);

        //pointId
        Intent intent1 = getIntent();
        String reportPointId = intent1.getStringExtra("pointID");
        //System.out.println(reportPointId);
        create cr = new create();
        cr.execute(reportPointId);
    }

    private class create extends AsyncTask<String, Void, ArrayList<Bitmap>> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public ArrayList<Bitmap> doInBackground(String... params) {
            //http接続を行うHttpURLConnectionオブジェクトを宣言
            //finallyで解放するためにtry外で宣言
            HttpURLConnection con = null;
            //http接続のレスポンスデータとして取得するInputStreamオブジェクトを宣言（try外）
            InputStream is = null;
            //返却用の変数
            StringBuffer conResult = new StringBuffer();
            //String sw = params[0];
            String sw = "http:/192.168.3.4/sample/get_point_pictures_information.php";
            String checkResult = "";
            picturesName = new ArrayList<String>();
            path = new ArrayList<String>();
            switch (sw) {
                case "http:/192.168.3.4/sample/get_point_pictures_information.php":
                    try {
                        String report_place_id = params[0];
                        URL url = new URL(sw);
                        con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        OutputStream outputStream = con.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        //POSTデータの編集
                        String post_data = URLEncoder.encode("report_place_id", "UTF-8") + "=" + URLEncoder.encode(report_place_id, "UTF-8");// + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pass_word, "UTF-8");
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
                    //System.out.println("con" + conResult);
            }


            //事前情報の取得
            String regex_pictures_Name = "\"pictures_name\":.+?\",";
            //String regex_date = "\"date\":.+?\",";
            //String regex_recorder = "\"recorder\":.+?\",";
            //String regex_fileName = "\"fileName\":.+?\",";
            String regex_path = "\"path\":.+?\",";
            //String regex_comments = "\"comments\":.+?]";
            Pattern p_projectName = Pattern.compile(regex_pictures_Name);
            checkPicturesName(p_projectName, conResult.toString());
            System.out.println(picturesName);
            Pattern p_path = Pattern.compile(regex_path);
            checkPath(p_path, conResult.toString());
            System.out.println(path);

            //画像の抽出
            ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
            Bitmap bmp = null;

            //System.out.println("getImage" + address);
            int i = 0;
            picNum = picturesName.size();
            while (i < picturesName.size()) {
                String address = path.get(i);
                System.out.println(address);
                HttpURLConnection urlConnection = null;

                try {
                    URL url = new URL(address);
                    // HttpURLConnection インスタンス生成
                    urlConnection = (HttpURLConnection) url.openConnection();
                    // タイムアウト設定
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setConnectTimeout(20000);
                    // リクエストメソッド
                    urlConnection.setRequestMethod("GET");
                    // リダイレクトを自動で許可しない設定
                    urlConnection.setInstanceFollowRedirects(false);
                    // 接続
                    urlConnection.connect();
                    int resp = urlConnection.getResponseCode();
                    switch (resp) {
                        case HttpURLConnection.HTTP_OK:
                            try (InputStream ips = urlConnection.getInputStream()) {
                                bmp = BitmapFactory.decodeStream(ips);
                                bitmapArrayList.add(bmp);
                                ips.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case HttpURLConnection.HTTP_UNAUTHORIZED:
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    Log.d("debug", "downloadImage error");
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
                i++;
            }
            return bitmapArrayList;
        }

        @Override
        public void onPostExecute(ArrayList<Bitmap> bitmapArrayList) {
            System.out.println(bitmapArrayList);
            //描画準備
            //ImageView[] image = new ImageView[picNum];
            CheckBox ch1 = findViewById(R.id.ch1);


            ImageView imageView = findViewById(R.id.imageView1);
            imageView.setImageBitmap(bitmapArrayList.get(0));

            /*CheckBox ch2 = findViewById(R.id.ch2);


            ImageView imageView2 = findViewById(R.id.imageView2);
            imageView.setImageBitmap(bitmapArrayList.get(1));
*/
            //CheckBox[] ch = new CheckBox[picNum];

            /*LinearLayout linearLayout_ver = new LinearLayout(selectPic.this);
            linearLayout_ver.setOrientation(LinearLayout.VERTICAL);
            LinearLayout linearLayout_hor;
*/
            /*int j = 0;
            int k = 0;
            while(j < picNum){
                linearLayout_hor  = new LinearLayout(selectPic.this);
                linearLayout_hor.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout_hor.setWeightSum(10);
                while(k < 2){
                    if(j < picNum){
                        image[j] = new ImageView(selectPic.this);
                        image[j].setImageBitmap(bitmapArrayList.get(j));
                        ch[j] = new CheckBox(selectPic.this);
                        linearLayout_hor.addView(ch[j], new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1));
                        linearLayout_hor.addView(image[j], new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                4));
                        j++;
                        k++;
                    }
                }
                linearLayout_ver.addView(linearLayout_hor, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                k=0;
            }*/
            /*scrollView.addView(linearLayout_ver, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        */}
    }

    private static void checkPicturesName(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            picturesName.add(pName.substring(18, pName.length() - 2));
            System.out.println(m.group());
        }
    }
    private static void checkPath(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            path.add(pName.substring(9, pName.length() - 2));
            System.out.println(m.group());
        }
    }
}