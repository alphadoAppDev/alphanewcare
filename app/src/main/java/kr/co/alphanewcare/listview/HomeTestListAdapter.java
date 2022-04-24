package kr.co.alphanewcare.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import kr.co.alphanewcare.R;
import kr.co.alphanewcare.utils.PetInfo;
import kr.co.alphanewcare.utils.TestHistory;

public class HomeTestListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private PetInfo mPetInfo;
    private TestHistory mTestHistory;

    public HomeTestListAdapter(Context context, PetInfo petInfo) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mPetInfo = petInfo;
    }

    @Override
    public int getCount() {
        return mPetInfo.getTestHistorySize();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_test, null);
            holder = new ViewHolder();

            holder.mTvTestNumber = convertView.findViewById(R.id.tv_test_number);
            holder.mTvDate = convertView.findViewById(R.id.tv_date);
            holder.mTvTime = convertView.findViewById(R.id.tv_time);
            holder.mTvTestNormal = convertView.findViewById(R.id.tv_test_noraml);
            holder.mTvTestDoubt = convertView.findViewById(R.id.tv_test_doubt);
            holder.mTvTestAlert = convertView.findViewById(R.id.tv_test_alert);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        mTestHistory = mPetInfo.getTestHistory(position);
        setStatusNum(mTestHistory);
        holder.mTvTestNumber.setText(mTestHistory.getNumber() + "");
        holder.mTvDate.setText(mTestHistory.getDate());
        holder.mTvTime.setText(mTestHistory.getTime());
        holder.mTvTestNormal.setText(mContext.getResources().getString(R.string.test_result_normal) + " " + normal);
        holder.mTvTestDoubt.setText(mContext.getResources().getString(R.string.test_result_doubt) + " " + doubt);
        holder.mTvTestAlert.setText(mContext.getResources().getString(R.string.test_result_alert) + " " + alert);
        return convertView;
    }

    class ViewHolder {
        TextView mTvTestNumber;
        TextView mTvDate;
        TextView mTvTime;
        TextView mTvTestNormal;
        TextView mTvTestDoubt;
        TextView mTvTestAlert;
    }

    int normal = 0;
    int doubt = 0;
    int alert = 0;


    private void setStatusNum(TestHistory history) {
        normal = 0;
        doubt = 0;
        alert = 0;

        if (history.getBlood() == 1) {
            normal += 1;
        } else if (history.getBlood() == 2 || history.getBlood() == 3) {
            doubt += 1;
        } else if (history.getBlood() == 4 || history.getBlood() == 5) {
            alert += 1;
        }

        if (history.getBilirubin() == 1) {
            normal += 1;
        } else if (history.getBilirubin() == 2 || history.getBilirubin() == 3) {
            doubt += 1;
        } else if (history.getBilirubin() == 4) {
            alert += 1;
        }

        if (history.getUrobilinogen() == 1 || history.getUrobilinogen() == 2) {
            normal += 1;
        } else if (history.getUrobilinogen() == 3) {
            doubt += 1;
        } else if (history.getUrobilinogen() == 4 || history.getUrobilinogen() == 5) {
            alert += 1;
        }

        if (history.getKetones() == 1 || history.getKetones() == 2) {
            normal += 1;
        } else if (history.getKetones() == 3 || history.getKetones() == 4) {
            doubt += 1;
        } else if (history.getKetones() == 5 || history.getKetones() == 6) {
            alert += 1;
        }

        if (history.getProtein() == 1 || history.getProtein() == 2) {
            normal += 1;
        } else if (history.getProtein() == 3 || history.getProtein() == 4) {
            doubt += 1;
        } else if (history.getProtein() == 5 || history.getProtein() == 6) {
            alert += 1;
        }

        if (history.getNitrite() == 1) {
            normal += 1;
        } else if (history.getNitrite() == 2) {
            alert += 1;
        }

        if (history.getGlucose() == 1 || history.getGlucose() == 2) {
            normal += 1;
        } else if (history.getGlucose() == 3 || history.getGlucose() == 4) {
            doubt += 1;
        } else if (history.getGlucose() == 5 || history.getGlucose() == 6) {
            alert += 1;
        }

        if (history.getPh() == 2 || history.getPh() == 3 || history.getPh() == 4) {
            normal += 1;
        } else if (history.getPh() == 5 || history.getPh() == 6) {
            doubt += 1;
        } else if (history.getPh() == 1 || history.getPh() == 7) {
            alert += 1;
        }

        if (history.getSg() == 2 || history.getSg() == 3 || history.getSg() == 4 || history.getSg() == 5) {
            normal += 1;
        } else if (history.getSg() == 1 || history.getSg() == 6) {
            doubt += 1;
        } else if (history.getSg() == 7) {
            alert += 1;
        }

        if (history.getLeukocytes() == 1 || history.getLeukocytes() == 2) {
            normal += 1;
        } else if (history.getLeukocytes() == 3 || history.getLeukocytes() == 4) {
            doubt += 1;
        } else if (history.getLeukocytes() == 5) {
            alert += 1;
        }
    }

