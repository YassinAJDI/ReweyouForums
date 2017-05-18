package in.reweyou.reweyouforums;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyouforums.classes.ConnectionDetector;
import in.reweyou.reweyouforums.classes.HandleActivityResult;
import in.reweyou.reweyouforums.classes.RequestHandler;
import in.reweyou.reweyouforums.classes.UploadOptions;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.utils.Constants;

import static in.reweyou.reweyouforums.classes.HandleActivityResult.HANDLE_IMAGE;
import static in.reweyou.reweyouforums.classes.UploadOptions.PERMISSION_ALL_IMAGE;
import static in.reweyou.reweyouforums.classes.UploadOptions.PERMISSION_ALL_PROFILE_PIC;
import static in.reweyou.reweyouforums.utils.Constants.MY_PROFILE_EDIT_URL;
import static in.reweyou.reweyouforums.utils.Constants.tempnumber;

public class MyProfile extends AppCompatActivity implements View.OnClickListener {


    private static final String PACKAGE_URL_SCHEME = "package:";
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    UserSessionManager session;
    ArrayList<String> profilelist = new ArrayList<>();
    int SELECT_FILE = 1;
    private String i, tag, number, user, result, image, selectedImagePath;
    private TextView Name, Reports, Info, Readers, Location, Mobile;
    private ImageView profilepic;
    private EditText editTextHeadline, editLocation;
    private Button buttonEdit;
    private int length;
    private Button button;
    private UploadOptions uploadOptions;
    private TextView viewReport;
    private boolean dataloaded;
    private String name;
    private String number1;
    private String numberextra;
    private String TAG = MyProfile.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //initCollapsingToolbar();
        session = new UserSessionManager(MyProfile.this);
        cd = new ConnectionDetector(MyProfile.this);
        session = new UserSessionManager(getApplicationContext());

        Name = (TextView) findViewById(R.id.Name);
        Reports = (TextView) findViewById(R.id.Reports);
        Info = (TextView) findViewById(R.id.Info);
        Readers = (TextView) findViewById(R.id.Readers);
        Location = (TextView) findViewById(R.id.Location);
        Mobile = (TextView) findViewById(R.id.Mobile);
        viewReport = (TextView) findViewById(R.id.viewreport);


        viewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataloaded) {
                   /* Intent i = new Intent(MyProfile.this, SearchResultsActivity.class);
                    i.putExtra("position", 29);
                    i.putExtra("query", name);
                    i.putExtra("number", number1
                    );
                    startActivity(i);*/
                    Constants.tempnumber = number1;
                    Intent i = new Intent(MyProfile.this, YourReview.class);

                    startActivity(i);
                }
            }
        });

        button = (Button) findViewById(R.id.button);
        profilepic = (ImageView) findViewById(R.id.profilepic);

        if (getIntent() != null) {
            numberextra = getIntent().getStringExtra("number");
            if (numberextra != null) {
                findViewById(R.id.editphoto).setVisibility(View.GONE);
                findViewById(R.id.privatecon).setVisibility(View.GONE);
                getSupportActionBar().setTitle("Profile");
            }

        }
        //Progress bar
        tag = "Random";

        profilepic.setOnClickListener(this);
        button.setOnClickListener(this);
        Readers.setOnClickListener(this);
        uploadOptions = new UploadOptions(this);

        loadDataFromServer();
    }

    private void loadDataFromServer() {
        HashMap<String, String> hashMap = new HashMap<>();

        AndroidNetworking.post("https://www.reweyou.in/reviews/user_list.php")
                .setTag("tss")
                .addBodyParameter(hashMap)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray respons) {
                        try {
                            JSONObject response = respons.getJSONObject(0);
                            Log.d(TAG, "onResponse: " + response);
                            dataloaded = true;
                            name = response.getString("name");
                            number1 = response.getString("number");
                            Readers.setText(response.getString("total_likes"));
                            TextView topiccc = (TextView) findViewById(R.id.topics);
                            topiccc.setText(response.getString("topics"));
                            Reports.setText(response.getString("reviews"));


                            Name.setText(name);
                            if (!response.getString("info").isEmpty()) {
                                Info.setText(response.getString("info"));
                                Info.setPaintFlags(Info.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                            } else if (tempnumber.equals(session.getMobileNumber())) {
                                Info.setText(getResources().getString(R.string.emptyStatus));
                                Info.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                                Info.setPaintFlags(Info.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                            } else Info.setText("");

                            if (tempnumber.equals(session.getMobileNumber())) {
                                Info.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            editHeadline();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }

                            Glide.with(getApplicationContext()).load(response.getString("profilepic")).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.download).into(profilepic);
                            Mobile.setText(number1);
                            Location.setText(response.getString("location"));


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                        Toast.makeText(MyProfile.this, "Couldn't fetch", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onClick(View v) {
        if (Constants.tempnumber.equals(session.getMobileNumber())) {

            switch (v.getId()) {

                case R.id.profilepic:

                    if (uploadOptions.showprofilepicOptions())
                        showpicgallery();
                    break;
                case R.id.button:
                    try {
                        editHeadline();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
           /* case R.id.Readers:
                Bundle bundle = new Bundle();
                bundle.putString("myData", user);
                Intent in = new Intent(this, Readers.class);
                in.putExtras(bundle);
                startActivity(in);
                break;*/
        }
    }

    private void showpicgallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 2. pick image only
        intent.setType("image/*");
        // 3. start activity
        startActivityForResult(intent, SELECT_FILE);
    }

    private void button(final String i) {
        // final ProgressDialog loading = ProgressDialog.show(this, "Authenticating", "Please wait", false, false);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Constants.MY_PROFILE_URL_VERIFY_FOLLOW, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //if the server response is success
                if (response.equalsIgnoreCase("success")) {
                    //dismissing the progressbar
                    //     loading.show();

                    //Starting a new activity
                    button.setVisibility(View.VISIBLE);
                    button.setBackgroundColor(ContextCompat.getColor(MyProfile.this, R.color.red));
                    button.setTextColor(ContextCompat.getColor(MyProfile.this, R.color.transparent_bg));
                    button.setText("Unread");
                    button.setTag(1);
                } else {
                    //Displaying a toast if the otp entered is wrong
                    button.setVisibility(View.VISIBLE);
                    button.setBackgroundColor(ContextCompat.getColor(MyProfile.this, R.color.feedbackground));
                    button.setTextColor(ContextCompat.getColor(MyProfile.this, R.color.black));
                    button.setText("Read");
                    button.setTag(0);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Something went wrong, Try again", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", i);
                params.put("number", number);

                return params;
            }
        };
        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }


    public void uploadImage(final String encodedImage) {
        class UploadImage extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MyProfile.this, "Please wait...", "Adding your picture", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Log.d("result", s);
                if (s.trim().equals("Error")) {
                    Toast.makeText(MyProfile.this, "Couldn't set", Toast.LENGTH_LONG).show();
                } else if (s.trim().equals(Constants.AUTH_ERROR)) {
                    session.logoutUser();
                } else {
                    Toast.makeText(MyProfile.this, "Profile Picture updated", Toast.LENGTH_LONG).show();
                    session.setProfilePicture(s);
                    Glide.with(MyProfile.this).load(s).error(R.drawable.download).into(profilepic);
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                HashMap<String, String> param = new HashMap<String, String>();
                param.put("number", i);
                param.put("image", encodedImage);
                param.put("token", session.getKeyAuthToken());
                param.put("deviceid", session.getDeviceid());

                String result = rh.sendPostRequest(Constants.MY_PROFILE_UPLOAD_URL, param);
                return result;
            }
        }
        UploadImage u = new UploadImage();
        u.execute();
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && reqCode == SELECT_FILE && data != null) {
            int dataType = new HandleActivityResult().handleResult(reqCode, resCode, data);
            if (dataType == HANDLE_IMAGE) {
                selectedImagePath = new UploadOptions(MyProfile.this).getAbsolutePath(Uri.parse(data.getData().toString()));
                compressImage();
            }


        }
    }

    public void compressImage() {

        if (selectedImagePath != null) {
            Glide
                    .with(this)
                    .load(selectedImagePath)
                    .asBitmap()
                    .toBytes(Bitmap.CompressFormat.JPEG, 100)
                    .fitCenter()
                    .atMost()
                    .override(300, 300)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<byte[]>() {
                        @Override
                        public void onLoadStarted(Drawable ignore) {
                            // started async load
                        }

                        @Override
                        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> ignore) {
                            String encodedImage = Base64.encodeToString(resource, Base64.DEFAULT);
                            uploadImage(encodedImage);
                        }

                        @Override
                        public void onLoadFailed(Exception ex, Drawable ignore) {
                            Log.d("ex", ex.getMessage());
                        }
                    });
        } else uploadImage(null);

    }


    //This method would confirm the otp
    public void editHeadline() throws JSONException {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(MyProfile.this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_profile, null);

        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        buttonEdit = (Button) confirmDialog.findViewById(R.id.buttonConfirm);
        editTextHeadline = (EditText) confirmDialog.findViewById(R.id.editTextOtp);
        editLocation = (EditText) confirmDialog.findViewById(R.id.editTextLocation);

        if (Info.getText().toString().equals(getResources().getString(R.string.emptyStatus)))
            editTextHeadline.setHint(R.string.emptyStatusHint);
        else editTextHeadline.setText(Info.getText());
        editTextHeadline.setSelection(editTextHeadline.getText().length());

        editLocation.setText(Location.getText());
        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(MyProfile.this);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Displaying the alert dialog
        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Hiding the alert dialog
                alertDialog.dismiss();
                //Displaying a progressbar
                Log.d("b", "onClick: reached");
                Log.d("b", "onClick: reached" + editTextHeadline.getText().toString());
                Log.d("b", "onClick: reached" + Info.getText().toString());

                Log.d("b", "onClick: reached1w1w1");

                final ProgressDialog loading = ProgressDialog.show(MyProfile.this, "Updating", "Please wait", false, false);
                //Getting the user entered otp from edittext
                final String headline = editTextHeadline.getText().toString().trim();
                final String location = editLocation.getText().toString().trim();
                //Creating an string request
                StringRequest stringRequest = new StringRequest(Request.Method.POST, MY_PROFILE_EDIT_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //if the server response is success
                                if (response.equalsIgnoreCase("success")) {
                                    //dismissing the progressbar
                                    loading.dismiss();

                                    if (!headline.isEmpty()) {
                                        Info.setText(headline);
                                        Info.setTextColor(getResources().getColor(android.R.color.black));
                                    } else {
                                        Info.setText(getResources().getString(R.string.emptyStatus));
                                        Info.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                                        Info.setPaintFlags(Info.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                                    }
                                    Toast.makeText(MyProfile.this, "Profile Updated!", Toast.LENGTH_LONG).show();
                                    //Starting a new activity
                                } else {
                                    //Displaying a toast if the otp entered is wrong
                                    loading.dismiss();
                                    Toast.makeText(MyProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();

                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                alertDialog.dismiss();
                                loading.dismiss();

                                Toast.makeText(MyProfile.this, "Try again later", Toast.LENGTH_LONG).show();
                                String body;
                                //get status code here
                                String statusCode = String.valueOf(error.networkResponse.statusCode);
                                Log.d("bodsssy", statusCode);

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        //Adding the parameters otp and username
                        params.put("number", i);
                        params.put("info", headline);
                        params.put("location", location);
                        params.put("token", session.getKeyAuthToken());
                        params.put("deviceid", session.getDeviceid());
                        // params.put("number",number);
                        return params;
                    }
                };
                //Adding the request to the queue
                RequestQueue requestQueue = Volley.newRequestQueue(MyProfile.this);
                requestQueue.add(stringRequest);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        if (getIntent() != null)
            if (getIntent().getStringExtra("number") != null)
                item.setVisible(false);

        // Retrieve the SearchView and plug it into SearchManager
        // Associate searchable configuration with the SearchView
       /* SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchable

        (
                searchManager.getSearchableInfo(getComponentName()));
*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_settings:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...

                try {
                    editHeadline();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_ALL_PROFILE_PIC:

                String permission4 = permissions[0];
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission

                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(MyProfile.this, permission4);
                    if (!showRationale) {
                        showPermissionDeniedDialog();
                    } else
                        showPermissionRequiredDialog(permission4);


                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showpicgallery();
                }
                break;

        }
    }

    private void showPermissionRequiredDialog(final String permission) {
        AlertDialogBox alertDialogBox = new AlertDialogBox(MyProfile.this, "Permission Required", getResources().getString(R.string.permission_required_image), "grant", "deny") {
            @Override
            public void onNegativeButtonClick(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                String[] p = {permission};
                ActivityCompat.requestPermissions(MyProfile.this, p, PERMISSION_ALL_IMAGE);

            }
        };
        alertDialogBox.setCancellable(true);
        alertDialogBox.show();
    }

    private void showPermissionDeniedDialog() {
        AlertDialogBox alertDialogBox = new AlertDialogBox(MyProfile.this, "Permission Denied", getResources().getString(R.string.permission_denied_image), "settings", "okay") {
            @Override
            public void onNegativeButtonClick(DialogInterface dialog) {
                dialog.dismiss();

            }

            @Override
            public void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                startAppSettings();

            }
        };
        alertDialogBox.setCancellable(true);
        alertDialogBox.show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class JSONTask extends AsyncTask<String, String, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("number", params[0]);
            data.put("token", session.getKeyAuthToken());
            data.put("deviceid", session.getDeviceid());
            try {
                URL url = new URL("https://www.reweyou.in/reviews/user_list.php");
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(rh.getPostDataString(data));
                wr.flush();


                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();

                JSONArray parentArray = new JSONArray(finalJson);
                StringBuffer finalBufferedData = new StringBuffer();

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    profilelist.add(finalObject.getString("name"));
                    profilelist.add(finalObject.getString("reviews"));
                    profilelist.add(finalObject.getString("profilepic"));
                    profilelist.add(finalObject.getString("info"));
                    profilelist.add(finalObject.getString("number"));
                    profilelist.add(finalObject.getString("location"));
                    profilelist.add(finalObject.getString("topics"));
                    //  profilelist.add(finalObject.getString("readers"));
                }

                return profilelist;

                //return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> result) {

            super.onPostExecute(result);
            if (result != null) {
                dataloaded = true;
                name = result.get(0);
                number1 = result.get(4);
                Name.setText(result.get(0));
                Reports.setText(result.get(1));
                if (!result.get(3).isEmpty()) {
                    Info.setText(result.get(3));
                    Info.setPaintFlags(Info.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                } else {
                    Info.setText(getResources().getString(R.string.emptyStatus));
                    Info.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    Info.setPaintFlags(Info.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                }

                Info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            editHeadline();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // imageLoader.displayImage(result.get(2), profilepic, option);

                Glide.with(getApplicationContext()).load(result.get(2)).error(R.drawable.download).into(profilepic);
                user = result.get(4);
                Mobile.setText(result.get(4));
                Location.setText(result.get(5));
                Readers.setText(result.get(6));

                //    progressBar.setVisibility(View.GONE);
                //need to set data to the list
            }
        }
    }

}