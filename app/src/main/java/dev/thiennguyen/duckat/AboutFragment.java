package dev.thiennguyen.duckat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class AboutFragment extends Fragment {
    ListView changelogList;
    String[] nameVersion = {
            "0.0.1",
            "0.0.2-alpha",
            "0.0.21-alpha",
            "0.0.22-29-alpha"
    };
    String[] contentchangelog = {
            "+ Start simple app",
            "+ Great Improvement\n" +
                    "+ Fix bug\n" +
                    "+ Add navigation\n" +
                    "+ Rename the application to \"DuCkat\"\n" +
                    "+ Changed icon",
            "+ Fix bug(sign out)",
            "+ Has converted the database\n" +
                    "+ Has split chat bubbles\n" +
                    "+ Update profile!\n" +
                    "+ Has Changed icon\n" +
                    "+ Version prepare for big update"
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about,container,false);
        changelogList = (ListView)view.findViewById(R.id.cview);
        ChangelogAdapter changelogAdapter = new ChangelogAdapter(getContext(),nameVersion,contentchangelog);
        changelogList.setAdapter(changelogAdapter);
        return view;
    }
}
