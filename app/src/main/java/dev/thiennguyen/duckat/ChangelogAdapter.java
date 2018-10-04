package dev.thiennguyen.duckat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChangelogAdapter extends BaseAdapter{
    Context context;
    String nameVersion[];
    String contentchangelog[];
    LayoutInflater inflater;

    public ChangelogAdapter(Context applicationContext, String[] nameVersion, String[] contentchangelog){
        this.nameVersion = nameVersion;
        this.contentchangelog = contentchangelog;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount(){
        return nameVersion.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup){
        view = inflater.inflate(R.layout.list_changelog,null);
        TextView nameVersion = (TextView)view.findViewById(R.id.nameVersion);
        TextView contentchagelog = (TextView)view.findViewById(R.id.contentchangelog);
        nameVersion.setText(this.nameVersion[i]);
        contentchagelog.setText(this.contentchangelog[i]);
        return view;
    }
}
