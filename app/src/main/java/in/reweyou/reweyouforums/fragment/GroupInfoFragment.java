package in.reweyou.reweyouforums.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import in.reweyou.reweyouforums.EditActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.utils.Utils;

/**
 * Created by master on 24/2/17.
 */

public class GroupInfoFragment extends Fragment {


    private static final String TAG = GroupInfoFragment.class.getName();
    private Activity mContext;
    private String groupid;
    private boolean isfollowed;
    private String groupdes = "";
    private String grouprules = "";
    private String adminuid = "";
    private ImageView shineeffect;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_group_info, container, false);

        TextView edit = (TextView) layout.findViewById(R.id.edit);

        final UserSessionManager userSessionManager = new UserSessionManager(mContext);
        final TextView btnfollow = (TextView) layout.findViewById(R.id.btn_follow);
        final ImageView img = (ImageView) layout.findViewById(R.id.image);
        final TextView groupname = (TextView) layout.findViewById(R.id.groupname);
        final TextView textrules = (TextView) layout.findViewById(R.id.textrules);
        TextView shortdes = (TextView) layout.findViewById(R.id.shortdescription);
        final TextView description = (TextView) layout.findViewById(R.id.description);
        final TextView members = (TextView) layout.findViewById(R.id.members);
        TextView threads = (TextView) layout.findViewById(R.id.threads);
        shineeffect = (ImageView) layout.findViewById(R.id.img_shine);
        final ProgressBar pd = (ProgressBar) layout.findViewById(R.id.pd);
        try {
            groupname.setText(getArguments().getString("groupname"));
            members.setText(getArguments().getString("members"));
            groupid = getArguments().getString("groupid");
            isfollowed = getArguments().getBoolean("follow");
            shortdes.setText(getArguments().getString("description"));
            groupdes = getArguments().getString("description");
            grouprules = getArguments().getString("rules");
            description.setText(getArguments().getString("rules"));
            adminuid = getArguments().getString("admin");
            if (getArguments().getString("rules").isEmpty()) {
                if (userSessionManager.getUID().equals(adminuid)) {

                    description.setText("*Edit to update rules*");
                } else {
                    textrules.setVisibility(View.GONE);
                    description.setVisibility(View.GONE);
                }
            }
            if (adminuid.equals(userSessionManager.getUID())) {
                btnfollow.setVisibility(View.GONE);
            } else if (isfollowed) {
                btnfollow.setText("Leave");
                btnfollow.setTextColor(mContext.getResources().getColor(R.color.main_background_pink));
                btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_border_pink));

            } else {
                btnfollow.setText("Join");
                btnfollow.setTextColor(mContext.getResources().getColor(R.color.white));
                btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_solid_pink));
            }
            Glide.with(mContext).load(getArguments().getString("image")).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img);
            btnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnfollow.setVisibility(View.INVISIBLE);
                    pd.setVisibility(View.VISIBLE);

                    AndroidNetworking.post("https://www.reweyou.in/google/follow_groups.php")
                            .addBodyParameter("groupid", groupid)
                            .addBodyParameter("groupname", getArguments().getString("groupname"))
                            .addBodyParameter("uid", userSessionManager.getUID())
                            .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                            .setTag("uploadpost")
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "onResponse: " + response);
                                    if (response.equals("Followed")) {
                                        btnfollow.setText("Leave");
                                        btnfollow.setTextColor(mContext.getResources().getColor(R.color.main_background_pink));
                                        btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_border_pink));

                                        btnfollow.setVisibility(View.VISIBLE);
                                        pd.setVisibility(View.GONE);
                                        Toast.makeText(mContext, "You are now following '" + getArguments().getString("groupname") + "'", Toast.LENGTH_SHORT).show();
                                        mContext.setResult(Activity.RESULT_OK);
                                    } else if (response.equals("Unfollowed")) {
                                        btnfollow.setText("Join");
                                        btnfollow.setTextColor(mContext.getResources().getColor(R.color.white));
                                        btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_solid_pink));
                                        btnfollow.setVisibility(View.VISIBLE);
                                        pd.setVisibility(View.GONE);
                                        mContext.setResult(Activity.RESULT_OK);
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.d(TAG, "onError: " + anError);
                                    Toast.makeText(mContext, "Connection problem", Toast.LENGTH_SHORT).show();
                                    btnfollow.setVisibility(View.VISIBLE);
                                    pd.setVisibility(View.GONE);
                                }
                            });

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, EditActivity.class);
                i.putExtra("description", groupdes);
                i.putExtra("image", getArguments().getString("image"));
                i.putExtra("rules", grouprules);
                i.putExtra("groupid", groupid);
                startActivityForResult(i, Utils.REQ_CODE_EDIT_GROUP_ACTIVITY);
            }
        });

        if (!isfollowed)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    shineeffect.animate().translationXBy(Utils.convertpxFromDp(28 + 90 + 28)).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(800).start();
                }
            }, 700);
        return layout;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            mContext = (Activity) context;
        else throw new IllegalArgumentException("Context should be an instance of Activity");
    }

    @Override
    public void onDestroy() {
        mContext = null;
        super.onDestroy();

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isAdded()) {


        }
    }


    public void refreshDetails() {
        Toast.makeText(mContext, "Details code to be updated soon", Toast.LENGTH_SHORT).show();
    }
}