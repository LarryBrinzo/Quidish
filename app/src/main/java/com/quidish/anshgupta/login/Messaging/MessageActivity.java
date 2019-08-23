package com.quidish.anshgupta.login.Messaging;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.SparseBooleanArray;
import android.support.v7.view.ActionMode;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.content.ClipboardManager;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.quidish.anshgupta.login.AdDiscriptionActivity;
import com.quidish.anshgupta.login.PostYourAd.CameraUtils;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.PostYourAd.ImagePicker;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;
import com.quidish.anshgupta.login.MyAdsAndUserProfile.UserProfile;
import com.quidish.anshgupta.login.ZoomActivity;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

class messageadapter extends RecyclerView.Adapter<messageadapter.MyHoder>{

    List<messagemodel> list;
    Context context;
    ClickAdapterListener listener;
    private SparseBooleanArray selectedItems;
    FirebaseUser fuser;
    FirebaseDatabase firebaseDatabase;
    private int clk=0;

    messageadapter(List<messagemodel> list, Context context, ClickAdapterListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        fuser = firebaseAuth.getCurrentUser();

    }

    @NonNull
    @Override
    public MyHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.messagebox,parent,false);
        return new MyHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHoder holder, final int position) {
        messagemodel mylist = list.get(position);

        String textmsg=mylist.getMsg();

          msgsetadap(textmsg,holder,position);

          holder.itemView.setActivated(selectedItems.get(position, false));
          applyClickEvents(holder, position);

    }

    @SuppressLint("SetTextI18n")
    private void msgsetadap(final String textmsg, final MyHoder holder, final int position)
    {
        messagemodel mylist = list.get(position);

        holder.date=mylist.getDate();
        holder.time=mylist.getTime();
        holder.type=mylist.getType();
        holder.activityno=mylist.getActivityno();

        holder.longpress.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });

        if(list.get(position).getSeen().equals("1"))
        {
            holder.status.setImageDrawable(context.getResources().getDrawable(R.drawable.tick));
            holder.status.setColorFilter(context.getResources().getColor(R.color.blue));
            holder.myimgstatus.setColorFilter(context.getResources().getColor(R.color.blue));
        }

        else
        {
            holder.status.setColorFilter(context.getResources().getColor(R.color.grn));
            holder.myimgstatus.setColorFilter(context.getResources().getColor(R.color.white));
        }

        holder.tvDate.setText(mylist.getDate());
        if (position > 0) {
            if (list.get(position).getDate().equalsIgnoreCase(list.get(position - 1).getDate())) {
                holder.card.setVisibility(View.GONE);
            } else {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String date = df.format(Calendar.getInstance().getTime());

                if(date.equals(mylist.getDate()))
                    holder.tvDate.setText("TODAY");

                holder.card.setVisibility(View.VISIBLE);
            }
        } else {
            holder.card.setVisibility(View.VISIBLE);
        }


        if (position > 0) {
            if (list.get(position).getType().equals(list.get(position - 1).getType())) {
                holder.bg2.setBackgroundResource(R.drawable.mymsg2);
                holder.bg3.setBackgroundResource(R.drawable.mymsg2);
                holder.bg1.setBackgroundResource(R.drawable.theirmsg2);
                holder.bg4.setBackgroundResource(R.drawable.theirmsg2);
                holder.img1.setVisibility(View.GONE);
                holder.img2.setVisibility(View.GONE);
                holder.img3.setVisibility(View.GONE);
                holder.img4.setVisibility(View.GONE);
                holder.s1.setVisibility(View.GONE);
                holder.s2.setVisibility(View.VISIBLE);
                holder.s3.setVisibility(View.VISIBLE);
            } else {
                holder.bg2.setBackgroundResource(R.drawable.my_message);
                holder.bg3.setBackgroundResource(R.drawable.my_message);
                holder.bg1.setBackgroundResource(R.drawable.their_message);
                holder.bg4.setBackgroundResource(R.drawable.their_message);
                holder.img1.setVisibility(View.VISIBLE);
                holder.img2.setVisibility(View.VISIBLE);
                holder.img3.setVisibility(View.VISIBLE);
                holder.img4.setVisibility(View.VISIBLE);
                holder.s1.setVisibility(View.VISIBLE);
                holder.s2.setVisibility(View.GONE);
                holder.s3.setVisibility(View.GONE);
            }
        } else {
            holder.bg2.setBackgroundResource(R.drawable.my_message);
            holder.bg3.setBackgroundResource(R.drawable.my_message);
            holder.bg1.setBackgroundResource(R.drawable.their_message);
            holder.bg4.setBackgroundResource(R.drawable.their_message);
            holder.img1.setVisibility(View.VISIBLE);
            holder.img2.setVisibility(View.VISIBLE);
            holder.img3.setVisibility(View.VISIBLE);
            holder.img4.setVisibility(View.VISIBLE);
            holder.s1.setVisibility(View.VISIBLE);
            holder.s2.setVisibility(View.GONE);
            holder.s3.setVisibility(View.GONE);
        }

        if(textmsg.length()>=23 && textmsg.substring(0,23).equals("https://firebasestorage"))
        {
            if(list.get(position).getType().equals(list.get(position).getActivityno())){
                holder.layouts.setVisibility(View.GONE);
                holder.layoutr.setVisibility(View.GONE);
                holder.theirimg.setVisibility(View.GONE);
                holder.myimg.setVisibility(View.VISIBLE);
                holder.myimgtime.setText(mylist.getTime());
               // Picasso.get().load(textmsg).into(holder.myimgview);
                 Glide.with(context).load(textmsg).into(holder.myimgview);
            }

            else
            {
                holder.layouts.setVisibility(View.GONE);
                holder.layoutr.setVisibility(View.GONE);
                holder.theirimg.setVisibility(View.VISIBLE);
                holder.myimg.setVisibility(View.GONE);
                holder.theirimgtime.setText(mylist.getTime());
               // Picasso.get().load(textmsg).into(holder.theirimgview);
                 Glide.with(context).load(textmsg).into(holder.theirimgview);
            }
        }

        else if(list.get(position).getType().equals(list.get(position).getActivityno()))
        {
            holder.layouts.setVisibility(View.VISIBLE);
            holder.layoutr.setVisibility(View.GONE);
            holder.theirimg.setVisibility(View.GONE);
            holder.myimg.setVisibility(View.GONE);
            holder.msg2.setText(mylist.getMsg());
            holder.msg2.setText(Html.fromHtml((holder.msg2.getText().toString()) + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));
            holder.timeto.setText(mylist.getTime());
        }

        else
        {
            holder.layoutr.setVisibility(View.VISIBLE);
            holder.layouts.setVisibility(View.GONE);
            holder.theirimg.setVisibility(View.GONE);
            holder.myimg.setVisibility(View.GONE);
            holder.msg.setText(mylist.getMsg());
            holder.msg.setText(Html.fromHtml((holder.msg.getText().toString()) + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));
            holder.timefrom.setText(mylist.getTime());
        }
    }

    private void applyClickEvents(final MyHoder holder, final int position) {

        holder.longpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clk==1)
                listener.onRowClicked(position);

                else if(list.get(position).getMsg().length()>=23 && list.get(position).getMsg().substring(0,23).equals("https://firebasestorage"))
                {
                    Intent intent=new Intent(context,ZoomActivity.class);
                    intent.putExtra("pic", list.get(position).getMsg());
                    context.startActivity(intent);
                }
            }
        });

        holder.longpress.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                clk=1;
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try{
            if(list.size()==0){

                arr = 0;

            }
            else{

                arr=list.size();
            }



        }catch (Exception ignored){
        }

        return arr;

    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void selectAll() {

        for (int i = 0; i < list.size(); i++)
            selectedItems.put(i, true);
        notifyDataSetChanged();

    }


    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
        clk=0;
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        list.remove(position);
        resetCurrentIndex();
        clk=0;
    }

    public void copyclip(int position) {
        String text=list.get(position).getMsg();

        if(text.length()>=23 && text.substring(0,23).equals("https://firebasestorage"))
            return;

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);

    }

