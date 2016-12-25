package com.snake.salarycounter.fragments.FinanceCondition;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.snake.salarycounter.R;
import com.snake.salarycounter.events.TextEvent;
import com.snake.salarycounter.models.FinanceCondition;
import com.snake.salarycounter.watchers.TextValidator;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import de.greenrobot.event.EventBus;

public class FinanceConditionMoneyFragment extends Fragment {

    private static final String ARG_PARAM1 = "_id";

    private long mId;

    private FinanceCondition fc;

    @BindView(R.id.finance_condition_salary)
    EditText financeConditionSalary;
    @BindView(R.id.finance_condition_addition)
    EditText financeConditionAddition;
    @BindView(R.id.finance_condition_addition_proc)
    EditText financeConditionAdditionProc;
    @BindView(R.id.finance_condition_north)
    EditText financeConditionNorth;
    @BindView(R.id.finance_condition_district)
    EditText financeConditionDistrict;
    @BindView(R.id.finance_condition_bonus)
    EditText financeConditionBonus;
    @BindView(R.id.finance_condition_alimony)
    EditText financeConditionAlimony;
    @BindView(R.id.finance_condition_alimony_proc)
    EditText financeConditionAlimonyProc;
    @BindView(R.id.finance_condition_residue)
    EditText financeConditionResidue;
    @BindView(R.id.finance_condition_residue_proc)
    EditText financeConditionResidueProc;

    @BindView(R.id.finance_condition_enable_tax)
    CheckBox enableTax;

    @OnCheckedChanged(R.id.finance_condition_enable_tax)
    void onEnableTaxChecked(boolean checked) {
        fc.enable_tax = checked;
        fc.save();
    }


    public FinanceConditionMoneyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getLong(ARG_PARAM1);
            fc = FinanceCondition.getById(mId);
        }
        EventBus.getDefault().register(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_finance_condition_money, container, false);
        ButterKnife.bind(this, v);

        financeConditionSalary.setText(fc.salary.toString());
        financeConditionSalary.addTextChangedListener(new TextValidator(getActivity(), financeConditionSalary, fc.getId()));

        financeConditionAddition.setText(fc.addition.toString());
        financeConditionAddition.addTextChangedListener(new TextValidator(getActivity(), financeConditionAddition, fc.getId()));

        financeConditionAdditionProc.setText(String.valueOf(fc.addition_proc));
        financeConditionAdditionProc.addTextChangedListener(new TextValidator(getActivity(), financeConditionAdditionProc, fc.getId()));

        financeConditionNorth.setText(String.valueOf(fc.north));
        financeConditionNorth.addTextChangedListener(new TextValidator(getActivity(), financeConditionNorth, fc.getId()));

        financeConditionDistrict.setText(String.valueOf(fc.district));
        financeConditionDistrict.addTextChangedListener(new TextValidator(getActivity(), financeConditionDistrict, fc.getId()));

        financeConditionBonus.setText(String.valueOf(fc.bonus));
        financeConditionBonus.addTextChangedListener(new TextValidator(getActivity(), financeConditionBonus, fc.getId()));

        financeConditionAlimony.setText(fc.alimony.toString());
        financeConditionAlimony.addTextChangedListener(new TextValidator(getActivity(), financeConditionAlimony, fc.getId()));

        financeConditionAlimonyProc.setText(String.valueOf(fc.alimony_proc));
        financeConditionAlimonyProc.addTextChangedListener(new TextValidator(getActivity(), financeConditionAlimonyProc, fc.getId()));

        financeConditionResidue.setText(fc.residue.toString());
        financeConditionResidue.addTextChangedListener(new TextValidator(getActivity(), financeConditionResidue, fc.getId()));

        financeConditionResidueProc.setText(String.valueOf(fc.residue_proc));
        financeConditionResidueProc.addTextChangedListener(new TextValidator(getActivity(), financeConditionResidueProc, fc.getId()));

        enableTax.setChecked(fc.enable_tax);
        return v;
    }

    public void onEvent(TextEvent event) {
        if (fc.getId().equals(event.mId)) {
            switch (event.mTextEditId) {
                case R.id.finance_condition_salary:
                    fc.salary = new BigDecimal(event.mValue);
                    fc.save();
                    break;
                case R.id.finance_condition_addition:
                    fc.addition = new BigDecimal(event.mValue);
                    fc.save();
                    break;
                case R.id.finance_condition_addition_proc:
                    fc.addition_proc = Double.valueOf(event.mValue);
                    fc.save();
                    break;
                case R.id.finance_condition_north:
                    fc.north = Double.valueOf(event.mValue);
                    fc.save();
                    break;
                case R.id.finance_condition_district:
                    fc.district = Double.valueOf(event.mValue);
                    fc.save();
                    break;
                case R.id.finance_condition_bonus:
                    fc.bonus = Double.valueOf(event.mValue);
                    fc.save();
                    break;
                case R.id.finance_condition_alimony:
                    fc.alimony = new BigDecimal(event.mValue);
                    fc.save();
                    break;
                case R.id.finance_condition_alimony_proc:
                    fc.alimony_proc = Double.valueOf(event.mValue);
                    fc.save();
                    break;
                case R.id.finance_condition_residue:
                    fc.residue = new BigDecimal(event.mValue);
                    fc.save();
                    break;
                case R.id.finance_condition_residue_proc:
                    fc.residue_proc = Double.valueOf(event.mValue);
                    fc.save();
                    break;
                default:
                    break;
            }
        }
    }
}
