package wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Yellow on 2018-1-8.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
    //登录微信的应用ID
    private static final String APP_ID="wxd8e1494ccbebbd1f";
    //和微信通信的openapi接口
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
/*        api= WXAPIFactory.createWXAPI(this,APP_ID,false);
        api.registerApp(APP_ID);*/
    }

    @Override
    public void onReq(BaseReq req){

    }
    @Override
    public void onResp(BaseResp resp){
        switch (resp.errCode){
            case BaseResp.ErrCode.ERR_OK:
                Toast.makeText(this,"分享成功",Toast.LENGTH_SHORT).show();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Toast.makeText(this,"分享已取消",Toast.LENGTH_SHORT).show();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(this,"无分享许可",Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this,"分享失败"+resp.errCode,Toast.LENGTH_SHORT).show();
                break;
        }
        this.finish();
    }

}
