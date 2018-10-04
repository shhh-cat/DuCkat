package dev.thiennguyen.duckat;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FriendslistFragment extends Fragment {

    RecyclerView friendslist;
    RecyclerView.LayoutManager friendslistmanagerlayout;
    TextView viewmode;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    //Auth
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    //Viewholder
    public static class friendHolder extends RecyclerView.ViewHolder{

        TextView name;

        public friendHolder(View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.friends_item);
        }
    }

    //Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Query queryListofFriend = db.collection("users");
            //.document(auth.getCurrentUser().getUid())
            //.collection("friends")
            //.limit(50);

    private FirestoreRecyclerOptions<friendsObject> options = new FirestoreRecyclerOptions.Builder<friendsObject>
            ()
            .setQuery(queryListofFriend, friendsObject.class).build();

    private FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<friendsObject, friendHolder>(options) {
        @Override
        protected void onBindViewHolder(@NonNull friendHolder holder, int position, @NonNull friendsObject model) {
            holder.name.setText(model.getName());
        }

        @NonNull
        @Override
        public friendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_friends,parent,false);
            return new friendHolder(view);
        }
    };
    //End Firestore


    @Override
    public void onStart(){
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null){
            adapter.stopListening();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View RootView = inflater.inflate(R.layout.fragment_friends,container,false);
        friendslist = (RecyclerView)RootView.findViewById(R.id.list_of_friends);
        viewmode = (TextView)RootView.findViewById(R.id.friends_viewmode);
        friendslistmanagerlayout = new LinearLayoutManager(getContext());
        displayFriendsList(adapter,1);
        return RootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.search_menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                adapter.stopListening();
                viewmode.setText(R.string.friend_viewmode_Search);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                adapter.startListening();
                displayFriendsList(adapter,1);
                return true;
            }
        });
        if (searchItem != null){
            searchView = (SearchView) searchItem.getActionView();
        }

        if (searchView != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String searchName) {
                    Log.i("onQueryTextChange", searchName);
                        SearchofFriend(searchName);
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_menu:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    private void displayFriendsList(RecyclerView.Adapter adapter,int mode){
        switch (mode){
            case 1:
                viewmode.setText(R.string.friend_viewmode_FriendList);
                break;
            case 2:
                viewmode.setText(R.string.friend_viewmode_Search);
                break;
        }
        friendslist.setLayoutManager(friendslistmanagerlayout);
        friendslist.setAdapter(adapter);
    }

    private void SearchofFriend(String searchName){
        Query querySearchofFriend = db.collection("users")
                .orderBy("name")
                .startAt(searchName.trim())
                .endAt(searchName + '\uf8ff');
        querySearchofFriend.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("result", document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d("Error", "Error getting documents: ", task.getException());
                }
            }
        });

        FirestoreRecyclerOptions<friendsObject> options = new FirestoreRecyclerOptions.Builder<friendsObject>
                ()
                .setQuery(querySearchofFriend, friendsObject.class).build();

        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<friendsObject, friendHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull friendHolder holder, int position, @NonNull friendsObject model) {
                holder.name.setText(model.getName());
            }

            @NonNull
            @Override
            public friendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_friends,parent,false);
                return new friendHolder(view);
            }
        };
        if (!searchName.trim().isEmpty()){
        adapter.startListening();
        displayFriendsList(adapter,2);
        } else {
            adapter.stopListening();
        }
    }
}
