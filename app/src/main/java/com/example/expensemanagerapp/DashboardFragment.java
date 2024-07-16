package com.example.expensemanagerapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanagerapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FloatingActionButton fabMain;
    private FloatingActionButton fabIncome;
    private FloatingActionButton fabExpeense;

    private TextView fabIncomeText;
    private TextView fabExpenseText;

    private boolean isOpen = false;

    private Animation fadOpen, fadClose;

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    private TextView incomeDahboard;
    private TextView expenseDashboard;
    private RecyclerView recyclerIncome;
    private RecyclerView recyclerExpense;

    private Spinner incomeMonthSpinner;
    private Spinner incomeYearSpinner;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
        View myView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        fabMain = myView.findViewById(R.id.fabMainPlusBtn);
        fabIncome = myView.findViewById(R.id.incomeFtBtn);
        fabExpeense = myView.findViewById(R.id.expenseFtBtn);

        fabIncomeText = myView.findViewById(R.id.incomeText);
        fabExpenseText = myView.findViewById(R.id.expenseText);

        incomeDahboard = myView.findViewById(R.id.incomeSetResult);
        expenseDashboard = myView.findViewById(R.id.expenseSetResult);

        recyclerIncome = myView.findViewById(R.id.recyclerIncome);
        recyclerExpense = myView.findViewById(R.id.recyclerExpense);

        incomeMonthSpinner = myView.findViewById(R.id.incomeMonthSpinner);
        incomeYearSpinner = myView.findViewById(R.id.incomeYearSpinner);

        fadOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        fadClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        setupSpinners();

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();

                if (isOpen) {

                    fabIncome.startAnimation(fadClose);
                    fabExpeense.startAnimation(fadClose);
                    fabIncome.setClickable(false);
                    fabExpeense.setClickable(false);

                    fabIncomeText.startAnimation(fadClose);
                    fabExpenseText.startAnimation(fadClose);
                    fabIncomeText.setClickable(false);
                    fabExpenseText.setClickable(false);
                    isOpen = false;
                } else {

                    fabIncome.startAnimation(fadOpen);
                    fabExpeense.startAnimation(fadOpen);
                    fabIncome.setClickable(true);
                    fabExpeense.setClickable(true);

                    fabIncomeText.startAnimation(fadOpen);
                    fabExpenseText.startAnimation(fadOpen);
                    fabIncomeText.setClickable(true);
                    fabExpenseText.setClickable(true);
                    isOpen = true;
                }

            }
        });


        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalsum = 0;

                for (DataSnapshot mysnapshot : snapshot.getChildren()) {

                    Data data = mysnapshot.getValue(Data.class);

                    totalsum += data.getAmount();

                    String stResult = String.valueOf(totalsum);

                    incomeDahboard.setText(stResult + ".00");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalExpense = 0;

                for (DataSnapshot mysnapshot : snapshot.getChildren()) {
                    Data data = mysnapshot.getValue(Data.class);
                    totalExpense += data.getAmount();
                    String stResult = String.valueOf(totalExpense);
                    expenseDashboard.setText(stResult + ".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        incomeMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        incomeYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        updateDashboardValues();

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        recyclerIncome.setHasFixedSize(true);
        recyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        recyclerExpense.setHasFixedSize(true);
        recyclerExpense.setLayoutManager(layoutManagerExpense);


        return myView;
    }

    private void ftAnimation() {
        if (isOpen) {

            fabIncome.startAnimation(fadClose);
            fabExpeense.startAnimation(fadClose);
            fabIncome.setClickable(false);
            fabExpeense.setClickable(false);

            fabIncomeText.startAnimation(fadClose);
            fabExpenseText.startAnimation(fadClose);
            fabIncomeText.setClickable(false);
            fabExpenseText.setClickable(false);
            isOpen = false;
        } else {

            fabIncome.startAnimation(fadOpen);
            fabExpeense.startAnimation(fadOpen);
            fabIncome.setClickable(true);
            fabExpeense.setClickable(true);

            fabIncomeText.startAnimation(fadOpen);
            fabExpenseText.startAnimation(fadOpen);
            fabIncomeText.setClickable(true);
            fabExpenseText.setClickable(true);
            isOpen = true;
        }
    }

    private void addData() {

        fabIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();
            }
        });

        fabExpeense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInasert();
            }
        });

    }

    public void incomeDataInsert() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.insert_data, null);

        mydialog.setView(myView);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        EditText amountEdit = myView.findViewById(R.id.amountEdit);
        EditText typeEdit = myView.findViewById(R.id.typeEdit);
        EditText noteEdit = myView.findViewById(R.id.noteEdit);

        Button btnSave = myView.findViewById(R.id.saveBtn);
        Button btnCancel = myView.findViewById(R.id.cancelBtn);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = typeEdit.getText().toString().trim();
                String amount = amountEdit.getText().toString().trim();
                String note = noteEdit.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    typeEdit.setError("Required Field..");
                    return;
                }

                if (TextUtils.isEmpty(amount)) {
                    amountEdit.setError("Required Field..");
                    return;
                }

                int amountint = Integer.parseInt(amount);

                if (TextUtils.isEmpty(note)) {
                    noteEdit.setError("Required Field..");
                    return;
                }

                String id = mIncomeDatabase.push().getKey();

                Date currDate = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(currDate);
                int month = cal.get(Calendar.MONTH) + 1;

                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String mDate = dateFormatter.format(currDate);

                Data data = new Data(amountint, type, note, id, mDate, month);

                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_LONG).show();

                ftAnimation();

                dialog.dismiss();
                ;
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void expenseDataInasert() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.insert_data, null);

        mydialog.setView(myView);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        EditText amountEdit = myView.findViewById(R.id.amountEdit);
        EditText typeEdit = myView.findViewById(R.id.typeEdit);
        EditText noteEdit = myView.findViewById(R.id.noteEdit);

        Button btnSave = myView.findViewById(R.id.saveBtn);
        Button btnCancel = myView.findViewById(R.id.cancelBtn);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = typeEdit.getText().toString().trim();
                String amount = amountEdit.getText().toString().trim();
                String note = noteEdit.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    typeEdit.setError("Required Field..");
                    return;
                }

                if (TextUtils.isEmpty(amount)) {
                    amountEdit.setError("Required Field..");
                    return;
                }

                int amountint = Integer.parseInt(amount);

                if (TextUtils.isEmpty(note)) {
                    noteEdit.setError("Required Field..");
                    return;
                }

                String id = mExpenseDatabase.push().getKey();

                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date currDate = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(currDate);
                int month = cal.get(Calendar.MONTH) + 1;
                String mDate = dateFormatter.format(currDate);

                Data data = new Data(amountint, type, note, id, mDate, month);

                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Expense Added", Toast.LENGTH_LONG).show();

                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    public void onStart() {
        super.onStart();

        Query incomeQuery = mIncomeDatabase;
        Query expenseQuery = mExpenseDatabase;

        FirebaseRecyclerOptions<Data> incomeOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(incomeQuery, Data.class)
                .build();

        FirebaseRecyclerOptions<Data> expenseOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(expenseQuery, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(incomeOptions) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder incomeViewHolder, int position, @NonNull Data model) {
                incomeViewHolder.setIncomeType(model.getType());
                incomeViewHolder.setIncomeAmount(model.getAmount());
                incomeViewHolder.setIncomeDate(model.getDate());
            }

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income, parent, false);
                return new IncomeViewHolder(view);
            }
        };

        recyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();

        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(expenseOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder expenseViewHolder, int position, @NonNull Data model) {
                expenseViewHolder.setIncomeType(model.getType());
                expenseViewHolder.setIncomeAmount(model.getAmount());
                expenseViewHolder.setIncomeDate(model.getDate());
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense, parent, false);
                return new ExpenseViewHolder(view);
            }
        };

        recyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {

        View mIncomeView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }

        public void setIncomeType(String type) {
            TextView mtype = mIncomeView.findViewById(R.id.typeIncome);
            mtype.setText(type);
        }

        public void setIncomeAmount(int amount) {
            TextView mAmount = mIncomeView.findViewById(R.id.amountIncome);
            String stamount = String.valueOf(amount);
            mAmount.setText(stamount);
        }

        public void setIncomeDate(String date) {
            TextView mDate = mIncomeView.findViewById(R.id.dateIncome);
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
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        View mExpenseView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView = itemView;
        }

        public void setIncomeType(String type) {
            TextView mtype = mExpenseView.findViewById(R.id.typeExpense);
            mtype.setText(type);
        }

        public void setIncomeAmount(int amount) {
            TextView mAmount = mExpenseView.findViewById(R.id.amountExpense);
            String stamount = String.valueOf(amount);
            mAmount.setText(stamount);
        }

        public void setIncomeDate(String date) {
            TextView mDate = mExpenseView.findViewById(R.id.dateExpense);
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
    }

    private void setupSpinners() {
        // Set up month spinner
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.months_array, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        incomeMonthSpinner.setAdapter(monthAdapter);

        // Set up year spinner
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[]{"All", String.valueOf(currentYear), String.valueOf(currentYear - 1), String.valueOf(currentYear - 2), String.valueOf(currentYear - 3), String.valueOf(currentYear - 4)};
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        incomeYearSpinner.setAdapter(yearAdapter);

        // Set current month and year
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        incomeMonthSpinner.setSelection(0);

        incomeYearSpinner.setSelection(0); // 0 is the current year
    }

    private void filterData() {
        String selectedMonth = incomeMonthSpinner.getSelectedItem().toString();
        String selectedYear = incomeYearSpinner.getSelectedItem().toString();

        System.out.println("Selected Year: " + selectedYear);
        System.out.println("Selected Month: " + selectedMonth);

        Query incomeQuery = mIncomeDatabase;
        Query expenseQuery = mExpenseDatabase;

        if (!selectedYear.equals("All") && !selectedMonth.equals("All")) {
            // Both year and month are selected
            String startMonth = getAbbreviatedMonth(selectedMonth);
            int year = Integer.parseInt(selectedYear);

            // Calculate the start and end dates for the selected month and year
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, getMonthIndex(selectedMonth)); // getMonthIndex method converts month string to index
            calendar.set(Calendar.DAY_OF_MONTH, 1); // Start date is the 1st day of the selected month
            Date startDate = calendar.getTime();

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // End date is the last day of the selected month
            Date endDate = calendar.getTime();

            // Format dates to match the Firebase database date format
//            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String formattedStartDate = dateFormat.format(startDate);
            String formattedEndDate = dateFormat.format(endDate);

            System.out.println("Filtering for specific month and year");
            System.out.println("Start Date: " + formattedStartDate);
            System.out.println("End Date: " + formattedEndDate);

            incomeQuery = mIncomeDatabase.orderByChild("date").startAt(formattedStartDate).endAt(formattedEndDate + "\uf8ff");
            expenseQuery = mExpenseDatabase.orderByChild("date").startAt(formattedStartDate).endAt(formattedEndDate + "\uf8ff");
        } else if (!selectedYear.equals("All")) {
            int year = Integer.parseInt(selectedYear);

            // Start and end dates for the entire year
            String startDate = year + "-01-01";
            String endDate = year + "-12-31";

            System.out.println("Filtering for specific year");
            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);

            // Firebase queries using the correct date format
            incomeQuery = mIncomeDatabase.orderByChild("date")
                    .startAt(startDate)
                    .endAt(endDate + "\uf8ff");

            expenseQuery = mExpenseDatabase.orderByChild("date")
                    .startAt(startDate)
                    .endAt(endDate + "\uf8ff");

            System.out.println("Income Query: " + incomeQuery.toString());
            System.out.println("Expense Query: " + expenseQuery.toString());
        } else if (!selectedMonth.equals("All")) {
            // Only month is selected, show all years for that month
            int startMonth = getMonthIndex(selectedMonth);

            System.out.println("Filtering for specific month across all years");
            System.out.println("Start Month: " + startMonth);

            incomeQuery = mIncomeDatabase.orderByChild("month")
                    .equalTo(startMonth + 1);

            expenseQuery = mExpenseDatabase.orderByChild("month")
                    .equalTo(startMonth + 1);

        } else {
            // All months and all years selected, retrieve all data
            System.out.println("Filtering for all data");
            incomeQuery = mIncomeDatabase.orderByChild("date");
            expenseQuery = mExpenseDatabase.orderByChild("date");
        }

        // Debugging logs
        System.out.println("Income Query: " + incomeQuery);
        System.out.println("Expense Query: " + expenseQuery);

        FirebaseRecyclerOptions<Data> incomeOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(incomeQuery, Data.class)
                .build();

        FirebaseRecyclerOptions<Data> expenseOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(expenseQuery, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(incomeOptions) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {
                System.out.println("Binding Income Data: " + model.getDate() + ", Amount: " + model.getAmount());
                holder.setIncomeAmount(model.getAmount());
                holder.setIncomeType(model.getType());
                holder.setIncomeDate(model.getDate());
            }

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income, parent, false);
                return new IncomeViewHolder(view);
            }
        };

        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(expenseOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {
                System.out.println("Binding Expense Data: " + model.getDate() + ", Amount: " + model.getAmount());
                holder.setIncomeAmount(model.getAmount());
                holder.setIncomeType(model.getType());
                holder.setIncomeDate(model.getDate());
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense, parent, false);
                return new ExpenseViewHolder(view);
            }
        };

        recyclerIncome.setAdapter(incomeAdapter);
        recyclerExpense.setAdapter(expenseAdapter);

        incomeAdapter.startListening();
        expenseAdapter.startListening();
    }


    private String getAbbreviatedMonth(String month) {
        return month.substring(0, 3); // Get the first three characters of the month
    }

    private int getMonthIndex(String month) {
        switch (month.toLowerCase()) {
            case "january":
                return Calendar.JANUARY;
            case "february":
                return Calendar.FEBRUARY;
            case "march":
                return Calendar.MARCH;
            case "april":
                return Calendar.APRIL;
            case "may":
                return Calendar.MAY;
            case "june":
                return Calendar.JUNE;
            case "july":
                return Calendar.JULY;
            case "august":
                return Calendar.AUGUST;
            case "september":
                return Calendar.SEPTEMBER;
            case "october":
                return Calendar.OCTOBER;
            case "november":
                return Calendar.NOVEMBER;
            case "december":
                return Calendar.DECEMBER;
            default:
                return -1; // Invalid month string
        }
    }


    private String getMonthNumber(String month) {
        switch (month.toLowerCase()) {
            case "january": return "01";
            case "february": return "02";
            case "march": return "03";
            case "april": return "04";
            case "may": return "05";
            case "june": return "06";
            case "july": return "07";
            case "august": return "08";
            case "september": return "09";
            case "october": return "10";
            case "november": return "11";
            case "december": return "12";
            default: return "01"; // Default to January if month is invalid
        }
    }


    private void updateDashboardValues() {
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalIncome = 0;
                for (DataSnapshot mysnapshot : snapshot.getChildren()) {
                    Data data = mysnapshot.getValue(Data.class);
                    totalIncome += data.getAmount();
                }
                incomeDahboard.setText(totalIncome + ".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });


    }
}