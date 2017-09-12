package pero.fesb.hr.werewolfandroid;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PlayersAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;

    public PlayersAdapter(Context context) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return PlayersDataStorage.playersListViewData.size();
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null) view = mInflater.inflate(R.layout.each_player, viewGroup, false);

        final TextView playerName = view.findViewById(R.id.playerName);

        final MyPreferences myPreferences = new MyPreferences(mContext);

        final Player player = PlayersDataStorage.playersListViewData.get(position);

        playerName.setText(player.getPlayerName());

        // Theme related
        if (myPreferences.getString("themeColor").equals("pink")) {
            playerName.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhiteLetters));
        }
        // End theme

        return view;
    }
}
