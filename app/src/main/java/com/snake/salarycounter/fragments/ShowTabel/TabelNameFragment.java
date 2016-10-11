package com.snake.salarycounter.fragments.ShowTabel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.snake.salarycounter.R;
import com.snake.salarycounter.events.TextEvent;
import com.snake.salarycounter.models.Tabel;
import com.snake.salarycounter.watchers.TextValidator;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class TabelNameFragment extends Fragment {

    private static final String ARG_PARAM1 = "_id";

    private long mId;

    private int currentYear;
    private int currentMonth;
    private int currentDay;

    private Tabel t;

    @BindView(R.id.tabel_start_date) EditText tabelStartDate;
    @BindView(R.id.tabel_hours) EditText tabelHours;
    @OnClick(R.id.tabel_start_date) void onTabelStartDateClick(){
        DatePickerDialog  tpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        currentYear = year;
                        currentMonth = monthOfYear;
                        currentDay = dayOfMonth;

                        t.startDate = new DateTime(currentYear, currentMonth + 1, currentDay, 0, 0);
                        t.save();
                        tabelStartDate.setText(t.getText());
                    }
                },
                currentYear,
                currentMonth,
                currentDay
        );
        tpd.setAccentColor(getResources().getColor(R.color.mdtp_accent_color));
        tpd.show(getActivity().getFragmentManager(), "startDate");
    }

    public TabelNameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getLong(ARG_PARAM1);
            t = Tabel.getById(mId);

            currentYear = t.startDate.getYear();
            currentMonth = t.startDate.getMonthOfYear() - 1; // because lib use JAN - 0 ((
            currentDay = t.startDate.getDayOfMonth();
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tabel_name, container, false);

        ButterKnife.bind(this, v);

        tabelStartDate.setText(t.getText());

        tabelHours.addTextChangedListener(new TextValidator(getActivity(), tabelHours, t.getId()));
        tabelHours.setText(String.valueOf(t.hours));

        return v;
    }

    public void onEvent(TextEvent event){
        if(t.getId() == event.mId) {
            switch (event.mTextEditId) {
                case R.id.tabel_hours:
                    t.hours = Double.valueOf(event.mValue);
                    t.save();
                    break;
            }
        }
    }
}