//    private void setStatusNum(TestHistory history) {
//        normal = 0;
//        doubt = 0;
//        alert = 0;
//        if (history.getBlood() == 1) {
//            normal += 1;
//        } else if (history.getBlood() == 2 || history.getBlood() == 3) {
//            doubt += 1;
//        } else if (history.getBlood() == 4 || history.getBlood() == 5) {
//            alert += 1;
//        }
//
//        if (history.getBilirubin() == 1) {
//            normal += 1;
//        } else if (history.getBilirubin() == 2 || history.getBilirubin() == 3) {
//            doubt += 1;
//        } else if (history.getBilirubin() == 4) {
//            alert += 1;
//        }
//
//        if (history.getUrobilinogen() == 1 || history.getUrobilinogen() == 2) {
//            normal += 1;
//        } else if (history.getUrobilinogen() == 3 || history.getUrobilinogen() == 4) {
//            doubt += 1;
//        } else if (history.getUrobilinogen() == 5) {
//            alert += 1;
//        }
//
//        if (history.getKetones() == 1 || history.getKetones() == 2) {
//            normal += 1;
//        } else if (history.getKetones() == 3 || history.getKetones() == 4) {
//            doubt += 1;
//        } else if (history.getKetones() == 5 || history.getKetones() == 6) {
//            alert += 1;
//        }
//
//        if (history.getProtein() == 1 || history.getProtein() == 2) {
//            normal += 1;
//        } else if (history.getProtein() == 3 || history.getProtein() == 4) {
//            doubt += 1;
//        } else if (history.getProtein() == 5 || history.getProtein() == 6) {
//            alert += 1;
//        }
//
//        if (history.getNitrite() == 1) {
//            normal += 1;
//        } else if (history.getNitrite() == 2) {
//            alert += 1;
//        }
//
//        if (history.getGlucose() == 1 || history.getGlucose() == 2) {
//            normal += 1;
//        } else if (history.getGlucose() == 3 || history.getGlucose() == 4) {
//            doubt += 1;
//        } else if (history.getGlucose() == 5 || history.getGlucose() == 6) {
//            alert += 1;
//        }
//
//        if (history.getPh() == 2 || history.getPh() == 3 || history.getPh() == 4) {
//            normal += 1;
//        } else if (history.getPh() == 5 || history.getPh() == 6) {
//            doubt += 1;
//        } else if (history.getPh() == 1 || history.getPh() == 7) {
//            alert += 1;
//        }
//
//        if (history.getSg() == 2 || history.getSg() == 3) {
//            normal += 1;
//        } else if (history.getSg() == 4 || history.getSg() == 5) {
//            doubt += 1;
//        } else if (history.getSg() == 1 || history.getSg() == 6 || history.getSg() == 7) {
//            alert += 1;
//        }
//
//        if (history.getLeukocytes() == 1 || history.getLeukocytes() == 2) {
//            normal += 1;
//        } else if (history.getLeukocytes() == 3 || history.getLeukocytes() == 4) {
//            doubt += 1;
//        } else if (history.getLeukocytes() == 5) {
//            alert += 1;
//        }
//    }
}
