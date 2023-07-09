

package com.mirea.productapp.dialog;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mirea.productapp.R;

import org.jetbrains.annotations.NotNull;

public class TextInputDialog extends DialogFragment {
    private static final String TAG = DialogFragment.class.getSimpleName();
    private static final String KEY_MESSAGE = "MESSAGE";
    private static final String KEY_INPUT = "INPUT";
    private static final String KEY_HINT = "INPUT";
    private TextInputDialogListener listener;
    private String message;
    private String hint;
    private int action;
    private EditText inputField;


    public interface TextInputDialogListener {
        void onInputComplete(String input, int action);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        Fragment owner = getParentFragment();
        if (ctx instanceof TextInputDialogListener) {
            listener = (TextInputDialogListener) ctx;
        } else if (owner instanceof TextInputDialogListener) {
            listener = (TextInputDialogListener) owner;
        } else {
            Log.e(TAG, "Dialog not attached");
        }
    }

    @NotNull
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @NotNull ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        String inputText = "";
        if (savedInstanceState != null) {
            message = savedInstanceState.getString(KEY_MESSAGE);
            hint = savedInstanceState.getString(KEY_HINT);
            inputText = savedInstanceState.getString(KEY_INPUT);
        }

        View dialogRoot = inflater.inflate(R.layout.dialog_text_input, container, false);
        TextView label = dialogRoot.findViewById(R.id.dialog_label);
        Button cancelButton = dialogRoot.findViewById(R.id.button_dialog_cancel);
        Button okButton = dialogRoot.findViewById(R.id.button_dialog_ok);
        label.setText(message);

        inputField = dialogRoot.findViewById(R.id.dialog_text_field);
        inputField.setHint(hint);
        inputField.setText(inputText);
        inputField.requestFocus();

        inputField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onInputComplete();
                return true;
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInputComplete();
            }
        });

        return dialogRoot;
    }

    private void onInputComplete() {
        String input = inputField.getText().toString();
        if (onValidateInput(input)) {
            listener.onInputComplete(input, action);
            dismiss();
        }
    }

    public boolean onValidateInput(String input) {
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_MESSAGE, message);
        outState.putString(KEY_HINT, hint);
        outState.putString(KEY_INPUT, inputField.getText().toString());
    }

    public static class Builder {
        private final FragmentActivity activity;
        private TextInputDialog dialog;
        private FragmentManager fragmentManager;

        public Builder(FragmentActivity activity, Class<? extends TextInputDialog> clazz) {
            this.activity = activity;
            try {
                this.dialog = clazz.newInstance();
            } catch (java.lang.InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Cannot start dialog" + clazz.getSimpleName());
            }
        }

        public Builder setMessage(String message) {
            dialog.message = message;
            return this;
        }

        public Builder setMessage( int messageID) {
            return setMessage(activity.getString(messageID));
        }

        public Builder setHint(String hint) {
            dialog.hint = hint;
            return this;
        }

        public Builder setHint( int hintID) {
            return setHint(activity.getString(hintID));
        }

        public Builder setAction(int action) {
            dialog.action = action;
            return this;
        }

        public Builder setFragmentManager(FragmentManager manager) {
            fragmentManager = manager;
            return this;
        }

        public Builder setTargetFragment(Fragment fragment, int requestCode) {
            dialog.setTargetFragment(fragment, requestCode);
            return this;
        }

        public void show() {
            if (fragmentManager == null) {
                fragmentManager = activity.getSupportFragmentManager();
            }
            dialog.show(fragmentManager, TAG);
        }
    }
}
