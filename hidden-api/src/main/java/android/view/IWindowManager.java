package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;

public interface IWindowManager extends IInterface {

    abstract class Stub extends Binder implements IWindowManager {

        public static IWindowManager asInterface(IBinder obj) {
            throw new UnsupportedOperationException();
        }
    }
}
