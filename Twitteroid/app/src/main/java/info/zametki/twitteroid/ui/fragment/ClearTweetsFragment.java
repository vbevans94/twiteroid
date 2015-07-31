package info.zametki.twitteroid.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import info.zametki.twitteroid.R;

/**
 * @author vbevans94
 */
public class ClearTweetsFragment extends DialogFragment {

    private final static String TAG = ClearTweetsFragment.class.getSimpleName();

    public static void show(FragmentManager fragmentManager) {
        ClearTweetsFragment fragment = new ClearTweetsFragment();
        fragment.show(fragmentManager, TAG);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ClearTweetsListener)) {
            throw new ClassCastException(activity.toString() + " must implement ClearTweetsListener");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.title_warning))
                .setMessage(getString(R.string.message_clear_old_tweets))
                .setPositiveButton(getString(R.string.title_clear), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ClearTweetsListener) getActivity()).onClear();
                    }
                }).setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    public interface ClearTweetsListener {
        void onClear();
    }
}
