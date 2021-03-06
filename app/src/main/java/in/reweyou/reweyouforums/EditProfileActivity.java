package in.reweyou.reweyouforums;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import in.reweyou.reweyouforums.classes.UserSessionManager;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = EditProfileActivity.class.getName();

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private UserSessionManager userSessionManager;
    private ImageView image;
    private Button create;
    private ImagePicker imagePicker;
    private String encodedImage = "";
    private String tempshortinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        userSessionManager = new UserSessionManager(this);

        try {
            getSupportActionBar().setTitle("Edit Profile");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final EditText username = (EditText) findViewById(R.id.message);
        final EditText shortinfo = (EditText) findViewById(R.id.shortinfo);
        image = (ImageView) findViewById(R.id.image);
        final ProgressBar pd = (ProgressBar) findViewById(R.id.progressBar);
        shortinfo.setText(userSessionManager.getShortinfo());
        create = (Button) findViewById(R.id.create);
        create.setEnabled(false);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    updateCreateTextUI(true);
                } else {
                    updateCreateTextUI(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create.setVisibility(View.INVISIBLE);
                pd.setVisibility(View.VISIBLE);
                tempshortinfo = "I am happy to be a part of Reweyou";
                if (shortinfo.getText().toString().length() > 0)
                    tempshortinfo = shortinfo.getText().toString();

                HashMap<String, String> hashMap = new HashMap<String, String>();
                if (!encodedImage.isEmpty())
                    hashMap.put("image", encodedImage);
                hashMap.put("username", username.getText().toString().trim());
                hashMap.put("aboutme", tempshortinfo);
                hashMap.put("uid", userSessionManager.getUID());
                hashMap.put("authtoken", userSessionManager.getAuthToken());


                AndroidNetworking.post("https://www.reweyou.in/google/edit_profile.php")
                        .addBodyParameter(hashMap)
                        .setTag("editgroup")
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "onResponse: " + response);
                                if (response.contains("Updated")) {

                                    /*userSessionManager.setShortInfo(tempshortinfo);
                                    userSessionManager.setUsername(username.getText().toString().trim());*/
                                    pd.setVisibility(View.GONE);
                                    Toast.makeText(EditProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.replace("Updated", ""));
                                        userSessionManager.setUsername(jsonObject.getString("username"));
                                        userSessionManager.setShortInfo(jsonObject.getString("aboutme"));
                                        userSessionManager.setProfilePicture(jsonObject.getString("imageurl"));
                                        setResult(RESULT_OK);
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(EditProfileActivity.this, "something went wrong!", Toast.LENGTH_SHORT).show();

                                    create.setVisibility(View.VISIBLE);
                                    pd.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d(TAG, "onError: " + anError);
                                Toast.makeText(EditProfileActivity.this, "couldn't connect", Toast.LENGTH_SHORT).show();

                                create.setVisibility(View.VISIBLE);
                                pd.setVisibility(View.GONE);
                            }
                        });


            }
        });


        LinearLayout container = (LinearLayout) findViewById(R.id.container);

        username.setText(userSessionManager.getUsername());
        username.setSelection(userSessionManager.getUsername().length());
       /* grouprul.setText(grouprules);
        grouprul.setSelection(grouprules.length());*/

        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        Glide.with(this).load(userSessionManager.getProfilePicture()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkStoragePermission();

                } else showPickImage();
            }
        });
    }

    private void updateCreateTextUI(boolean b) {
        if (b) {
            create.setEnabled(true);
            create.setTextColor(this.getResources().getColor(R.color.main_background_pink));
            create.setBackground(this.getResources().getDrawable(R.drawable.border_pink));
        } else {
            create.setEnabled(false);
            create.setTextColor(this.getResources().getColor(R.color.grey_create));
            create.setBackground(this.getResources().getDrawable(R.drawable.border_grey));
        }
    }

    private void checkStoragePermission() {
        Dexter.withActivity(EditProfileActivity.this)
                .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        showPickImage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(EditProfileActivity.this, "Storage Permission denied by user", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onPermissionGranted: " + response.isPermanentlyDenied());

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }

    public void showPickImage() {
        imagePicker = new ImagePicker(this);
        imagePicker.setImagePickerCallback(new ImagePickerCallback() {
                                               @Override
                                               public void onImagesChosen(List<ChosenImage> images) {

                                                   onImageChoosenbyUser(images);

                                               }

                                               @Override
                                               public void onError(String message) {
                                                   // Do error handling
                                                   Log.e(TAG, "onError: " + message);
                                               }
                                           }

        );

        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(false);
        imagePicker.pickImage();

    }

    private void onImageChoosenbyUser(List<ChosenImage> images) {
        if (images != null) {

            try {

                Log.d(TAG, "onImagesChosen: size" + images.size());
                if (images.size() > 0) {
                    Log.d(TAG, "onImagesChosen: path" + images.get(0).getOriginalPath() + "  %%%   " + images.get(0).getThumbnailSmallPath());

                    if (images.get(0).getOriginalPath() != null) {
                        Log.d(TAG, "onImagesChosen: " + images.get(0).getFileExtensionFromMimeTypeWithoutDot());
                        if (images.get(0).getFileExtensionFromMimeTypeWithoutDot().equals("gif")) {
                            // handleGif(images.get(0).getOriginalPath());
                            Toast.makeText(this, "Only image can be uploaded", Toast.LENGTH_SHORT).show();
                        } else {
                            startImageCropActivity(Uri.parse(images.get(0).getQueryUri()));
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong. ErrorCode: 19", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startImageCropActivity(Uri data) {
        CropImage.activity(data)
                .setActivityTitle("Crop Image")
                .setBackgroundColor(Color.parseColor("#90000000"))
                .setMinCropResultSize(200, 200)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1, 1)
                .setFixAspectRatio(true)
                .setBorderCornerColor(getResources().getColor(R.color.colorPrimaryDark))
                .setBorderLineColor(getResources().getColor(R.color.colorPrimary))
                .setGuidelinesColor(getResources().getColor(R.color.divider))
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("reached", "activigty");
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                handleImage(result.getUri().toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                imagePicker.submit(data);
            }
        }


    }

    private void handleImage(String s) {
        Glide.with(this).load(s).into(image);
        Glide.with(this).load(s).asBitmap().toBytes(Bitmap.CompressFormat.JPEG, 90).override(200, 200).into(new SimpleTarget<byte[]>() {
            @Override
            public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                String encoded = Base64.encodeToString(resource, Base64.DEFAULT);
                encodedImage = encoded;
            }
        });
    }


}
