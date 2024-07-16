package com.example.expensemanagerapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.expensemanagerapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IncomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IncomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeData;
    private RecyclerView recyclerView;
    private TextView incomeTotal;
    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;
    private Button updateBtn;
    private Button deleteBtn;
    private String type;
    private String note;
    private  int amount;
    private String post_key;

    public IncomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IncomeFragment newInstance(String param1, String param2) {
        IncomeFragment fragment = new IncomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_income, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid= mUser.getUid();
        incomeTotal = myView.findViewById(R.id.incomeText);

        mIncomeData= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        recyclerView = myView.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalvalue = 0;
                for (DataSnapshot mysnapshot:snapshot.getChildren()){
                    Data data = mysnapshot.getValue(Data.class);

                    totalvalue += data.getAmount();

                    String stTotalValue = String.valueOf(totalvalue);

                    incomeTotal.setText(stTotalValue+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mIncomeData, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setAmount(model.getAmount());
                holder.setDate(model.getDate());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int adapterPosition = holder.getAdapterPosition();
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            post_key = getRef(adapterPosition).getKey();
                            type = model.getType();
                            note = model.getNote();
                            amount = model.getAmount();
                            updateDataItem();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public MyViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        private void setType(String type) {

            TextView mType = mView.findViewById(R.id.typeText);
            mType.setText(type);
        }

        private void setNote(String note) {

            TextView mNote = mView.findViewById(R.id.noteText);
            mNote.setText(note);
        }

        private void setDate(String date) {

            TextView mDate = mView.findViewById(R.id.dateText);
            mDate.setText(convertDateFormat(date));
        }

        private String convertDateFormat(String date) {
            try {
                SimpleDateFormat backendFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date parsedDate = backendFormat.parse(date);
                SimpleDateFormat frontendFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
                return frontendFormat.format(parsedDate);
            } catch (Exception e) {
                e.printStackTrace();
                return date;
            }
        }

        private void setAmount(int amount) {

            TextView mAmount = mView.findViewById(R.id.amountText);
            String stammount = String.valueOf(amount);
            mAmount.setText(stammount);
        }
    }

    private void updateDataItem() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.update_data, null);
        mydialog.setView(myView);

        edtAmount = myView.findViewById(R.id.amountEdit);
        edtType = myView.findViewById(R.id.typeEdit);
        edtNote = myView.findViewById(R.id.noteEdit);

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        updateBtn = myView.findViewById(R.id.updateBtn);
        deleteBtn = myView.findViewById(R.id.deleteBtn);

        AlertDialog dialog = mydialog.create();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();
                String mdamount = String.valueOf(amount);
                mdamount=edtAmount.getText().toString().trim();

                int myAmmount = Integer.parseInt(mdamount);

                Date currDate = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(currDate);
                int month = cal.get(Calendar.MONTH) + 1;

                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String mDate = dateFormatter.format(currDate);
                Data data = new Data(myAmmount,type,note,post_key,mDate,month);

                mIncomeData.child(post_key).setValue(data);
                dialog.dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIncomeData.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}