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
 * Use the {@link ExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseData;
    private RecyclerView recyclerView;
    private TextView expenseTotal;
    private EditText amountEdt;
    private EditText typeEdt;
    private EditText noteEdt;
    private Button updateBtn;
    private Button deleteBtn;
    private String type;
    private String note;
    private int amount;
    private String post_key;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenseFragment newInstance(String param1, String param2) {
        ExpenseFragment fragment = new ExpenseFragment();
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
        View myView =  inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid= mUser.getUid();
        expenseTotal = myView.findViewById(R.id.expenseText);

        mExpenseData= FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        recyclerView = myView.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalvalue = 0;
                for (DataSnapshot mysnapshot:snapshot.getChildren()){
                    Data data = mysnapshot.getValue(Data.class);

                    totalvalue += data.getAmount();

                    String stTotalValue = String.valueOf(totalvalue);

                    expenseTotal.setText(stTotalValue+".00");
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
                        .setQuery(mExpenseData, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseFragment.MyViewHolder holder, int position, @NonNull Data model) {
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setAmount(model.getAmount());
                holder.setDate(model.getDate());
//                holder.setDate(dateStr);

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
            public ExpenseFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false);
                return new ExpenseFragment.MyViewHolder(view);
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
        View myView = inflater.inflate(R.layout.update_data,null);

        mydialog.setView(myView);

        amountEdt=myView.findViewById(R.id.amountEdit);
        noteEdt=myView.findViewById(R.id.noteEdit);
        typeEdt=myView.findViewById(R.id.typeEdit);

        typeEdt.setText(type);
        typeEdt.setSelection(type.length());

        noteEdt.setText(note);
        noteEdt.setSelection(note.length());

        amountEdt.setText(String.valueOf(amount));
        amountEdt.setSelection(String.valueOf(amount).length());

        updateBtn=myView.findViewById(R.id.updateBtn);
        deleteBtn=myView.findViewById(R.id.deleteBtn);

        AlertDialog dialog=mydialog.create();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type=typeEdt.getText().toString().trim();
                note=noteEdt.getText().toString().trim();
                String mdamount = String.valueOf(amount);
                mdamount=amountEdt.getText().toString().trim();

                int myAmmount = Integer.parseInt(mdamount);

                Date currDate = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(currDate);
                int month = cal.get(Calendar.MONTH) + 1;

                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String mDate = dateFormatter.format(currDate);
                Data data = new Data(myAmmount,type,note,post_key,mDate, month);

                mExpenseData.child(post_key).setValue(data);
                dialog.dismiss();

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mExpenseData.child(post_key).removeValue();

                dialog.dismiss();

            }
        });

        dialog.show();

    }

}