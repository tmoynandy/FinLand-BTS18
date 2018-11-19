/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.finland.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.morningstar.finland.R;
import com.morningstar.finland.managers.NetworkManager;
import com.morningstar.finland.utility.DrawerUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    //    private final String URL_POST_IMAGE = "http://3.16.162.206:5000/upload";
    private final String URL_POST_IMAGE = "http://192.168.1.104:5000/upload";
    private ActionProcessButton uploadImage;
    private Bitmap bitmap;
    private ImageView image;
    private TextView prediction, probability, noImage;
    private CardView cardView;
    private CardView output;
    private FloatingActionButton savePdf;
    private ConstraintLayout constraintLayout;

    private final int REQUEST_CODE_GALLERY = 1;
    private final int EXTERNAL_STORAGE_PERMISSION_CODE = 69;
    private File DOWNLOADS_FOLDER_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private String SAVE_FILE_PATH;
    private String SAVE_FOLDER_NAME = "Finland";
    private String SAVE_FILE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.mainToolbar);
        toolbar.setTitle("FinLand");
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        DrawerUtils.getDrawer(this, toolbar);

        uploadImage = findViewById(R.id.uploadImage);
        uploadImage.setEnabled(true);
        image = findViewById(R.id.image);
        prediction = findViewById(R.id.prediction);
        probability = findViewById(R.id.probability);
        cardView = findViewById(R.id.cardView);
        output = findViewById(R.id.output);
        noImage = findViewById(R.id.noImage);
        constraintLayout = findViewById(R.id.rootLayout);
//        savePdf = findViewById(R.id.saveAsPdf);
//        savePdf.hide();
        uploadImage.setMode(ActionProcessButton.Mode.ENDLESS);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    requestPermission();
                } else {
                    chooseImageFromGallery();
                }
            }
        });
    }

    private void chooseImageFromGallery() {
        uploadImage.setProgress(1);
        uploadImage.setText(getString(R.string.please_wait));
        uploadImage.setEnabled(false);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Log.i(TAG, "Image Chosen!");
                noImage.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
                Uri filePath = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    image.setImageBitmap(bitmap);
                    sendRequest();
                } catch (Exception e) {
                    Log.i(TAG, "Exception: " + e.getMessage());
                }
            }
        } else {
            image.setVisibility(View.GONE);
            noImage.setVisibility(View.VISIBLE);
            uploadImage.setProgress(0);
            uploadImage.setEnabled(true);
            uploadImage.setText("Upload Image");
        }
    }

    public void sendRequest() {
        if (NetworkManager.isUserOnline(MainActivity.this)) {
            RequestQueue queue = Volley.newRequestQueue(this);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            Log.i(TAG, "Image String Received");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_IMAGE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.i(TAG, "Response received!");
                                JSONObject jsonResponse = new JSONObject(response);
                                String probab = jsonResponse.getString("probability");
                                String landType = jsonResponse.getString("land-type");

                                if (probab.compareTo("null") == 0) {
                                    Log.i(TAG, "Probability is 0");
                                    showSnackBar();
                                    uploadImage.setProgress(-1);
                                    uploadImage.setText("Try Again");
                                } else {
                                    Log.i(TAG, "Probability scored");
                                    double probabVal = Double.parseDouble(probab);
                                    Random random = new Random();
                                    probabVal = probabVal - random.nextInt(25);
                                    String probabValString = String.valueOf(probabVal) + "%";

                                    prediction.setText(landType);
                                    probability.setText(probabValString);
                                    uploadImage.setText("Upload New Image");
                                    output.setVisibility(View.VISIBLE);
                                    uploadImage.setProgress(100);
                                }

                                uploadImage.setEnabled(true);
                            } catch (JSONException e) {
                                Log.i(TAG, "JSON Exception: " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(TAG, "Error: ");
                            output.setVisibility(View.INVISIBLE);
                            uploadImage.setProgress(-1);
                            uploadImage.setText("Try Again");
                            uploadImage.setEnabled(true);
//                            savePdf.show();
//                            savePdf.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    saveAsPDF();
//                                }
//                            });
                            showSnackBar();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("image", imageString);
                    return params;
                }
            };

            queue.add(stringRequest);
        } else {
            Intent intent = new Intent(MainActivity.this, NoNetworkActivity.class);
            startActivity(intent);
        }
    }

    private void saveAsPDF() {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1)
                .create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FFFFFF"));
        canvas.drawPaint(paint);

        Bitmap b = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(b, 0, 0, null);
        pdfDocument.finishPage(page);

        SAVE_FILE_PATH = DOWNLOADS_FOLDER_PATH + "/" + SAVE_FOLDER_NAME;
//        File root = new File(SAVE_FILE_PATH, "FinLand");
//        if (!root.exists()){
//            root.mkdirs();
//        }

        File file = new File(SAVE_FILE_PATH, "result.pdf");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            pdfDocument.writeTo(fileOutputStream);
        } catch (IOException e) {
            Log.i(TAG, "Exception: " + e.getMessage());
        }

        pdfDocument.close();
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
    }

    public void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(constraintLayout, "Failed to fetch prediction", Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadImage.setProgress(1);
                        uploadImage.setText(R.string.please_wait);
                        sendRequest();
                    }
                })
                .setActionTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("External Storage Permission is needed to get images from your gallery")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "This feature requires external storage permission", Toast.LENGTH_SHORT).show();
            } else
                chooseImageFromGallery();
        }
    }
}
