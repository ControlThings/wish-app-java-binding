package wish.request;

/**
 * Created by jan on 8/30/17.
 */

public class Host {

    /**
     *
     * @param callback Host.ConfigCb
     * @return
     */
    public static int config(ConfigCb callback) {
       return HostConfig.request(callback);
    }

    public abstract static class ConfigCb extends Callback {
        public abstract void cb(String version);
    }
}