//    public void updateData(int position) {
//        //list.get(position).colored = true;
//        resetCurrentIndex();
//    }

    private void resetCurrentIndex() {
    }

    class MyHoder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        TextView msg,msg2,tvDate,timeto,timefrom,myimgtime,theirimgtime;
        LinearLayout layoutr,theirimg,main;
        ImageView status,myimgview,myimgstatus,theirimgview,img1,img2,img3,img4;
        RelativeLayout layouts,myimg;
        String date,time,type,activityno;
        ConstraintLayout longpress;
        FrameLayout bg1,bg2,bg3,bg4;
        Space s1,s2,s3;
        CardView card;

        MyHoder(View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.message);
            msg2 = itemView.findViewById(R.id.messages);
            layouts=itemView.findViewById(R.id.layouts);
            layoutr=itemView.findViewById(R.id.layoutr);
            tvDate=itemView.findViewById(R.id.tvDate);
            timeto=itemView.findViewById(R.id.Time);
            timefrom=itemView.findViewById(R.id.TimeFrom);
            status=itemView.findViewById(R.id.status);
            myimgtime=itemView.findViewById(R.id.myimgtime);
            myimgview=itemView.findViewById(R.id.myimgview);
            myimgstatus=itemView.findViewById(R.id.myimgstatus);
            myimg=itemView.findViewById(R.id.myimg);
            theirimgtime=itemView.findViewById(R.id.theirimgtime);
            theirimg=itemView.findViewById(R.id.theirimg);
            theirimgview=itemView.findViewById(R.id.theirimgview);
            longpress=itemView.findViewById(R.id.longpress);
            itemView.setOnLongClickListener(this);
            img1=itemView.findViewById(R.id.img1);
            img2=itemView.findViewById(R.id.img2);
            img3=itemView.findViewById(R.id.img3);
            img4=itemView.findViewById(R.id.img4);
            bg1=itemView.findViewById(R.id.bg1);
            bg2=itemView.findViewById(R.id.bg2);
            bg3=itemView.findViewById(R.id.bg3);
            bg4=itemView.findViewById(R.id.bg4);
            main=itemView.findViewById(R.id.main);
            s1=itemView.findViewById(R.id.s1);
            s2=itemView.findViewById(R.id.s2);
            s3=itemView.findViewById(R.id.s3);
            card=itemView.findViewById(R.id.card);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface ClickAdapterListener {

        void onRowClicked(int position);

        void onRowLongClicked(int position);
    }
}


