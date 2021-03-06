import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

//
public class Server {

    final static String DOMAINNAME = "jhcoder.top";

    public static void main(String[] args) {
        Log.init(Server.class);
        String ip = AliUtils.getAliIp(DOMAINNAME);
        String recordId = AliUtils.getAliRecordId(DOMAINNAME);
        Log.info("程序开始运行.....");
        if(ip.equals("") || recordId.equals("")) {
            Log.error("请求错误...不开启执行器...");
        }
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new CheakIp(ip, recordId), 0,1, TimeUnit.MINUTES);
    }
}

class CheakIp implements Runnable {

    private String ip = "";
    private String reId = "3604589338286080";

    public CheakIp(String ip, String reId) {
        this.ip = ip;
        this.reId = reId;
    }

    @Override
    public void run() {
        try {
            Log.info("执行检查....  当前ip： " + ip + "  当前ReId:  " + reId);
            String nowIp = Iputils.getV4IP();
            if (nowIp.equals("")) {
                return;
            }
            if (!ip.equals(nowIp)) {
                //调用接口
                Object obj1 = HttpUtils.doGetBackJson(AliUtils.update(nowIp, reId), null);
                if (obj1 != null) {
                    Object obj2 = HttpUtils.doGetBackJson(AliUtils.enable(nowIp, reId), null);
                    if (obj2 != null) {
                        Log.info("更新了ip地址...." + ip + "---->" + nowIp);
                        ip = nowIp;
                    }
                }
            }
        }catch (Exception e) {
            Log.error("执行出现错误，不执行线程操作...",e);
        }
    }
}
