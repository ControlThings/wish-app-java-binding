package wishApp.request;

/**
 * Created by akaustel on 12/8/16.
 */

public interface CallbackInterface {
    public void err(int code, String msg);
    public void end();
}
