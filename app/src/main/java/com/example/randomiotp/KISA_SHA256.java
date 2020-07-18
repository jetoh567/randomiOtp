package com.example.randomiotp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*import java.nio.ByteBuffer;
 * import java.security.*;
import java.nio.ByteOrder;*/

import com.example.randomiotp.gps.GpsTracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 @file KISA_SHA256.java
 @brief SHA256 암호 알고리즘
 @author Copyright (c) 2013 by KISA
 @remarks http://seed.kisa.or.kr/
 */

import java.util.Random;

import co.junwei.bswabe.Bswabe;
import co.junwei.cpabe.Cpabe;
/*import kisa_sample.KISA_SHA256.Common;
import kisa_sample.KISA_SHA256.SHA256_INFO;*/



public class KISA_SHA256 extends AppCompatActivity {

    Global global;

    /*************************************************속성암호코드***************************************************/
    final static boolean DEBUG = true;
    private static final String TAG = "CpabeDemo";

    static String dir = "..\\co\\junwei\\dir";

    static String pubfile;
    static String mskfile;
    static String prvfile;

    static String inputfile ;
    static String encfile ;
    static String decfile ;

    FileOutputStream fos_pub_key;
    FileOutputStream fos_master_key;
    FileOutputStream fos_prv_key;

    FileOutputStream fos_inputfile;
    FileOutputStream fos_encfile;
    FileOutputStream fos_decfile;

//    static String inputfile = dir + "\\input.pdf";
//    static String encfile = dir + "\\input.pdf.cpabe";
//    static String decfile = dir + "\\input.pdf.new";

    static String[] attr = { "time:201911201233", "loc:123"};
    static String policy = "time:201911201233 loc:123 2of2";

    private GpsTracker gpsTracker;

    static double lat;
    static double lon;

    /***************************************************************************************************************/


    /*************************************************kisa OTP 생성**************************************************/
    long now; //현재시간을 mesc으로 구현함.
    Date date;//현재시간은 date 변수에 저장한다.
    SimpleDateFormat sdfNow;//시간을 나타내는 포맷
    String formateDate; //nowDate 변수에 값을 저장함.

    private TextView txtDateNow; //현재 시간을 나타내는 텍스트뷰
    private TextView txtOtpNum; // 생성시간을 보여주는 텍스트뷰
    private Button btnCreateOTP;
    private TextView otpDate1;
    private TextView pastOtp1;


    private String strNow_minus; //현재시간-1 int형 string으로 저장
    private String strNow;
    private String strNow_plus; //현재시간+1 int형 string으로 저장

    private String sel_minus; //6자리까지 끊긴 해쉬값 (현재시간-1)
    private String sel;
    private String sel_plus;
    /****************************************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kisa__sha256);

        global = new Global();

        txtOtpNum = findViewById(R.id.txtOptNum); //6자리 otp
        txtDateNow = findViewById(R.id.txtOtpTime); // otp 생성시간
        btnCreateOTP = findViewById(R.id.btnCreateOTP); //otp 생성버튼

        otpDate1 = findViewById(R.id.otpDate1); // 지난 otp를 보여주는 첫번째 칸
        pastOtp1 = findViewById(R.id.pastOtp1);

        btnCreateOTP.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int otpcreate_count = 0;
                String temp_date;
                String temp_otp;
                /*************************************************kisa OTP 생성**************************************************/
                initialSetting();
                createSeed();
                createTime();
                createHashOTP();
                otpcreate_count++;
                //randomOTP();
                txtDateNow.setText(formateDate);
                Toast.makeText(getApplicationContext(), "OTP를 생성했습니다.", Toast.LENGTH_LONG).show();
                txtOtpNum.setText(sel);

                temp_otp = sel;
                temp_date = formateDate;

                if (otpcreate_count > 1) {
                    pastOtp1.setText(temp_otp);
                    otpDate1.setText(temp_date);
                }


                /***************************************************************************************************************/

                /*************************************************속성암호코드***************************************************/
                gpsTracker = new GpsTracker(KISA_SHA256.this);

                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                lat = latitude;
                lon = longitude;

