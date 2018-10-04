package dev.thiennguyen.duckat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FirebaseFirestore;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


public class ChatFragment extends Fragment {
    //View
    private View RootView;
    EmojiconEditText emojiconEditText;
    ImageView emojiButton,submitButton;
    RecyclerView messageView;
    RecyclerView.LayoutManager messageLayoutManager;
    //Action
    EmojIconActions emojIconActions;

    //Auth
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public static class messageHolder extends RecyclerView.ViewHolder{

        TextView content, content_myself, form, time;
        ImageView avatar;

        public messageHolder(View v) {
            super(v);
            content = (BubbleTextView) v.findViewById(R.id.message_content);
            content_myself = (BubbleTextView) v.findViewById(R.id.message_content_myself);
            form = (TextView) v.findViewById(R.id.message_from);
            time = (TextView) v.findViewById(R.id.message_time);
            avatar = (ImageView) v.findViewById(R.id.message_avt);
        }
    }

    //Firestore

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Query query = db.collection("conversations")
            .document("conversation")
            .collection("messages")
            .orderBy("time")
            .limit(50);

    private FirestoreRecyclerOptions<messageObject> options = new FirestoreRecyclerOptions.Builder<messageObject>
            ()
            .setQuery(query, messageObject.class).build();

    private FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<messageObject, messageHolder>(options) {
        @Override
        protected void onBindViewHolder(@NonNull messageHolder holder, int position, @NonNull messageObject model) {
            if (model.getFrom().equals( auth.getCurrentUser().getDisplayName())){
                holder.content_myself.setVisibility(holder.content_myself.VISIBLE);
                holder.content_myself.setText(model.getContent());
                holder.content.setVisibility(holder.content.INVISIBLE);
                holder.avatar.setVisibility(holder.avatar.INVISIBLE);
            }
            else {
                holder.content.setVisibility(holder.content.VISIBLE);
                holder.content.setText(model.getContent());
                holder.content_myself.setVisibility(holder.content_myself.INVISIBLE);
                holder.avatar.setVisibility(holder.avatar.VISIBLE);
            }
            holder.form.setText(model.getFrom());
            holder.time.setText(DateFormat.format("h:mm a",model.getTime().toDate()));
        }

        @NonNull
        @Override
        public messageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_message,parent,false);
            return new messageHolder(view);
        }

        @Override
        public void onDataChanged() {
            super.onDataChanged();
            messageView.scrollToPosition(this.getItemCount() -1);
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
        final View RootView = inflater.inflate(R.layout.fragment_chat,container,false);
        this.RootView = RootView;
        emojiButton = (ImageView)RootView.findViewById(R.id.emoji_button);
        submitButton = (ImageView) RootView.findViewById(R.id.submit_button);
        emojiconEditText = (EmojiconEditText)RootView.findViewById(R.id.emojicon_edit_text);
        emojIconActions = new EmojIconActions(getActivity().getApplicationContext(),RootView,emojiButton,emojiconEditText);
        emojIconActions.ShowEmojicon();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!emojiconEditText.getText().toString().matches("")) {
                    db.collection("conversations")
                            .document("conversation")
                            .collection("messages")
                            .add(new messageObject(emojiconEditText.getText().toString(),
                                    auth.getCurrentUser().getDisplayName()))
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(RootView, "Send Failed", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                    emojiconEditText.setText("");
                    emojiconEditText.requestFocus();
                }
            }
        });
        displayChatMessage(RootView);
        return RootView;
    }


    private void displayChatMessage(View RootView) {

        messageView = (RecyclerView) RootView.findViewById(R.id.list_of_message);
        messageLayoutManager = new LinearLayoutManager(getContext());
        ((LinearLayoutManager) messageLayoutManager).setStackFromEnd(true);
        ((LinearLayoutManager) messageLayoutManager).setSmoothScrollbarEnabled(true);


        messageView.scrollToPosition(adapter.getItemCount()- 1);
        messageView.setLayoutManager(messageLayoutManager);
        messageView.setAdapter(adapter);
    }
}
