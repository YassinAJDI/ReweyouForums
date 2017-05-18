package in.reweyou.reweyouforums.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.GroupActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.model.GroupModel;
import in.reweyou.reweyouforums.utils.Utils;

/**
 * Created by master on 1/5/17.
 */

public class YourGroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<GroupModel> messagelist;

    public YourGroupsAdapter(Context context) {
        this.context = context;
        this.messagelist = new ArrayList<>();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new YourGroupsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore_your_groups, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        YourGroupsViewHolder forumViewHolder = (YourGroupsViewHolder) holder;
        Glide.with(context).load(messagelist.get(position).getImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(forumViewHolder.backgroundImage);
        forumViewHolder.groupName.setText(messagelist.get(position).getGroupname());
        forumViewHolder.members.setText(messagelist.get(position).getMembers());
        forumViewHolder.threads.setText(messagelist.get(position).getThreads());

    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<GroupModel> list) {
        messagelist.clear();
        messagelist.addAll(list);
        Log.d("dhwh", "add: list" + list.size());
        notifyDataSetChanged();
    }

    private class YourGroupsViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout container;
        private ImageView backgroundImage;
        private TextView groupName;
        private TextView members;
        private TextView threads;

        public YourGroupsViewHolder(View inflate) {
            super(inflate);

            backgroundImage = (ImageView) inflate.findViewById(R.id.img);
            groupName = (TextView) inflate.findViewById(R.id.groupname);
            members = (TextView) inflate.findViewById(R.id.members);
            threads = (TextView) inflate.findViewById(R.id.threads);
            container = (LinearLayout) inflate.findViewById(R.id.container);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, GroupActivity.class);
                    i.putExtra("groupname", messagelist.get(getAdapterPosition()).getGroupname());
                    i.putExtra("groupid", messagelist.get(getAdapterPosition()).getGroupId());
                    i.putExtra("image", messagelist.get(getAdapterPosition()).getImage());
                    i.putExtra("description", messagelist.get(getAdapterPosition()).getDescription());
                    i.putExtra("rules", messagelist.get(getAdapterPosition()).getRules());
                    i.putExtra("admin", messagelist.get(getAdapterPosition()).getAdmin());
                    i.putExtra("members", messagelist.get(getAdapterPosition()).getMembers());
                    i.putExtra("follow", true);
                    ((Activity) context).startActivityForResult(i, Utils.REQ_CODE_GROP_ACITIVTY);
                    //  i.putExtra("threads",messagelist.get(getAdapterPosition()).ge);

                }
            });
        }
    }
}