public class MessageActivity extends AppCompatActivity implements messageadapter.ClickAdapterListener,ConnectivityReceiver.ConnectivityReceiverListener {

    public static boolean isAppRunning;
    String ad_no="0",uid,senduid,activityno="0",usertype;
    DatabaseReference ref,databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth myfba;
    String userinter,mapdate,buyerid,useremail;
    StorageReference mstoragrref,refimg2;
    FirebaseUser fuser;
    EditText msg;
    LinearLayout back,addimg,prodcl,userp;
    ProgressDialog progressDialog;
    String user1,user2,dt2;
    LinearLayout send;
    messageadapter recyclerAdapter;
    List<messagemodel> allmsg=new ArrayList<>();
    TextView usern,title,pricet,profile,lasts;
    ImageView pic;
    NestedScrollView scroll;
    Toolbar toolbar;
    int size=1;
    public static int work=2;
    RecyclerView recycle;
    String img,num1="0",num2="0",act,adtitle,price,uname;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    String offer="0",clgid;
    private static final int PICK_FROM_GALLERY = 2;
    public static final int MEDIA_TYPE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        checkConnection();

        myfba= FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
        back=findViewById(R.id.backbt);
        usern=findViewById(R.id.usern);
        title=findViewById(R.id.title);
        pricet=findViewById(R.id.price);
        profile=findViewById(R.id.profile_image);
        pic=findViewById(R.id.pic);
        lasts=findViewById(R.id.lasts);
        addimg=findViewById(R.id.addimg);
        scroll=findViewById(R.id.scroll);
        toolbar=findViewById(R.id.my_toolbar);
        prodcl=findViewById(R.id.prodcl);
        userp=findViewById(R.id.userp);
        progressDialog =new ProgressDialog(this,R.style.MyAlertDialogStyle);

        Random rand = new Random();
        int x=rand.nextInt(4);

