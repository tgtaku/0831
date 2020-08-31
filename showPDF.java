package com.example.sampleapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class showPDF extends AppCompatActivity {



    String url = "http://";
    LinearLayout linearLayout;
    FrameLayout frameLayout;
    public static ArrayList<Bitmap> bitmapPDF = new ArrayList<>();
    private static int pageNum;
    private static TextView pdfPage;
    private static ImageView view;
    private int i = 1;
    private static String pageText;
    private final int CODE_MULTIPLE_IMG_GALLERY = 2;
    public String insertLocalPicturesInformation = "http://192.168.3.4/sample/insert_local_pictures_information.php";
    public String insertPicturesInformation = "http://192.168.3.4/sample/insert_pictures_information.php";
    public String insertReportPlacesInformation = "http://192.168.3.4/sample/insert_report_places_information.php";
    public String getReportPointInformation = "http://192.168.3.4/sample/get_report_point_information.php";
    public String getServerPicturesInformation = "http://192.168.3.4/sample/get_server_pictures_information.php";

    public static String fileNameForInsert,pointX,pointY,pathForInsert;

    //int flag = 0;
    int flag = 2;

    //図面描画用
    public static ArrayList<String> filesName;// = new ArrayList<String>();
    public static ArrayList<String> filesNameServer;// = new ArrayList<String>();
    public static ArrayList<String> reportPoint = new ArrayList<>();
    public static ArrayList<String> touchX;// = new ArrayList<>();
    public static ArrayList<String> touchY;// = new ArrayList<>();
    public static ArrayList<String> touchXServer;// = new ArrayList<>();
    public static ArrayList<String> touchYServer;// = new ArrayList<>();
    public ImageView[] imageViewLocal;
    public ImageView imageViewServer;
    public FrameLayout frameLayoutSample;
    public static int m = 0;

    public ArrayList<ImageView> images = new ArrayList<>();
    public ArrayList<String> pointTitle = new ArrayList<>();

    //事前情報の取得
    String regex_filesName = "\"files_name\":.+?\",";
    String regex_pointX = "\"point_x\":.+?\",";
    String regex_pointY = "\"point_y\":.+?\",";
    String regex_X = "\"X\":.+?\",";
    String regex_Y = "\"Y\":.+?\",";
    String regex_report_point = "\"report_place_name\":.+?\",";

    //id
    String projectsId;
    String pdfId;
    String reportPlaceId;
    String userId;
    String companyId;



    Uri _imageUri;

    public String title;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_p_d_f);

        //id
        projectsId = MyPage.projects_id;
        pdfId = MyPage.pdf_id;


                //linearLayout = findViewById(R.id.linearLayout);

        frameLayout = findViewById(R.id.frameLayout);
        pdfPage = findViewById(R.id.page);

        //File sdcard = Environment.getExternalStorageDirectory(DOWNLOAD_SERVICE);
        File path = new File("sdcard/Download/");
        //System.out.println(path);
        ParcelFileDescriptor fd = null;
        PdfRenderer renderer = null;
        PdfRenderer.Page page = null;
        //PdfRenderer.Page page = null;
        try {
            // SDカード直下からtest.pdfを読み込み、1ページ目を取得
            fd = ParcelFileDescriptor.open(new File(path, MyPage.paramFileName), ParcelFileDescriptor.MODE_READ_ONLY);
            renderer = new PdfRenderer(fd);
            pageNum = renderer.getPageCount();
            page = renderer.openPage(0);


            view = (ImageView) findViewById(R.id.pdfImage);
            int viewWidth = view.getWidth();
            int viewHeight = view.getHeight();
            float pdfWidth = page.getWidth();
            float pdfHeight = page.getHeight();
            Log.i("test", "viewWidth=" + viewWidth + ", viewHeight=" + viewHeight
                    + ", pdfWidth=" + pdfWidth + ", pdfHeight=" + pdfHeight);

            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            bitmapPDF.add(Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888));
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            view.setImageBitmap(bitmap);
            pageText = i + "ページ / " + pageNum +"ページ";
            pdfPage.setText(pageText);
        } catch (FileNotFoundException e) {
            getPdf(view);
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fd != null) {
                    fd.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (page != null) {
                page.close();
            }
            if (renderer != null) {
                renderer.close();
            }
        }


        showPdf sp = new showPdf();
        sp.execute(getReportPointInformation, getServerPicturesInformation);

        //pictureInfo
        //TextView picTitle = new TextView(this);
    }



    private class showPdf extends AsyncTask<String, Void, String> {
        @Override
        public String doInBackground(String... params) {
            /*ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
            Bitmap bmp = null;*/
            filesName = new ArrayList<String>();
            filesNameServer = new ArrayList<String>();
            touchX = new ArrayList<>();
            touchY = new ArrayList<>();
            touchXServer = new ArrayList<>();
            touchYServer = new ArrayList<>();


            String params0_url = params[0];
            HttpURLConnection con = null;
            //http接続のレスポンスデータとして取得するInputStreamオブジェクトを宣言（try外）
            InputStream is = null;
            //返却用の変数
            StringBuffer conResult = new StringBuffer();
            switch (params0_url) {
                case "http://192.168.3.4/sample/insert_pictures_information.php":
                //case "http://192.168.3.4/sample/insert_local_pictures_information.php":
                    //カメラ操作後の処理
                    System.out.println("insert_pictures_information.php");


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
                        String pageNo = String.valueOf(i);
                        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd");
                        Date now = new Date(System.currentTimeMillis());
                        StringBuilder sb = new StringBuilder();
                        sb.append(dataFormat.format(now));
                        sb.insert(4, "-");
                        sb.insert(7, "-");
                        pathForInsert = "http://192.168.3.4/sample/images/" + fileNameForInsert;
                        String post_data = URLEncoder.encode("pictures_name", "UTF-8") + "=" + URLEncoder.encode(fileNameForInsert, "UTF-8") + "&" +
                                URLEncoder.encode("pdf_id", "UTF-8") + "=" + URLEncoder.encode(pdfId, "UTF-8") + "&" +
                                URLEncoder.encode("users_id", "UTF-8") + "=" + URLEncoder.encode(MainActivity.host, "UTF-8") + "&" +
                                URLEncoder.encode("report_place_id", "UTF-8") + "=" + URLEncoder.encode(reportPlaceId, "UTF-8") + "&" +
                                URLEncoder.encode("page", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8") + "&" +
                                URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(sb.toString(), "UTF-8") + "&" +
                                URLEncoder.encode("path", "UTF-8") + "=" + URLEncoder.encode(pathForInsert, "UTF-8");
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
                        System.out.println(conResult.toString());
                        bufferedReader.close();
                        inputStream.close();
                        con.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //画像データをアップロード

                    break;

                case "http://192.168.3.4/sample/get_report_point_information.php":
                    String params1_url = params[1];
                    StringBuffer _conResult = new StringBuffer();
                    try {
                        System.out.println("reportPointInformation.php");
                        URL url = new URL(params0_url);
                        con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        OutputStream outputStream = con.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        String _page = String.valueOf(i);
                        //POSTデータの編集
                        String post_data = URLEncoder.encode("page", "UTF-8")
                                + "=" + URLEncoder.encode(_page, "UTF-8");
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
                    System.out.println(conResult.toString());
                    //JSONデータからファイル名、ポイントの取得
                    /*Pattern p_filesName = Pattern.compile(regex_filesName);
                    checkFilesName(p_filesName, conResult.toString());
                    System.out.println(filesName);*/
                    Pattern p_pointX = Pattern.compile(regex_X);
                    checkX(p_pointX, conResult.toString());
                    System.out.println(touchX);
                    Pattern p_pointY = Pattern.compile(regex_Y);
                    checkY(p_pointY, conResult.toString());
                    System.out.println(touchY);
                    Pattern p_report_point = Pattern.compile(regex_report_point);
                    checkReportPoint(p_report_point, conResult.toString());
                    System.out.println(reportPoint);

                    /*try{
                    System.out.println("serverPicturesInformation.php");
                    URL url = new URL(params1_url);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    OutputStream outputStream = con.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String _page = String.valueOf(i);
                    //POSTデータの編集
                    String post_data = URLEncoder.encode("page", "UTF-8")
                            + "=" + URLEncoder.encode(_page, "UTF-8");
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
                        _conResult.append(line);
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
            System.out.println(_conResult.toString());
            //JSONデータからファイル名、ポイントの取得
            ///Pattern p_filesName = Pattern.compile(regex_filesName);
            *//*checkServerFilesName(p_filesName, _conResult.toString());
            System.out.println(filesNameServer);*//*
            //Pattern p_pointX = Pattern.compile(regex_pointX);
            checkServerPointX(p_pointX, _conResult.toString());
            System.out.println(touchXServer);
            //Pattern p_pointY = Pattern.compile(regex_pointY);
            checkServerPointY(p_pointY, _conResult.toString());
            System.out.println(touchYServer);*/
                    break;

                case "http://192.168.3.4/sample/insert_report_places_information.php":
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
                        String pageNo = String.valueOf(i);
                        String reportPlace = params[1];

                        String post_data = URLEncoder.encode("pdf_id", "UTF-8") + "=" + URLEncoder.encode(pdfId, "UTF-8") + "&" +
                                URLEncoder.encode("report_place_name", "UTF-8") + "=" + URLEncoder.encode(reportPlace, "UTF-8") + "&" +
                                URLEncoder.encode("page", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8") + "&" +
                                URLEncoder.encode("point_x", "UTF-8") + "=" + URLEncoder.encode(pointX, "UTF-8") + "&" +
                                URLEncoder.encode("point_y", "UTF-8") + "=" + URLEncoder.encode(pointY, "UTF-8");
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
                        System.out.println(conResult.toString());
                        bufferedReader.close();
                        inputStream.close();
                        con.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
            }

            //return
            //return bitmapArrayList;
            String result = params0_url;
            return result;


        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onPostExecute(String result) {
            String param = result;
            switch (param) {
                case "http://192.168.3.4/sample/get_report_point_information.php":
                    System.out.println("#################################333");
                    //描画の設定
                    frameLayout = findViewById(R.id.frameLayout);
                    int num = 0;
                    m = 0;
                    imageViewLocal = new ImageView[touchY.size()];
                    while(num < touchY.size()){
                        imageViewLocal[m] = new ImageView(showPDF.this);
                        imageViewLocal[m].setImageResource(R.drawable.local_pic);
                        imageViewLocal[m].setTag(String.valueOf(m));
                        //imageViewLocal.setId(m);
                        //imageViewLocal.setId(View.generateViewId());
                        imageViewLocal[m].setOnClickListener(new View.OnClickListener(){
                            public void onClick(View view){
                                //マークの色を変更
                                final View touchView = view;
                                String tag = String.valueOf(view.getTag());
                                imageViewLocal[Integer.valueOf(tag)].setImageResource(R.drawable.server_pic);
                                //アラートダイアログ出力
                                pointX = String.valueOf(view.getX());
                                pointY = String.valueOf(view.getY());
                                System.out.println("-------------------------------"+ Integer.valueOf(tag));
                                title = reportPoint.get(Integer.valueOf(tag));
                                int rePlaceId = Integer.valueOf(tag) + 1;
                                reportPlaceId = String.valueOf(rePlaceId);
                                //String title = "X:" + pointX + "Y:" + pointY;
                                int xPoint = (int) view.getX();
                                int yPoint = (int) view.getY();
                                System.out.println(title);
                                //String title = "*********************aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
                                String[] items = {"写真撮影", "写真管理", "報告書作成"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(showPDF.this, R.style.MyAlertDialogStyle);
                                builder.setTitle(title)
                                        .setItems(items , new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String tag = String.valueOf(touchView.getTag());
                                                switch(which){
                                                    case 0:
                                                        System.out.println("0");
                                                        imageViewLocal[Integer.valueOf(tag)].setImageResource(R.drawable.local_pic);
                                                        //Uri _imageUri;
                                                        if(ActivityCompat.checkSelfPermission(showPDF.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                                            //WRITE_EXTERNAL_STORAGEの許可を求めるダイアログを表示。その際、リクエストコードを2000に設定。
                                                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                                            ActivityCompat.requestPermissions(showPDF.this, permissions, 2000);
                                                            return;
                                                        }

                                                        //日時データを「yyyyMMddHHmmss」の形式に整形するフォーマッタを生成。
                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                                                        //現在の日時を取得。
                                                        Date now = new Date(System.currentTimeMillis());
                                                        //取得した日時データを「yyyyMMddHHmmss」形式に整形した文字列を生成。
                                                        String nowStr = dateFormat.format(now);
                                                        //ストレージに格納する画像のファイル名を生成。ファイル名の一意を確保するためにタイムスタンプの値を利用。
                                                        String fileName = "UseCameraActivityPhoto_" + nowStr +".jpg";
                                                        fileNameForInsert = fileName;
                                                        //System.out.println("----------------------");
                                                        //System.out.println(fileName);

                                                        //ContentValuesオブジェクトを生成。
                                                        ContentValues values = new ContentValues();
                                                        //画像ファイル名を設定。
                                                        values.put(MediaStore.Images.Media.TITLE, fileName);


                                                        //画像ファイルの種類を設定。
                                                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

                                                        //ContentResolverオブジェクトを生成。
                                                        ContentResolver resolver = getContentResolver();
                                                        //ContentResolverを使ってURIオブジェクトを生成。
                                                        _imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                                        //Intentオブジェクトを生成。
                                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                        //Extra情報として_imageUriを設定。
                                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri);
                                                        //アクティビティを起動。
                                                        startActivityForResult(intent, 200);
                                                        break;
                                                    case 1:
                                                        System.out.println("1");
                                                        int rePlaceId = Integer.valueOf(tag) + 1;
                                                        reportPlaceId = String.valueOf(rePlaceId);
                                                        System.out.println("reportPlaceId = "+ rePlaceId);
                                                        Intent intent1 = new Intent(showPDF.this, selectPic.class);
                                                        intent1.putExtra("pointID", reportPlaceId);
                                                        startActivity(intent1);
                                                        imageViewLocal[Integer.valueOf(tag)].setImageResource(R.drawable.local_pic);
                                                        break;
                                                    case 2:
                                                        System.out.println("2");
                                                        imageViewLocal[Integer.valueOf(tag)].setImageResource(R.drawable.local_pic);
                                                        break;
                                                }
                                            }
                                        })
                                        .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String tag = String.valueOf(touchView.getTag());
                                                imageViewLocal[Integer.valueOf(tag)].setImageResource(R.drawable.local_pic);
                                            }
                                        });
                                //.setView(alert);
                                AlertDialog alertDialog = builder.create();

                                WindowManager.LayoutParams wm = alertDialog.getWindow().getAttributes();
                                if(view.getX() < 400){
                                    wm.gravity = Gravity.TOP | Gravity.LEFT;
                                    if(view.getY() < 794){
                                        wm.y = 250 + (int)view.getY();
                                        wm.alpha = 0.6f;
                                    }else{
                                        wm.y = 110 + (int)view.getY() - 794;
                                        wm.alpha = 0.6f;
                                    }
                                }
                                else if(view.getX() > 700 | view.getX() == 700){
                                    wm.gravity = Gravity.TOP | Gravity.RIGHT;
                                    if(view.getY() < 794){
                                        wm.y = 250 + (int)view.getY();
                                        wm.alpha = 0.6f;
                                    }else{
                                        wm.y = 110 + (int)view.getY() - 794;
                                        wm.alpha = 0.6f;
                                    }
                                }else{
                                    if(view.getY() < 794){
                                        wm.gravity = Gravity.CENTER |Gravity.TOP;
                                        wm.y = 250 + (int)view.getY();
                                        wm.alpha = 0.6f;
                                    }else{
                                        wm.gravity = Gravity.CENTER | Gravity.TOP;
                                        wm.y = 110 + (int)view.getY() - 794;
                                        wm.alpha = 0.6f;
                                    }
                                }

                                System.out.println(wm.gravity);
                                System.out.println(wm.x);
                                System.out.println(wm.y);
                                //DisplayMetrics metrics = getResources().getDisplayMetrics();
                                alertDialog.getWindow().setAttributes(wm);
                                alertDialog.show();
                            }
                        });

                        FrameLayout.LayoutParams lpLocal = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lpLocal.gravity = Gravity.TOP;
                        int X = Integer.valueOf(touchX.get(num));
                        lpLocal.leftMargin = X;
                        int Y = Integer.valueOf(touchY.get(num));
                        lpLocal.topMargin = Y;
                        frameLayout.addView(imageViewLocal[num], lpLocal);
                        num++;
                        m++;
                    }

                    num = 0;

                    //サーバーに保存した画像の描画
        /*while(num < touchYServer.size()){
            imageViewServer = new ImageView(showPDF.this);
            imageViewServer.setImageResource(R.drawable.server_pic);
            imageViewServer.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    //マーク押下時の処理
                    System.out.println("-------------------------");
                }
            });
            FrameLayout.LayoutParams lpServer = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lpServer.gravity = Gravity.TOP;
            int X = Integer.valueOf(touchXServer.get(num));
            lpServer.leftMargin = X;
            int Y = Integer.valueOf(touchYServer.get(num));
            lpServer.topMargin = Y;
            frameLayout.addView(imageViewServer, lpServer);
            num++;
        }*/
                    break;
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event){
        if(flag == 1) {
            //X軸の取得
            float point_x = event.getX();
            int _pointX = ((int) point_x -20);
            pointX = String.valueOf(_pointX);
            //Y軸の取得
            float point_y = event.getY();
            int _pointY = ((int) point_y -280);
            pointY = String.valueOf(_pointY);

            final ImageView imageView = new ImageView(this);
            images.add(imageView);
            int in = images.size();
            images.get(in-1).setTag(in-1);
            imageView.setImageResource(R.drawable.local_pic);
            imageView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    //マーク押下時の処理
                    final int out = (int)view.getTag();
                    System.out.println(out);
                    String pointName = pointTitle.get(out);
                    images.get(out).setImageResource(R.drawable.server_pic);


                    //アラートダイアログ出力
                    pointX = String.valueOf(view.getX());
                    pointY = String.valueOf(view.getY());
                    System.out.println(pointName);
                    //String title = "*********************aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
                    String[] items = {"写真撮影", "写真管理", "報告書作成"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(showPDF.this, R.style.MyAlertDialogStyle);
                    builder.setTitle(pointName)
                            .setItems(items , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //String tag = String.valueOf(touchView.getTag());
                                    switch(which){
                                        case 0:
                                            System.out.println("0");
                                            images.get(out).setImageResource(R.drawable.local_pic);
                                            //Uri _imageUri;
                                            if(ActivityCompat.checkSelfPermission(showPDF.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                                //WRITE_EXTERNAL_STORAGEの許可を求めるダイアログを表示。その際、リクエストコードを2000に設定。
                                                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                                ActivityCompat.requestPermissions(showPDF.this, permissions, 2000);
                                                return;
                                            }

                                            //日時データを「yyyyMMddHHmmss」の形式に整形するフォーマッタを生成。
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                                            //現在の日時を取得。
                                            Date now = new Date(System.currentTimeMillis());
                                            //取得した日時データを「yyyyMMddHHmmss」形式に整形した文字列を生成。
                                            String nowStr = dateFormat.format(now);
                                            //ストレージに格納する画像のファイル名を生成。ファイル名の一意を確保するためにタイムスタンプの値を利用。
                                            String fileName = "UseCameraActivityPhoto_" + nowStr +".jpg";
                                            fileNameForInsert = fileName;
                                            //System.out.println("----------------------");
                                            //System.out.println(fileName);

                                            //ContentValuesオブジェクトを生成。
                                            ContentValues values = new ContentValues();
                                            //画像ファイル名を設定。
                                            values.put(MediaStore.Images.Media.TITLE, fileName);


                                            //画像ファイルの種類を設定。
                                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

                                            //ContentResolverオブジェクトを生成。
                                            ContentResolver resolver = getContentResolver();
                                            //ContentResolverを使ってURIオブジェクトを生成。
                                            _imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                            //Intentオブジェクトを生成。
                                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            //Extra情報として_imageUriを設定。
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri);
                                            //アクティビティを起動。
                                            startActivityForResult(intent, 200);
                                            break;
                                        case 1:
                                            System.out.println("1");
                                            Intent intent1 = new Intent(showPDF.this, selectPic.class);
                                            startActivity(intent1);
                                            images.get(out).setImageResource(R.drawable.local_pic);
                                            break;
                                        case 2:
                                            System.out.println("2");
                                            images.get(out).setImageResource(R.drawable.local_pic);
                                            break;
                                    }
                                }
                            })
                            .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //String tag = String.valueOf(touchView.getTag());
                                    images.get(out).setImageResource(R.drawable.local_pic);
                                }
                            });
                    //.setView(alert);
                    AlertDialog alertDialog = builder.create();

                    WindowManager.LayoutParams wm = alertDialog.getWindow().getAttributes();
                    if(view.getX() < 400){
                        wm.gravity = Gravity.TOP | Gravity.LEFT;
                        if(view.getY() < 794){
                            wm.y = 250 + (int)view.getY();
                            wm.alpha = 0.6f;
                        }else{
                            wm.y = 110 + (int)view.getY() - 794;
                            wm.alpha = 0.6f;
                        }
                    }
                    else if(view.getX() > 700 | view.getX() == 700){
                        wm.gravity = Gravity.TOP | Gravity.RIGHT;
                        if(view.getY() < 794){
                            wm.y = 250 + (int)view.getY();
                            wm.alpha = 0.6f;
                        }else{
                            wm.y = 110 + (int)view.getY() - 794;
                            wm.alpha = 0.6f;
                        }
                    }else{
                        if(view.getY() < 794){
                            wm.gravity = Gravity.CENTER |Gravity.TOP;
                            wm.y = 250 + (int)view.getY();
                            wm.alpha = 0.6f;
                        }else{
                            wm.gravity = Gravity.CENTER | Gravity.TOP;
                            wm.y = 110 + (int)view.getY() - 794;
                            wm.alpha = 0.6f;
                        }
                    }

                    //DisplayMetrics metrics = getResources().getDisplayMetrics();
                    alertDialog.getWindow().setAttributes(wm);
                    alertDialog.show();



                }
            });

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.TOP;
            lp.leftMargin = _pointX;
            lp.topMargin = _pointY;
            frameLayout.addView(imageView, lp);

            //取得した内容をログに表示
            Log.d("TouchEvent", "X:" + pointX + ",Y:" + pointY);

            //アラートダイアログ出力
            AlertDialog.Builder builder = new AlertDialog.Builder(showPDF.this);
            final EditText editText = new EditText(showPDF.this);
            editText.setHint("施工箇所");
            builder.setMessage("施工箇所名を登録してください")
                    .setView(editText)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String editTextInfo = editText.getText().toString();
                            pointTitle.add(editTextInfo);

                            //アップロード
                            showPdf sp = new showPdf();
                            sp.execute(insertReportPlacesInformation, editTextInfo);
                        }
                    })
                    .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            imageView.setImageResource(0);
                        }
                    })
                    .show();
            flag = 0;

        }else if(flag == 2){

            event.getX();
            System.out.println(event.getX());
            System.out.println(event.getY());

            //event.getX();
            //System.out.println(event.getY());

        }



        return true;
    }


    /*
    NextPage
    @param myButton nextPage
    */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onClickNextPage(View myButton){
        if(i != pageNum){
            //frameLayout = new FrameLayout(showPDF.this);
            //frameLayout.removeAllViews();
            linearLayout.removeAllViews();

            //linearLayout.addView(frameLayout);
            setContentView(R.layout.activity_show_p_d_f);
            frameLayout = findViewById(R.id.frameLayout);
            pdfPage = findViewById(R.id.page);
            /*
            int num = 0;
            while(num < touchYServer.size()){
                imageViewServer = new ImageView(this);
                imageViewServer.setImageResource(R.drawable.server_pic);
                imageViewServer.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        //マーク押下時の処理
                        System.out.println("-------------------------");
                    }
                });
                FrameLayout.LayoutParams lpServer = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lpServer.gravity = Gravity.TOP;
                lpServer.leftMargin = touchXServer.get(num);
                lpServer.topMargin = touchYServer.get(num);
                frameLayout.addView(imageViewServer, lpServer);
                num++;
            }
*/

            File path = new File("sdcard/Download/");
            System.out.println(path);
            ParcelFileDescriptor fd = null;
            PdfRenderer renderer = null;
            PdfRenderer.Page page = null;
            try {
                fd = ParcelFileDescriptor.open(new File(path, "lowcarbon05.pdf"), ParcelFileDescriptor.MODE_READ_ONLY);
                renderer = new PdfRenderer(fd);
                page = renderer.openPage(i);

                view = (ImageView) findViewById(R.id.pdfImage);
                int viewWidth = view.getWidth();
                int viewHeight = view.getHeight();
                float pdfWidth = page.getWidth();
                float pdfHeight = page.getHeight();
                Log.i("test", "viewWidth=" + viewWidth + ", viewHeight=" + viewHeight
                        + ", pdfWidth=" + pdfWidth + ", pdfHeight=" + pdfHeight);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                view.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fd != null) {
                        fd.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (page != null) {
                    page.close();
                }
                if (renderer != null) {
                    renderer.close();
                }
            }
            i++;
            pageText = i + "ページ / " + pageNum +"ページ";
            pdfPage.setText(pageText);
            //pdfPage.setText(i + "ページ");
            /*imageViewLocal.setImageResource(0);
            imageViewServer.setImageResource(0);*/
            showPdf sp = new showPdf();
            sp.execute(getReportPointInformation, getServerPicturesInformation);
        }
    }

    /*
    BackPage
    @param myButton backPage
    */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onClickBackPage(View myButton){
        if(i != 1){
            frameLayout = findViewById(R.id.frameLayout);
            //frameLayout.removeView(imageViewLocal);
            frameLayout = new FrameLayout(showPDF.this);
            //frameLayout = new FrameLayout(showPDF.this);
            //String pathname = "http://192.168.3.4/sample/pdf/sampleProject1/lowcarbon05.pdf";
            //File path = new File()
            File path = new File("sdcard/Download/");
            //System.out.println(path);
            ParcelFileDescriptor fd = null;
            PdfRenderer renderer = null;
            PdfRenderer.Page page = null;
            try {
                fd = ParcelFileDescriptor.open(new File(path,"lowcarbon05.pdf"), ParcelFileDescriptor.MODE_READ_ONLY);
                //fd = ParcelFileDescriptor.open(new File(pathname), ParcelFileDescriptor.MODE_READ_ONLY);
                renderer = new PdfRenderer(fd);
                pageNum = renderer.getPageCount();
                page = renderer.openPage(i-2);

                view = (ImageView) findViewById(R.id.pdfImage);
                int viewWidth = view.getWidth();
                int viewHeight = view.getHeight();
                float pdfWidth = page.getWidth();
                float pdfHeight = page.getHeight();
                Log.i("test", "viewWidth=" + viewWidth + ", viewHeight=" + viewHeight
                        + ", pdfWidth=" + pdfWidth + ", pdfHeight=" + pdfHeight);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                view.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fd != null) {
                        fd.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (page != null) {
                    page.close();
                }
                if (renderer != null) {
                    renderer.close();
                }
            }
            i--;
            pageText = i + "ページ / " + pageNum +"ページ";
            pdfPage.setText(pageText);
            //pdfPage.setText(i + "ページ /" + pageNum +" ページ");
            //imageViewLocal.setImageResource(0);
            /*
            imageViewServer.setImageResource(0);*/
            showPdf sp = new showPdf();
            sp.execute(getReportPointInformation, getServerPicturesInformation);
        }
    }

    public void cameraClick(View view){
        //アラートダイアログ出力
        //final EditText editTextProject = new EditText(MyPage.this);
        //editTextProject.setHint("プロジェクト名");.setView(editTextProject)editProjectName = editTextProject.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(showPDF.this);
        builder.setMessage("写真撮影する場所を選択してください")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        flag = 1;
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    public void createReportClick(View view){
        //アラートダイアログ出力
        AlertDialog.Builder builder = new AlertDialog.Builder(showPDF.this);
        builder.setMessage("写真撮影する場所を選択してください")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        flag = 1;
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
        //タッチイベントフラグ立てる

        //Intent intent = new Intent(this, createReport.class);
        //startActivity(intent);
        //dialogFragment dialog = new dialogFragment();
        //dialog.show(getSupportFragmentManager(), "dialogFragment");
        //Intent intent = new Intent(this, selectReportType.class);
        //startActivity(intent);
    }

    public void reportViewClick(View view){
        Intent intent = new Intent(this, showReport.class);
        startActivity(intent);
    }

    public void imageUploadClick(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SelectMulti"),CODE_MULTIPLE_IMG_GALLERY);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200 && resultCode == RESULT_OK){
            //※ファイル名をインサートするための処理
            String[] proj = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME};
            Cursor c = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,proj,null,null,null);
            if(c.moveToFirst()){
                do{
                    //Bitmap bit = BitmapFactory.decodeFile(c.getString(0));
                    String fileNameGet = c.getString(1);
                    fileNameForInsert = fileNameGet;
                }while (c.moveToNext());
            }
            //※

            //写真をアップロードする処理
            //String env = String.valueOf(Environment.getExternalStorageDirectory());
            //String PATH env + "/Pictures";
            String PATH = "sdcard/Pictures/";
            System.out.println(PATH);
            final File useFile = new File(PATH, fileNameForInsert);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    String content_type = getMimeType(useFile.getPath());
                    String file_path = useFile.getAbsolutePath();
                    OkHttpClient client = new OkHttpClient();
                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type), useFile);

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

                        //progress.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();

            showPdf sp = new showPdf();
            sp.execute(insertPicturesInformation, title);
        }

        if (requestCode == CODE_MULTIPLE_IMG_GALLERY && resultCode == RESULT_OK) {
            ClipData clipData = data.getClipData();
            System.out.println(data);

            if(clipData != null){
                //img1.setImageURI(clipData.getItemAt(0).getUri());
                //img2.setImageURI(clipData.getItemAt(1).getUri());

                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    System.out.println(item);
                    Uri uri = item.getUri();
                    getPicPath(uri);
                    System.out.println(uri);
                    Log.e("MAS IMAGES", uri.toString());
                    System.out.println("入ってる");
                }
                System.out.println("なにも入ってない");
            }
        }
    }

    //Uri→path
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getPicPath(Uri uri){
        System.out.println("------------------------------------------------------------");
        System.out.println(uri);
        String id = DocumentsContract.getDocumentId(uri);
        String selection = "_id=?";
        String[] selectionArgs = new String[]{id.split(":")[1]};
        File file = null;
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.MediaColumns.DATA},selection,selectionArgs,null);
        if (cursor != null && cursor.moveToFirst()){
            file = new File(cursor.getString(0));
        }
        final File useFile = file;
        cursor.close();

        if(file != null) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String content_type = getMimeType(useFile.getPath());
                    String file_path = useFile.getAbsolutePath();
                    //ここでファイルパスから取得、正規表現でファイル名を取って配列へ
                    System.out.println(useFile.getAbsolutePath());
                    //return file.getAbsolutePath();
                    OkHttpClient client = new OkHttpClient();
                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type), useFile);

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

                        //progress.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }

        return null;
    }
    private String getMimeType(String path){
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
    public void getPdf(View view){
        //String title = MyPage.paramFileName;
        String title = "uploads_public_archive_0000006996_00_04-36sekou-keikakusyo.pdf";
        String dir = "http://192.168.3.4/sample/images/";
        String url = dir + title;

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);
        DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        request.setMimeType("application/pdf");
        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE |DownloadManager.Request.NETWORK_WIFI);
        downloadManager.enqueue(request);
    }

    //正規表現でJSON形式から配列に当てはめる
    private static void checkFilesName(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            filesName.add(pName.substring(15, pName.length() - 2));
            System.out.println(m.group());
        }
    }
    private static void checkPointX(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            touchX.add(pName.substring(12, pName.length() - 2));
            System.out.println(m.group());
        }
    }
    private static void checkPointY(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            touchY.add(pName.substring(12, pName.length() - 2));
            System.out.println(m.group());
        }
    }
    private static void checkX(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            touchX.add(pName.substring(6, pName.length() - 2));
            System.out.println(m.group());
        }
    }
    private static void checkY(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            touchY.add(pName.substring(6, pName.length() - 2));
            System.out.println(m.group());
        }
    }

    private static void checkServerFilesName(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            filesNameServer.add(pName.substring(15, pName.length() - 2));
            System.out.println(m.group());
        }
    }
    private static void checkServerPointX(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            touchXServer.add(pName.substring(12, pName.length() - 2));
            System.out.println(m.group());
        }
    }
    private static void checkServerPointY(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            touchYServer.add(pName.substring(12, pName.length() - 2));
            System.out.println(m.group());
        }
    }

    private Bitmap getBitmapFromAsset(String strName){
        AssetManager assetManager = getAssets();
        InputStream istr = null;
        try{
            istr = assetManager.open(strName);
        }catch (IOException e){
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }
    private static void checkReportPoint(Pattern p, String target){
        Matcher m = p.matcher(target);
        while(m.find()){
            String pName = m.group();
            reportPoint.add(pName.substring(22, pName.length() - 2));
            System.out.println(m.group());
        }
    }

}