                encDec();
                /***************************************************************************************************************/
            }
        });
    }

    /*************************************************kisa OTP 생성*************************************************/
    public void initialSetting(){
        now = System.currentTimeMillis(); //현재시간을 mesc으로 구현함.
        date = new Date(now); //현재시간은 date 변수에 저장한다.
        sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //시간을 나타내는 포맷
        formateDate = sdfNow.format(date);//nowDate 변수에 값을 저장함.
        sel_minus = "";
        sel = "";
        sel_plus = "";
    }

    public void createSeed(){
        Random random = new Random();

        StringBuffer buffer = new StringBuffer();

        String[] a_array;

        String changeToAscii = ""; //랜덤값 ascii코드로 변환할 때 사용
        String cut_8_Ascii = ""; //랜덤값 ascii코드 8자리까지 자를 때 사용

        String chars[]=
                "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",");

        for (int i=0; i<8; i++) {
            buffer.append(chars[random.nextInt(chars.length)]);
        }

        //랜덤값(8자리 SEED 값 생성)
        System.out.println("랜덤값: " + buffer.toString());
        String mid = buffer.toString();

        //랜덤값 ascii 코드 변환
        System.out.print("랜덤값 ascii코드 변환: ");
        int j=0;
        byte[] byte_str = new byte[mid.length()];
        for(int i=0;i<mid.length();i++) {
            byte_str[i] = (byte)mid.charAt(i);
            System.out.print(byte_str[i]);
            changeToAscii += byte_str[i];
        }

        //랜덤값 ascii 코드 8자리까지 자르기
        a_array = changeToAscii.split("");
        System.out.print("\n랜덤값 ascii 8자리값: ");
        for(int i=0; i<8; i++) {
            System.out.print(a_array[i]);
            cut_8_Ascii += a_array[i];
        }
        global.setSeed(cut_8_Ascii);
        System.out.println("\n--------------------------------------------------------------------------------");
    }

    public void createTime(){

        strNow_plus = "";//현재시간+1 int형 string으로 저장
        strNow_minus = "";//현재시간-1 int형 string으로 저장

        //현재시간값 가져오기
        SimpleDateFormat sdf = new SimpleDateFormat("YYMMddHHmm"); //시간,분까지만 입력받음
        Calendar ch = Calendar.getInstance();
        strNow = sdf.format(ch.getTime()); //현재시간 string형
        strNow_plus += Integer.parseInt(strNow)+1; //add time으로 바꿀것!
        strNow_minus += Integer.parseInt(strNow)-1;

        global.setNowDate(strNow);

        System.out.println("현재시간= " + strNow_minus);
        System.out.println("현재시간= " + strNow);
        System.out.println("현재시간= " + strNow_plus);
        System.out.println("--------------------------------------------------------------------------------");

    }

    public void createHashOTP(){

        SecuritySHA SecuritySHA = new SecuritySHA();

        StringBuffer midd_minus = new StringBuffer(); //현재시간-1 이랑 랜덤값 더한 값
        StringBuffer midd = new StringBuffer(); //현재시간 이랑 랜덤값 더한 값
        StringBuffer midd_plus = new StringBuffer(); //현재시간+1 이랑 랜덤값 더한 값

        String[] hash_array_minus; //배열 hash_array 에 string rtn1값 의 한글자씩 배열에 저장됨
        String[] hash_array; //배열 hash_array2 에 string rtn1값 의 한글자씩 배열에 저장됨
        String[] hash_array_plus; //배열 hash_array3 에 string rtn1값 의 한글자씩 배열에 저장됨

        String[] sel_array_minus; //10진수로 변환된 해쉬값 배열로 변환해 저장(현재시간-1)
        String[] sel_array;
        String[] sel_array_plus;

        String dec_minus = ""; //해쉬값을 10진수로 변환한 결과 저장
        String dec = "";
        String dec_plus = "";

        int c_minus = 0;
        int c = 0;
        int c_plus = 0;

        //랜덤값 + 현재시간값 구하기(StringBuffer midd 에 값 저장)
        for(int i=0;i<8;i++) {
            c_minus = Integer.parseInt(global.getSeed()) + Integer.parseInt(strNow_minus);
        }
        midd_minus.append(c_minus);
        System.out.println("랜덤값 + (현재시간-1)값: " + midd_minus);


        for(int i=0;i<8;i++) {
            c = Integer.parseInt(global.getSeed()) + Integer.parseInt(strNow);
        }
        midd.append(c);
        System.out.println("랜덤값 + 현재시간값: " + midd);


        for(int i=0;i<8;i++) {
            c_plus = Integer.parseInt(global.getSeed()) + Integer.parseInt(strNow_plus);
        }
        midd_plus.append(c_plus);
        System.out.println("랜덤값 + (현재시간+1)값: " + midd_plus);

        System.out.println("--------------------------------------------------------------------------------");


        //SEED값으로 HASH값 생성
        String rtn_minus = SecuritySHA.encryptSHA256(midd_minus.toString());//rtn1 에 16진수 해쉬값이 string으로 입력
        System.out.println("(현재시간-1)해시값: " + rtn_minus);
        String rtn = SecuritySHA.encryptSHA256(midd.toString());//rtn2 에 16진수 해쉬값이 string으로 입력
        System.out.println("현재시간해시값: " + rtn);
        String rtn_plus = SecuritySHA.encryptSHA256(midd_plus.toString());//rtn3 에 16진수 해쉬값이 string으로 입력
        System.out.println("(현재시간+1)해시값: " + rtn_plus);
        System.out.println("--------------------------------------------------------------------------------");



        //HASH값 10진수 변환
        hash_array_minus = rtn_minus.split(""); //배열 hash_array 에 string rtn1값 의 한글자씩 배열에 저장됨
        for(int i=1;i<hash_array_minus.length;i++) {
            //System.out.println(Integer.parseInt(hash_array[i],16) + "");
            dec_minus += Integer.parseInt(hash_array_minus[i],16) + "";
            //dec += Integer.toString(Integer.parseInt(hash_array[i],16) );
        }
        System.out.println("(현재시간-1)10진수 값: " + dec_minus);

        hash_array = rtn.split(""); //배열 hash_array2 에 string rtn2값 의 한글자씩 배열에 저장됨
        for(int i=1;i<hash_array.length;i++) {
            //System.out.println(Integer.parseInt(hash_array[i],16) + "");
            dec += Integer.parseInt(hash_array[i],16) + "";
            //dec2 += Integer.toString(Integer.parseInt(hash_array2[i],16));
        }
        System.out.println("(현재시간)10진수 값: " + dec);

        hash_array_plus = rtn_plus.split(""); //배열 hash_array3 에 string rtn3값 의 한글자씩 배열에 저장됨
        for(int i=1;i<hash_array_minus.length;i++) {
            //System.out.println(Integer.parseInt(hash_array[i],16) + "");
            dec_plus += Integer.parseInt(hash_array_plus[i],16) + "";
            //dec3 += Integer.toString(Integer.parseInt(hash_array3[i],16));
        }
        System.out.println("(현재시간+1)10진수 값: " + dec_plus);

        System.out.println("--------------------------------------------------------------------------------");

        //10진수로 변환된 HASH값 6자리까지 끊기
        sel_array_minus = dec_minus.split("");
        for(int i=12;i<18;i++) {
            sel_minus += sel_array_minus[i];
        }
        System.out.println("(현재시간-1)최종otp값: " + sel_minus);

        sel_array = dec.split("");
        for(int i=12;i<18;i++) {
            sel += sel_array[i];
        }
        System.out.println("(현재시간)최종otp값: " + sel);

        sel_array_plus = dec_plus.split("");
        for(int i=12;i<18;i++) {
            sel_plus += sel_array_plus[i];
        }
        System.out.println("(현재시간+1)최종otp값: " + sel_plus);
    }

    /***************************************************************************************************************/


    /*************************************************속성암호코드***************************************************/

    public void encDec(){
        String attr_str;
        String time = global.getNowDate();
        String gps = "100";
        String seed = global.getSeed();

        try {
            fos_pub_key = openFileOutput("pub_key", Context.MODE_PRIVATE);
            fos_master_key = openFileOutput("master_key",Context.MODE_PRIVATE);
            fos_prv_key = openFileOutput("prv_key",Context.MODE_PRIVATE);

            fos_inputfile = openFileOutput("input.pdf",Context.MODE_PRIVATE);
            fos_encfile = openFileOutput("input.pdf.cpabe",Context.MODE_PRIVATE);
            fos_decfile = openFileOutput("input.pdf.new",Context.MODE_PRIVATE);


            File f_inputfile = new File(getFilesDir(),"input.pdf");
            if(!f_inputfile.exists()){
                f_inputfile.createNewFile();
            }
            inputfile = f_inputfile.getPath();

            File f_encfile = new File(getFilesDir(),"input.pdf.cpabe");
            if(!f_encfile.exists()){
                f_encfile.createNewFile();
            }
            encfile = f_encfile.getPath();

            File f_decfile = new File(getFilesDir(),"input.pdf.new");
            if(!f_decfile.exists()){
                f_decfile.createNewFile();
            }
            decfile = f_decfile.getPath();

        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pubfile = "/data/data/com.example.randomiotp/fos_pub_key";
        mskfile = "/data/data/com.example.randomiotp/fos_master_key";
        prvfile = "/data/data/com.example.randomiotp/fos_prv_key";

        double distanceMeter = Bswabe.distance(lat, lon, 37.501025, 127.037701, "meter");
        Log.d(TAG, "distanceMeter"+distanceMeter);
        Log.d(TAG, "lat"+lat);
        Log.d(TAG, "lon"+lon);
        String user_attr = "time:"+ time +" loc:" + gps;//속성이 가만히
        //String user_policy = "time:" + "201911201233" + " loc:" + "1000" + " 2of2";//정책이 바뀜-도어락
        String user_policy = "time:" + global.getNowDate() + " loc:" + "1000" + " 2of2";//정책이 바뀜-도어락

        try {
            Cpabe test = new Cpabe();
            println("//start to setup");
            test.setup(pubfile, mskfile);
            println("//end to setup");

            if (distanceMeter <= 1000) {
                gps = "1000";
                user_attr = "time:" + time + " loc:" + gps;//속성이 가만히
            }
            attr_str = user_attr;
            policy = user_policy;

            println("//start to keygen");
            test.keygen(pubfile, prvfile, mskfile, attr_str);
            println("//end to keygen");

            println("//start to enc");
            test.enc(pubfile, policy, inputfile, encfile);
            println("//end to enc");

            println("//start to dec");
            test.dec(pubfile, prvfile, encfile, decfile);
            println("//end to dec");


        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /* connect element of array with blank */
    public static String array2Str(String[] arr) {
        int len = arr.length;
        String str = arr[0];

        for (int i = 1; i < len; i++) {
            str += " ";
            str += arr[i];
        }

        return str;
    }

    private static void println(Object o) {
        if (DEBUG)
            System.out.println(o);
    }

    /***************************************************************************************************************/




    /************************************************************kisa OTP 생성************************************************************/
    // DEFAULT : JAVA = BIG_ENDIAN
    private static int ENDIAN = Common.BIG_ENDIAN;

    private static final int SHA256_DIGEST_BLOCKLEN = 64;
    private static final int SHA256_DIGEST_VALUELEN = 32;

    private static final int SHA256_K[] =
            {
                    0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1,
                    0x923f82a4, 0xab1c5ed5, 0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
                    0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174, 0xe49b69c1, 0xefbe4786,
                    0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
                    0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147,
                    0x06ca6351, 0x14292967, 0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
                    0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85, 0xa2bfe8a1, 0xa81a664b,
                    0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
                    0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a,
                    0x5b9cca4f, 0x682e6ff3, 0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
                    0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
            };


    private static final int ROTL_ULONG(int x, int n) {
        return (x << n) | Common.URShift(x, 32 - n);
    }

    private static final int ROTR_ULONG(int x, int n) {
        return Common.URShift(x, n) | (x << (32 - (n)));
    }

    private static final int ENDIAN_REVERSE_ULONG(int dwS) {
        return ( (ROTL_ULONG((dwS),  8) & 0x00ff00ff) | (ROTL_ULONG((dwS), 24) & 0xff00ff00) );
    }

    private static final void BIG_D2B(int D, byte[] B, int B_offset) {
        Common.int_to_byte_unit(B, B_offset, D, ENDIAN);
    }

    private static final int RR(int x, int n) { return ROTR_ULONG(x, n); }
    private static final int SS(int x, int n) { return Common.URShift(x, n); }

    private static final int Ch(int x, int y, int z) { return ((x & y) ^ ((~x) & z)); }
    private static final int Maj(int x, int y, int z) { return ((x & y) ^ (x & z) ^ (y & z)); }
    private static final int Sigma0(int x) { return (RR(x,  2) ^ RR(x, 13) ^ RR(x, 22)); }
    private static final int Sigma1(int x) { return (RR(x,  6) ^ RR(x, 11) ^ RR(x, 25)); }

    private static final int RHO0(int x) { return (RR(x,  7) ^ RR(x, 18) ^ SS(x,  3)); }
    private static final int RHO1(int x) { return (RR(x, 17) ^ RR(x, 19) ^ SS(x, 10)); }

    private static final int abcdefgh_a = 0;
    private static final int abcdefgh_b = 1;
    private static final int abcdefgh_c = 2;
    private static final int abcdefgh_d = 3;
    private static final int abcdefgh_e = 4;
    private static final int abcdefgh_f = 5;
    private static final int abcdefgh_g = 6;
    private static final int abcdefgh_h = 7;

    private static final void FF(int[] abcdefgh, int a, int b, int c, int d, int e, int f, int g, int h, int[] X, int j) {
        long T1;

        T1 = Common.intToUnsigned(abcdefgh[h]) + Common.intToUnsigned(Sigma1(abcdefgh[e])) + Common.intToUnsigned(Ch(abcdefgh[e], abcdefgh[f], abcdefgh[g])) + Common.intToUnsigned(SHA256_K[j]) + Common.intToUnsigned(X[j]);
        abcdefgh[d] += T1;
        abcdefgh[h] = (int)(T1 + Common.intToUnsigned(Sigma0(abcdefgh[a])) + Common.intToUnsigned(Maj(abcdefgh[a], abcdefgh[b], abcdefgh[c])));
    }

    private static final int GetData(byte[] x, int x_offset) {
        return Common.byte_to_int(x, x_offset, ENDIAN);
    }

    //*********************************************************************************************************************************
    // o SHA256_Transform() : 512 비트 단위 블록의 메시지를 입력 받아 연쇄변수를 갱신하는 압축 함수로써
    //	                      4 라운드(64 단계)로 구성되며 8개의 연쇄변수(a, b, c, d, e, f, g, h)를 사용
    // o 입력                               : Message               - 입력 메시지의 포인터 변수
    //	                      ChainVar              - 연쇄변수의 포인터 변수
    // o 출력                               :
    //*********************************************************************************************************************************
    private static void SHA256_Transform(byte[] Message, int[] ChainVar) {
        int abcdefgh[] = new int[8];
        int T1[] = new int[1];
        int X[] = new int[64];
        int j;

        for (j = 0; j < 16; j++)
            X[j] = GetData(Message, j*4);

        for (j = 16; j < 64; j++)
            X[j] = (int)(Common.intToUnsigned(RHO1(X[j - 2])) + Common.intToUnsigned(X[j - 7]) + Common.intToUnsigned(RHO0(X[j - 15])) + Common.intToUnsigned(X[j - 16]));

        abcdefgh[abcdefgh_a] = ChainVar[0];
        abcdefgh[abcdefgh_b] = ChainVar[1];
        abcdefgh[abcdefgh_c] = ChainVar[2];
        abcdefgh[abcdefgh_d] = ChainVar[3];
        abcdefgh[abcdefgh_e] = ChainVar[4];
        abcdefgh[abcdefgh_f] = ChainVar[5];
        abcdefgh[abcdefgh_g] = ChainVar[6];
        abcdefgh[abcdefgh_h] = ChainVar[7];

        for (j = 0; j < 64; j += 8)
        {
            FF(abcdefgh, abcdefgh_a, abcdefgh_b, abcdefgh_c, abcdefgh_d, abcdefgh_e, abcdefgh_f, abcdefgh_g, abcdefgh_h, X, j + 0);
            FF(abcdefgh, abcdefgh_h, abcdefgh_a, abcdefgh_b, abcdefgh_c, abcdefgh_d, abcdefgh_e, abcdefgh_f, abcdefgh_g, X, j + 1);
            FF(abcdefgh, abcdefgh_g, abcdefgh_h, abcdefgh_a, abcdefgh_b, abcdefgh_c, abcdefgh_d, abcdefgh_e, abcdefgh_f, X, j + 2);
            FF(abcdefgh, abcdefgh_f, abcdefgh_g, abcdefgh_h, abcdefgh_a, abcdefgh_b, abcdefgh_c, abcdefgh_d, abcdefgh_e, X, j + 3);
            FF(abcdefgh, abcdefgh_e, abcdefgh_f, abcdefgh_g, abcdefgh_h, abcdefgh_a, abcdefgh_b, abcdefgh_c, abcdefgh_d, X, j + 4);
            FF(abcdefgh, abcdefgh_d, abcdefgh_e, abcdefgh_f, abcdefgh_g, abcdefgh_h, abcdefgh_a, abcdefgh_b, abcdefgh_c, X, j + 5);
            FF(abcdefgh, abcdefgh_c, abcdefgh_d, abcdefgh_e, abcdefgh_f, abcdefgh_g, abcdefgh_h, abcdefgh_a, abcdefgh_b, X, j + 6);
            FF(abcdefgh, abcdefgh_b, abcdefgh_c, abcdefgh_d, abcdefgh_e, abcdefgh_f, abcdefgh_g, abcdefgh_h, abcdefgh_a, X, j + 7);
        }

        ChainVar[0] += abcdefgh[abcdefgh_a];
        ChainVar[1] += abcdefgh[abcdefgh_b];
        ChainVar[2] += abcdefgh[abcdefgh_c];
        ChainVar[3] += abcdefgh[abcdefgh_d];
        ChainVar[4] += abcdefgh[abcdefgh_e];
        ChainVar[5] += abcdefgh[abcdefgh_f];
        ChainVar[6] += abcdefgh[abcdefgh_g];
        ChainVar[7] += abcdefgh[abcdefgh_h];
    }

    /**
     @brief 연쇄변수와 길이변수를 초기화하는 함수
     @param Info : SHA256_Process 호출 시 사용되는 구조체
     */
    public static void SHA256_Init( SHA256_INFO Info ) {
        Info.uChainVar[0] = 0x6a09e667;
        Info.uChainVar[1] = 0xbb67ae85;
        Info.uChainVar[2] = 0x3c6ef372;
        Info.uChainVar[3] = 0xa54ff53a;
        Info.uChainVar[4] = 0x510e527f;
        Info.uChainVar[5] = 0x9b05688c;
        Info.uChainVar[6] = 0x1f83d9ab;
        Info.uChainVar[7] = 0x5be0cd19;

        Info.uHighLength = Info.uLowLength = 0;
    }

    /**
     @brief 연쇄변수와 길이변수를 초기화하는 함수
     @param Info : SHA256_Init 호출하여 초기화된 구조체(내부적으로 사용된다.)
     @param pszMessage : 사용자 입력 평문

     */
    public static void SHA256_Process( SHA256_INFO Info, byte[] pszMessage, int uDataLen ) {
        int pszMessage_offset;

        if((Info.uLowLength += (uDataLen << 3)) < 0) {
            Info.uHighLength++;
        }

        Info.uHighLength += Common.URShift(uDataLen,29);

        pszMessage_offset = 0;
        while (uDataLen >= SHA256_DIGEST_BLOCKLEN) {
            Common.arraycopy_offset(Info.szBuffer, 0, pszMessage, pszMessage_offset, SHA256_DIGEST_BLOCKLEN);
            SHA256_Transform(Info.szBuffer, Info.uChainVar);
            pszMessage_offset += SHA256_DIGEST_BLOCKLEN;
            uDataLen -= SHA256_DIGEST_BLOCKLEN;
        }

        Common.arraycopy_offset(Info.szBuffer, 0, pszMessage, pszMessage_offset, uDataLen);
    }

    /**
     @brief 메시지 덧붙이기와 길이 덧붙이기를 수행한 후 마지막 메시지 블록을 가지고 압축함수를 호출하는 함수
     @param Info : SHA256_Init 호출하여 초기화된 구조체(내부적으로 사용된다.)
     @param pszDigest : 암호문
     */
    public static void SHA256_Close( SHA256_INFO Info, byte[] pszDigest ) {
        int i, Index;

        Index = Common.URShift(Info.uLowLength, 3) % SHA256_DIGEST_BLOCKLEN;
        Info.szBuffer[Index++] = (byte)0x80;

        if (Index > SHA256_DIGEST_BLOCKLEN - 8) {
            Common.arrayinit_offset(Info.szBuffer, Index, (byte)0, SHA256_DIGEST_BLOCKLEN - Index);
            SHA256_Transform(Info.szBuffer, Info.uChainVar);
            Common.arrayinit(Info.szBuffer, (byte)0, SHA256_DIGEST_BLOCKLEN - 8);
        }
        else {
            Common.arrayinit_offset(Info.szBuffer, Index, (byte)0, SHA256_DIGEST_BLOCKLEN - Index - 8);
        }

        if(ENDIAN == Common.LITTLE_ENDIAN) {
            Info.uLowLength = ENDIAN_REVERSE_ULONG(Info.uLowLength);
            Info.uHighLength = ENDIAN_REVERSE_ULONG(Info.uHighLength);
        }

        Common.int_to_byte_unit(Info.szBuffer, ((int)(SHA256_DIGEST_BLOCKLEN / 4 - 2))*4, Info.uHighLength, ENDIAN);
        Common.int_to_byte_unit(Info.szBuffer, ((int)(SHA256_DIGEST_BLOCKLEN / 4 - 1))*4, Info.uLowLength, ENDIAN);

        SHA256_Transform(Info.szBuffer, Info.uChainVar);

        for (i = 0; i < SHA256_DIGEST_VALUELEN; i += 4)
            BIG_D2B((Info.uChainVar)[i / 4], pszDigest, i);
    }

    /**
     @brief 사용자 입력 평문을 한번에 처리
     @param pszMessage : 사용자 입력 평문
     @param pszDigest : 암호문
     @remarks 내부적으로 SHA256_Init, SHA256_Process, SHA256_Close를 호출한다.
     */
    public static void SHA256_Encrpyt( byte[] pszMessage, int uPlainTextLen, byte[] pszDigest ) {
        SHA256_INFO info = new SHA256_INFO();
        SHA256_Init( info );
        SHA256_Process( info, pszMessage, uPlainTextLen );
        SHA256_Close( info, pszDigest );
    }


    public static class SHA256_INFO {
        public int uChainVar[] = new int[SHA256_DIGEST_VALUELEN / 4];
        public int uHighLength;
        public int uLowLength;
        public byte szBuffer[] = new byte[SHA256_DIGEST_BLOCKLEN];
    }

    public static class Common {

        public static final int BIG_ENDIAN = 0;
        public static final int LITTLE_ENDIAN = 1;

        public static void arraycopy(byte[] dst, byte[] src, int length) {
            for(int i=0; i<length; i++) {
                dst[i] = src[i];
            }
        }

        public static void arraycopy_offset(byte[] dst, int dst_offset, byte[] src, int src_offset, int length) {
            for(int i=0; i<length; i++) {
                dst[dst_offset+i] = src[src_offset+i];
            }
        }

        public static void arrayinit(byte[] dst, byte value, int length) {
            for(int i=0; i<length; i++) {
                dst[i] = value;
            }
        }

        public static void arrayinit_offset(byte[] dst, int dst_offset, byte value, int length) {
            for(int i=0; i<length; i++) {
                dst[dst_offset+i] = value;
            }
        }

        public static void memcpy(int[] dst, byte[] src, int length, int ENDIAN) {
            int iLen = length / 4;
            for(int i=0; i<iLen; i++) {
                byte_to_int(dst, i, src, i*4, ENDIAN);
            }
        }

        public static void memcpy(int[] dst, int[] src, int src_offset, int length) {
            int iLen = length / 4 + ((length % 4 != 0)?1:0);
            for(int i=0; i<iLen; i++) {
                dst[i] = src[src_offset+i];
            }
        }

        public static void set_byte_for_int(int[] dst, int b_offset, byte value, int ENDIAN) {
            if(ENDIAN == BIG_ENDIAN) {
                int shift_value = (3-b_offset%4)*8;
                int mask_value =  0x0ff << shift_value;
                int mask_value2 = ~mask_value;
                int value2 = (value&0x0ff) << shift_value;
                dst[b_offset/4] = (dst[b_offset/4] & mask_value2) | (value2 & mask_value);
            } else {
                int shift_value = (b_offset%4)*8;
                int mask_value =  0x0ff << shift_value;
                int mask_value2 = ~mask_value;
                int value2 = (value&0x0ff) << shift_value;
                dst[b_offset/4] = (dst[b_offset/4] & mask_value2) | (value2 & mask_value);
            }
        }

        public static byte get_byte_for_int(int[] src, int b_offset, int ENDIAN) {
            if(ENDIAN == BIG_ENDIAN) {
                int shift_value = (3-b_offset%4)*8;
                int mask_value =  0x0ff << shift_value;
                int value = (src[b_offset/4] & mask_value) >> shift_value;
                return (byte)value;
            } else {
                int shift_value = (b_offset%4)*8;
                int mask_value =  0x0ff << shift_value;
                int value = (src[b_offset/4] & mask_value) >> shift_value;
                return (byte)value;
            }

        }

        public static byte[] get_bytes_for_ints(int[] src, int offset, int ENDIAN) {
            int iLen = src.length-offset;
            byte[] result = new byte[(iLen)*4];
            for(int i=0; i<iLen; i++) {
                int_to_byte(result, i*4, src, offset+i, ENDIAN);
            }

            return result;
        }

        public static void byte_to_int(int[] dst, int dst_offset, byte[] src, int src_offset, int ENDIAN) {
            if(ENDIAN == BIG_ENDIAN) {
                dst[dst_offset] = ((0x0ff&src[src_offset]) << 24) | ((0x0ff&src[src_offset+1]) << 16) | ((0x0ff&src[src_offset+2]) << 8) | ((0x0ff&src[src_offset+3]));
            } else {
                dst[dst_offset] = ((0x0ff&src[src_offset])) | ((0x0ff&src[src_offset+1]) << 8) | ((0x0ff&src[src_offset+2]) << 16) | ((0x0ff&src[src_offset+3]) << 24);
            }
        }

        public static int byte_to_int(byte[] src, int src_offset, int ENDIAN) {
            if(ENDIAN == BIG_ENDIAN) {
                return ((0x0ff&src[src_offset]) << 24) | ((0x0ff&src[src_offset+1]) << 16) | ((0x0ff&src[src_offset+2]) << 8) | ((0x0ff&src[src_offset+3]));
            } else {
                return ((0x0ff&src[src_offset])) | ((0x0ff&src[src_offset+1]) << 8) | ((0x0ff&src[src_offset+2]) << 16) | ((0x0ff&src[src_offset+3]) << 24);
            }
        }

        public static int byte_to_int_big_endian(byte[] src, int src_offset) {
            return ((0x0ff&src[src_offset]) << 24) | ((0x0ff&src[src_offset+1]) << 16) | ((0x0ff&src[src_offset+2]) << 8) | ((0x0ff&src[src_offset+3]));
        }

        public static void int_to_byte(byte[] dst, int dst_offset, int[] src, int src_offset, int ENDIAN) {
            int_to_byte_unit(dst, dst_offset, src[src_offset], ENDIAN);
        }

        public static void int_to_byte_unit(byte[] dst, int dst_offset, int src, int ENDIAN) {
            if(ENDIAN == BIG_ENDIAN) {
                dst[dst_offset] = (byte)((src>> 24) & 0x0ff);
                dst[dst_offset+1] = (byte)((src >> 16) & 0x0ff);
                dst[dst_offset+2] = (byte)((src >> 8) & 0x0ff);
                dst[dst_offset+3] = (byte)((src) & 0x0ff);
            } else {
                dst[dst_offset] = (byte)((src) & 0x0ff);
                dst[dst_offset+1] = (byte)((src >> 8) & 0x0ff);
                dst[dst_offset+2] = (byte)((src >> 16) & 0x0ff);
                dst[dst_offset+3] = (byte)((src >> 24) & 0x0ff);
            }

        }

        public static void int_to_byte_unit_big_endian(byte[] dst, int dst_offset, int src) {
            dst[dst_offset] = (byte)((src>> 24) & 0x0ff);
            dst[dst_offset+1] = (byte)((src >> 16) & 0x0ff);
            dst[dst_offset+2] = (byte)((src >> 8) & 0x0ff);
            dst[dst_offset+3] = (byte)((src) & 0x0ff);
        }

        public static int URShift(int x, int n) {
            if(n == 0)
                return x;
            if(n >= 32)
                return 0;
            int v = x >> n;
            int v_mask = ~(0x80000000 >> (n-1));
            return v & v_mask;
        }

        public static final long INT_RANGE_MAX = (long)Math.pow(2, 32);

        public static long intToUnsigned(int x) {
            if(x >= 0)
                return x;
            return x + INT_RANGE_MAX;
        }
    }

    //암호화 샘플코드
    public static class SecuritySHA{
        public String encryptSHA256(String str) {
            String sha = "";
            try {
                MessageDigest sh = MessageDigest.getInstance("SHA-256");
                sh.update(str.getBytes());
                byte byteData[] = sh.digest();
                StringBuffer sb = new StringBuffer();
                for(int i =0; i < byteData.length; i++){
                    sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
                }
                sha = sb.toString();
            }catch(NoSuchAlgorithmException e) {
                System.out.println("Encrpt Error - NoSuchAlgorithmException");
                sha = null;
            }
            return sha;
        }
    }
    /************************************************************************************************************************************/

    /*************************************************************속성암호코드*************************************************************/
    //attr_kevin 지움
    //attr_sara 지움
    /*************************************************************************************************************************************/


}