        if(x==0)
            profile.setBackgroundResource(R.drawable.cir_borderimg);
        else if(x==1)
            profile.setBackgroundResource(R.drawable.cir_borderimg2);
        else if(x==2)
            profile.setBackgroundResource(R.drawable.cir_borderimg3);
        else if(x==3)
            profile.setBackgroundResource(R.drawable.cir_borderimg4);

        work=1;

        setSupportActionBar(toolbar);

        lasts.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        actionModeCallback = new ActionModeCallback();
        senduid = fuser.getUid();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ad_no=bundle.getString("ad_no");

            String[] splited = ad_no.split(" ");

            clgid=splited[0];
            ad_no=splited[1];

            usertype=bundle.getString("usertype");
            act=bundle.getString("act");
            uid=bundle.getString("uid");
            uname=bundle.getString("name");

            if (bundle.containsKey("offer"))
             offer=bundle.getString("offer");
        }

        prodcl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),AdDiscriptionActivity.class);
                intent.putExtra("msgad_no", clgid+" "+ad_no);
                startActivity(intent);
            }
        });

        assert usertype != null;
        if(usertype.equals("buyer"))
            activityno="2";

        else
            activityno="1";

        recycle =findViewById(R.id.recycle);
        msg=findViewById(R.id.message);
        send=findViewById(R.id.sub);

        recycle.setNestedScrollingEnabled(false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref=firebaseDatabase.getReference().child("Ads").child(clgid).child(ad_no);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        recycle.setLayoutManager(linearLayoutManager);

        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(act.equals("1"))
                    uid=dataSnapshot.child("userid").getValue(String.class);

                userp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getApplicationContext(),UserProfile.class);
                        intent.putExtra("userid", uid);
                        startActivity(intent);
                    }
                });

                img=dataSnapshot.child("image").getValue(String.class);

                String[] splited2 = img.split(" ");
                img=splited2[0];

                adtitle=dataSnapshot.child("ad_title").getValue(String.class);
                price=dataSnapshot.child("price").getValue(String.class);

                String[] splited = img.split(" ");

                Glide.with(getApplicationContext()).load(splited[0]).into(pic);
                usern.setText(uname);
                title.setText(adtitle);
                pricet.setText(price);

                String x=uname;
                profile.setText(x.substring(0,1));

                if(usertype.equals("buyer"))
                {
                    userinter=clgid+" "+ad_no+" "+senduid;
                    buyerid=senduid;
                }

                else
                {
                    userinter=clgid+" "+ad_no+" "+uid;
                    buyerid=uid;
                }

                msgnoset();

                if(uid.equals("0"))
                    return;

                if(offer.equals("1"))
                    msg.setText("I would like to buy your product for "+price);


                messagegiver(uid);
                messagegiver(senduid);

                lastseen();
                messagemethod();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                send.setEnabled(true);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });



        recycle.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if ( i3 < i7) {
                    recycle.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recycle.scrollToPosition(recycle.getAdapter().getItemCount()-1);

                        }
                    }, 0);
                }
            }
        });

        KeyboardVisibilityEvent.setEventListener(MessageActivity.this,new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {

                if (isOpen && msg.hasFocus()) {

                    //scroll to last view
                    View lastChild = scroll.getChildAt(scroll.getChildCount() - 1);
                    int bottom = lastChild.getBottom() + scroll.getPaddingBottom();
                    int sy = scroll.getScrollY();
                    int sh = scroll.getHeight();
                    int delta = bottom - (sy + sh);

                    scroll.scrollBy(0, delta);
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=msg.getText().toString();
                text=text.trim();

                if(text.length()==0)
                {
                    msg.setText("");
                    send.setEnabled(false);
                    return;
                }

                setdata(text,1);
            }
        });

    }


    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        recyclerAdapter.toggleSelection(position);
        int count = recyclerAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
            actionMode = null;
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public void onRowClicked(int position) {
        enableActionMode(position);
    }

    @Override
    public void onRowLongClicked(int position) {
        enableActionMode(position);
    }

    private void selectAll() {
        recyclerAdapter.selectAll();
    }


    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {

                case R.id.action_copy:
                    copytext();
                    mode.finish();
                    return true;

                case R.id.action_delete:
                    deleteRows();
                    mode.finish();
                    return true;

                case R.id.action_select:
                    selectAll();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            recyclerAdapter.clearSelections();
            actionMode = null;
        }
    }

    public void msgnoset(){

        DatabaseReference usermsg = FirebaseDatabase.getInstance().getReference().child("Users").child(senduid).child("Allmsg");

        usermsg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(work!=1)
                    return;

                 if (snapshot.hasChild(userinter)) {
                    DatabaseReference usermsg2;
                    usermsg2= FirebaseDatabase.getInstance().getReference().child("Users").child(senduid).child("Allmsg").child(userinter).child("msgno");
                    usermsg2.setValue("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteRows() {

        final List<Integer> selectedItemPositions =
                recyclerAdapter.getSelectedItems();

        AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
        builder.setMessage("Are you sure, You want to Delete these Message(s)")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int si=selectedItemPositions.size();

                        for (int i = si-1; i >= 0; i--) {
                            String msval=allmsg.get(selectedItemPositions.get(i)).getType();
                            if(!msval.equals(activityno))
                                continue;

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Message").child(clgid+" "+ad_no).child(buyerid).child(allmsg.get(selectedItemPositions.get(i)).getKey());
                            ref.setValue(null);
                            allmsg.remove(selectedItemPositions.get(i));
                            recyclerAdapter.removeData(selectedItemPositions.get(i));
                        }
                        recyclerAdapter.notifyDataSetChanged();
                        emptylist();
                        actionMode = null;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void copytext() {
        List<Integer> selectedItemPositions =
                recyclerAdapter.getSelectedItems();
            recyclerAdapter.copyclip(selectedItemPositions.get(selectedItemPositions.size() - 1));

            Toast.makeText(getApplicationContext(),"Message copied",Toast.LENGTH_SHORT).show();

        actionMode = null;
    }








    public void messagemethod(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Message").child(clgid+" "+ad_no).child(buyerid);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    allmsg.clear();
                    for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                        String advalue=dataSnapshot1.getValue(String.class);
                        String keyvalue=dataSnapshot1.getKey();
                        String mtype;

                        if(advalue==null)
                            continue;

                        mtype=advalue.substring(23,24);

                        DatabaseReference current_user = null;
                         if(keyvalue != null)
                        current_user= FirebaseDatabase.getInstance().getReference().child("Message").child(clgid+" "+ad_no).child(buyerid).child(keyvalue);

                        if(usertype != null && current_user != null && usertype.equals("buyer") && mtype.equals("1")) //1
                        {
                            advalue=advalue.substring(0,25)+'1'+advalue.substring(26);
                            current_user.setValue(advalue);
                        }

                        else if(usertype != null && current_user != null && usertype.equals("seller") && mtype.equals("2"))//2
                        {
                            advalue=advalue.substring(0,25)+'1'+advalue.substring(26);
                            current_user.setValue(advalue);
                        }

                        messagemodel fire=new messagemodel();

                        fire.setMsg(advalue.substring(27));
                        fire.setActivityno(activityno);
                        fire.setDate(advalue.substring(0,10));
                        fire.setTime(advalue.substring(11,16)+" "+advalue.substring(20,22));
                        fire.setType(advalue.substring(23,24));
                        fire.setSeen(advalue.substring(25,26));
                        fire.setKey(keyvalue);

                        allmsg.add(fire);

                    }

                    if(allmsg.size()>0)
                    {
                    DatabaseReference usermsg2;
                    usermsg2= FirebaseDatabase.getInstance().getReference().child("Users").child(senduid).child("Allmsg").child(userinter).child("lastmsg");
                    usermsg2.setValue(allmsg.get(allmsg.size()-1).getMsg());}

                    printmsg();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    public void emptylist(){

        if(allmsg.size()==0)
        {
            DatabaseReference current_user;
            current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Allmsg").child(userinter);
            current_user.setValue(null);

            current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(senduid).child("Allmsg").child(userinter);
            current_user.setValue(null);

        }

    }

    public void setdata(String text,int tp){

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mdformat = new SimpleDateFormat("hh:mm:ss");
        String strDate =mdformat.format(calendar.getTime());

        if (calendar.get(Calendar.AM_PM) == 0) {
            strDate+=" AM";
        } else {
            strDate+=" PM";
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        dt2=date;

        date+=" ";
        date+=strDate;

        mapdate=date;
        date+=" "+activityno+" 0";

        String fmsg=date+" "+text;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df2 = new SimpleDateFormat("yyyy/MM/dd");
        String chdate = df2.format(Calendar.getInstance().getTime());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mdformat2 = new SimpleDateFormat("HH:mm:ss");
        String strDate2 =mdformat2.format(calendar.getTime());

        chdate=chdate+" "+strDate2;

        DatabaseReference current_user;
        current_user= FirebaseDatabase.getInstance().getReference().child("Message").child(clgid+" "+ad_no).child(buyerid);
        current_user.push().setValue(fmsg);

        if(usertype.equals("buyer"))
        {
            int x=Integer.parseInt(num1);
            x++;

            num1=Integer.toString(x);

            current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Allmsg").child(userinter);
            current_user.child("time").setValue(mapdate);
            current_user.child("chtime").setValue(chdate);
            current_user.child("msgno").setValue(num1);
            current_user.child("type").setValue("seller");

            if(text.length()>=23 && text.substring(0,23).equals("https://firebasestorage"))
                current_user.child("lastmsg").setValue("Image");

            else
                current_user.child("lastmsg").setValue(text);

            current_user.child("img").setValue(img);
            current_user.child("name").setValue(user2);

            current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(senduid).child("Allmsg").child(userinter);
            current_user.child("time").setValue(mapdate);
            current_user.child("chtime").setValue(chdate);
            current_user.child("msgno").setValue(num2);
            current_user.child("type").setValue("buyer");

            if(text.length()>=23 && text.substring(0,23).equals("https://firebasestorage"))
                current_user.child("lastmsg").setValue("Image");

            else
                current_user.child("lastmsg").setValue(text);

            current_user.child("img").setValue(img);
            current_user.child("name").setValue(user1);

            if(act.equals("1"))
            current_user.child("selleruid").setValue(uid);

        }

        else
        {
            int x=Integer.parseInt(num1);
            x++;

            num1=Integer.toString(x);

            current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Allmsg").child(userinter);
            current_user.child("time").setValue(mapdate);
            current_user.child("chtime").setValue(chdate);
            current_user.child("msgno").setValue(num1);
            current_user.child("type").setValue("buyer");

            if(text.length()>=23 && text.substring(0,23).equals("https://firebasestorage"))
                current_user.child("lastmsg").setValue("Image");

            else
                current_user.child("lastmsg").setValue(text);

            current_user.child("img").setValue(img);
            current_user.child("name").setValue(user2);

            current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(senduid).child("Allmsg").child(userinter);
            current_user.child("time").setValue(mapdate);
            current_user.child("chtime").setValue(chdate);
            current_user.child("msgno").setValue(num2);
            current_user.child("type").setValue("seller");

            if(text.length()>=23 && text.substring(0,23).equals("https://firebasestorage"))
                current_user.child("lastmsg").setValue("Image");

            else
                current_user.child("lastmsg").setValue(text);

            current_user.child("img").setValue(img);
            current_user.child("name").setValue(user1);
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try
                {
                    Uri alarmSound =Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/raw/sond");
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
                    r.play();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, 70);

        msg.setText("");
        send.setEnabled(false);
    }

    public void printmsg(){

        recycle.removeAllViewsInLayout();
        recyclerAdapter = new messageadapter(allmsg,MessageActivity.this,this);
        RecyclerView.LayoutManager recyce = new GridLayoutManager(MessageActivity.this,1);
        recycle.setLayoutManager(recyce);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapter);

        recycle.scrollToPosition(allmsg.size()-1);


        if(allmsg.size()>size){
        scroll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int scrollViewHeight = scroll.getHeight();
                if (scrollViewHeight > 0) {
                    scroll.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    final View lastView = scroll.getChildAt(scroll.getChildCount() - 1);
                    final int lastViewBottom = lastView.getBottom() + scroll.getPaddingBottom();
                    final int deltaScrollY = lastViewBottom - scrollViewHeight - scroll.getScrollY();
                    scroll.scrollBy(0, deltaScrollY);
                }
            }
        });}

        size=allmsg.size();

    }

    public void messagegiver(final String userid) {

        DatabaseReference refer;
        refer=firebaseDatabase.getReference().child("Users").child(userid);
        refer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(userid.equals(uid))
                {
                    user1=dataSnapshot.child("Full_Name").getValue(String.class);
                    num1=dataSnapshot.child("Allmsg").child(userinter).child("msgno").getValue(String.class);
                    useremail=dataSnapshot.child("Email_ID").getValue(String.class);
                }

                else
                {
                    user2=dataSnapshot.child("Full_Name").getValue(String.class);
                    num2=dataSnapshot.child("Allmsg").child(userinter).child("msgno").getValue(String.class);

                    if(num1==null)
                        num1="0";
                    if(num2==null)
                        num2="0";

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void lastseen(){

        DatabaseReference refer;
        refer=firebaseDatabase.getReference().child("Users").child(uid).child("lastseen");
        refer.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String last=dataSnapshot.getValue(String.class);

                assert last != null;
                if(last.equals("Online"))
                {
                    lasts.setVisibility(View.VISIBLE);
                    lasts.setText("online");
                    return;
                }

                String datel=last.substring(0,10);
                String timel=last.substring(11,16)+" "+last.substring(20,22);
                String prt="Last seen ";

                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                dt2= df.format(Calendar.getInstance().getTime());

                if(dt2.equals(datel)) {
                    prt+="today at "+timel;
                } else
                    prt+=datel+" "+timel;

                lasts.setVisibility(View.VISIBLE);
                lasts.setText(prt);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
              work=2;
             finish();
            super.onBackPressed();
    }


    private void selectImage() {

        final CharSequence[] options = { "Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);

        String titleText = "Add Image";
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.rgb(45, 62, 78));
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);

        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        builder.setTitle(ssBuilder);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Camera"))
                {

                    if (CameraUtils.checkPermissions(getApplicationContext())) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 1);
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }

                }
                else if (options[item].equals("Gallery"))
                {

                    try {
                        if (ActivityCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                        } else {
                            Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }

                break;
        }
    }

    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                selectImage();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }



    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == 1) {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
              //  Bitmap bmp = ImagePicker.getImageFromResult(this, resultCode, data);

                if(bmp==null)
                    return;

                Uri selectedImage = getImageUri(getApplicationContext(),bmp);

                uploadImage1(selectedImage);

            }
            else if (requestCode == 2) {

                Bitmap bmp = ImagePicker.getImageFromResult(this, resultCode, data);
                Uri selectedImage = getImageUri(getApplicationContext(),bmp);

                uploadImage1(selectedImage);
            }
        }
    }

    private void uploadImage1(Uri filepath1) {

            if(filepath1 != null)
            {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMessage("Sending Image...");
                        progressDialog.show();
                    }
                }, 1000);


                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat mdformat = new SimpleDateFormat("hh:mm:ss");
                String strDate =mdformat.format(calendar.getTime());

                if (calendar.get(Calendar.AM_PM) == 0) {
                    strDate+="AM";
                } else {
                    strDate+="PM";
                }

                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd:MM:yyyy");
                String date = df.format(Calendar.getInstance().getTime());

                date+=strDate;
                mstoragrref= FirebaseStorage.getInstance().getReference().child("Images");
                refimg2 = mstoragrref.child(date);

                refimg2.putFile(filepath1)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                refimg2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String s=uri.toString();
                                        setdata(s,2);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                progressDialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            }
                        });
            }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.clear:
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
            builder.setMessage("Are you sure, You want to delete chat")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            allmsg.clear();
                            ref=FirebaseDatabase.getInstance().getReference().child("Message").child(ad_no).child(buyerid);
                            ref.setValue(null);
                            emptylist();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            break;
        }

        case R.id.report:
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
            builder.setMessage("Are you sure, You want to report this user")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                           Toast.makeText(getApplicationContext(),"Your response is saved. And we look to this user",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            break;
        }


    }
        return(super.onOptionsItemSelected(item));
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showDialog(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showDialog(isConnected);
    }

    private void showDialog(boolean isConnected)
    {
        if (!isConnected) {

            Intent intent=new Intent(getApplicationContext(),No_InternetActivity.class);
            startActivity(intent);


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAppRunning = false;
    }

    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(MessageActivity.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }


}
