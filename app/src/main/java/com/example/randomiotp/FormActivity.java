package com.example.randomiotp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormActivity extends AppCompatActivity {

    private EditText mtxtHostEmail;
    private EditText mtxtHostPhone;
    private EditText mtxtGuestEmail;
    private EditText mtxtGuestPhone;
    private EditText mtxtPlaceAddress;
    private EditText mtxtPeopleNum;

    private TextView textView_StartDate;
    private DatePickerDialog.OnDateSetListener callbackMethod1;
    private TextView textView_StartTime;
    private TimePickerDialog.OnTimeSetListener callbackMethod2;
    private TextView textView_EndDate;
    private DatePickerDialog.OnDateSetListener callbackMethod3;
    private TextView textView_EndTime;
    private TimePickerDialog.OnTimeSetListener callbackMethod4;

    private ImageView imageView_Image;
    private Uri mCaptureUri;
    public String mPhotoPath;
    public static final int REQUEST_IMAGE_CAPTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        mtxtHostEmail = findViewById(R.id.txtHostEmail);
        mtxtHostPhone = findViewById(R.id.txtHostPhone);
        mtxtGuestEmail = findViewById(R.id.txtGuestEmail);
        mtxtGuestPhone = findViewById(R.id.txtGuestPhone);
        mtxtPlaceAddress = findViewById(R.id.txtPlaceAddress);
        mtxtPeopleNum = findViewById(R.id.txtPeopleNum);

        this.InitializeView1();
        this.InitializeListener1();

        this.InitializeView2();
        this.InitializeListener2();

        this.InitializeView3();
        this.InitializeListener3();

        this.InitializeView4();
        this.InitializeListener4();

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 0);

        checkPermission();

        imageView_Image = findViewById(R.id.imageView);
        findViewById(R.id.btnPicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    // 시작날짜 선택
    public void InitializeView1() {
        textView_StartDate = findViewById(R.id.textView_startdate);
    }

    public void InitializeListener1() {
        callbackMethod1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                textView_StartDate.setText(year + "년 " + (month+1) + "월" + day +"일");
            }
        };
    }

    public void OnClickHandler1(View view) {
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod1, 2020 ,3 ,24);
        dialog.show();
    }

    // 시작시간 선택
    public void InitializeView2() {
        textView_StartTime = findViewById(R.id.textView_starttime);
    }

    public void InitializeListener2() {
        callbackMethod2 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                textView_StartTime.setText(hour + "시" + minute + "분");
            }
        };
    }

    public void OnClickHandler2(View view) {
        TimePickerDialog dialog = new TimePickerDialog(this, callbackMethod2, 8, 10, true);
        dialog.show();
    }

    // 종료날짜 선택
    public void InitializeView3() {
        textView_EndDate = findViewById(R.id.textView_enddate);
    }

    public void InitializeListener3() {
        callbackMethod3 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                textView_EndDate.setText(year + "년 " + (month+1) + "월" + day +"일");
            }
        };
    }

    public void OnClickHandler3(View view) {
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod3, 2020 ,3 ,24);
        dialog.show();
    }

    // 종료시간 선택
    public void InitializeView4() {
        textView_EndTime = findViewById(R.id.textView_endtime);
    }

    public void InitializeListener4() {
        callbackMethod4 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                textView_EndTime.setText(hour + "시" + minute + "분");
            }
        };
    }

    public void OnClickHandler4(View view) {
        TimePickerDialog dialog = new TimePickerDialog(this, callbackMethod4, 8, 10, true);
        dialog.show();
    }

    private void takePicture() {

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mCaptureUri = Uri.fromFile( getOutPutMediaFile() );
        } else {
            mCaptureUri = FileProvider.getUriForFile(this,
                    "com.example.randomiotp", getOutPutMediaFile());
        }

        i.putExtra(MediaStore.EXTRA_OUTPUT, mCaptureUri);

        //내가 원하는 액티비티로 이동하고, 그 액티비티가 종료되면 (finish되면)
        //다시금 나의 액티비티의 onActivityResult() 메서드가 호출되는 구조이다.
        //내가 어떤 데이터를 받고 싶을때 상대 액티비티를 호출해주고 그 액티비티에서
        //호출한 나의 액티비티로 데이터를 넘겨주는 구조이다. 이때 호출되는 메서드가
        //onActivityResult() 메서드 이다.
        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);

    } // end of takePicure

    private File getOutPutMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Profile");
        if(!mediaStorageDir.exists()) {
            if(!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        mPhotoPath = file.getAbsolutePath();

        return file;
    }

    private void sendPicture() {
        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath);
        Bitmap resizedBmp = getResizedBitmap(bitmap, 4, 100, 100);

        bitmap.recycle();

        //사진이 캡쳐되서 들어오면 뒤집어져 있다. 이애를 다시 원상복구 시킨다.
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(mPhotoPath);
        } catch(Exception e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;
        if(exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientToDegree(exifOrientation);
        } else {
            exifDegree = 0;
        }
        Bitmap rotatedBmp = roate(resizedBmp, exifDegree);
        imageView_Image.setImageBitmap( rotatedBmp );
    } // end of sendpicture

    private int exifOrientToDegree(int exifOrientation) {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap roate(Bitmap bmp, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                matrix, true);
    }

    //비트맵의 사이즈를 줄여준다.
    public static Bitmap getResizedBitmap(Bitmap srcBmp, int size, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap resized = Bitmap.createScaledBitmap(srcBmp, width, height, true);
        return resized;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //카메라로부터 오는 데이터를 취득한다.
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_IMAGE_CAPTURE) {
                sendPicture();
            }
        }
    }

    private void checkPermission() {
        // Self 권한 체크
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            // 권한동의 체크
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
            ) {

            } else {
                // 권한동의 팝업 표시 요청
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA}, 1111);
            }
        }
    } // End checkPermission
}
