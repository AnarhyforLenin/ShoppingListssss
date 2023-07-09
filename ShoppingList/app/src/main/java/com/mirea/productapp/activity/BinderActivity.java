package com.mirea.productapp.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.mirea.productapp.shoppinglist.ShoppingListService;

public abstract class BinderActivity extends AppCompatActivity {

    private static final String TAG = "101";
    private final ShoppingListServiceConnection serviceConnection = new ShoppingListServiceConnection();
    private ShoppingListService.ShoppingListBinder binder = null;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ShoppingListService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    protected void onStop() {
        unbindService(serviceConnection);
        super.onStop();
    }

    public boolean isServiceConnected() {
        return binder != null;
    }

    protected ShoppingListService.ShoppingListBinder getBinder() {
        return binder;
    }

    protected abstract void onServiceConnected(ShoppingListService.ShoppingListBinder binder);

    protected abstract void onServiceDisconnected(ShoppingListService.ShoppingListBinder binder);

    private class ShoppingListServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            binder = ((ShoppingListService.ShoppingListBinder) iBinder);
            BinderActivity.this.onServiceConnected(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            BinderActivity.this.onServiceDisconnected(binder);
            binder = null;
        }
    }
}
