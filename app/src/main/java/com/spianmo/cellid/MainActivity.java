package com.spianmo.cellid;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView result = findViewById(R.id.result);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
        }else {
            final Handler handler=new Handler();
            Runnable runnable=new Runnable() {
                @Override
                public void run() {
                    result.setText(listToString(getTowerInfo(MainActivity.this)));
                    handler.postDelayed(this, 2000);
                }
            };
            handler.postDelayed(runnable, 2000);//每两秒执行一次runnable.
        }
        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyToClipboard(MainActivity.this,listToString(getTowerInfo(MainActivity.this)));
            }
        });
    }
    public static String listToString(List<String> mList) {
        String convertedListStr = "";
        if (null != mList && mList.size() > 0) {
            String[] mListArray = mList.toArray(new String[mList.size()]);
            for (int i = 0; i < mListArray.length; i++) {
                if (i < mListArray.length - 1) {
                    convertedListStr += mListArray[i];
                } else {
                    convertedListStr += mListArray[i];
                }
            }
            return convertedListStr;
        } else return "List is null!!!";
    }

    void copyToClipboard(Context context, String text) {
        ClipboardManager systemService = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        assert systemService != null;
        systemService.setPrimaryClip(ClipData.newPlainText("text", text));
        Toasty.success(MainActivity.this, "复制成功！").show();
    }
    public List<String> getTowerInfo(Context context) {
        int mcc = -1;
        int mnc = -1;
        int lac = -1;
        int cellId = -1;
        int rssi = -1;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getNetworkOperator();
        mcc = Integer.parseInt(operator.substring(0, 3));
        List<String> list = new ArrayList<String>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
        }
        List<CellInfo> infos = tm.getAllCellInfo();
        Log.e("1212","info:"+infos);
        int iv = 0;
        for (CellInfo info : infos){
            if (info instanceof CellInfoCdma){
                CellInfoCdma cellInfoCdma = (CellInfoCdma) info;
                CellIdentityCdma cellIdentityCdma = cellInfoCdma.getCellIdentity();
                mnc = cellIdentityCdma.getSystemId();
                lac = cellIdentityCdma.getNetworkId();
                cellId = cellIdentityCdma.getBasestationId();
                CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                rssi = cellSignalStrengthCdma.getCdmaDbm();
            }else if (info instanceof CellInfoGsm){
                CellInfoGsm cellInfoGsm = (CellInfoGsm) info;
                CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
                mnc = cellIdentityGsm.getMnc();
                lac = cellIdentityGsm.getLac();
                cellId = cellIdentityGsm.getCid();
                CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
                rssi = cellSignalStrengthGsm.getDbm();
            }else if (info instanceof CellInfoLte){
                CellInfoLte cellInfoLte = (CellInfoLte) info;
                CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
                mnc = cellIdentityLte.getMnc();
                lac = cellIdentityLte.getTac();
                cellId = cellIdentityLte.getCi();
                CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                rssi = cellSignalStrengthLte.getDbm();
            }else if (info instanceof CellInfoWcdma){
                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) info;
                CellIdentityWcdma cellIdentityWcdma = null;
                CellSignalStrengthWcdma cellSignalStrengthWcdma = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
                    mnc = cellIdentityWcdma.getMnc();
                    lac = cellIdentityWcdma.getLac();
                    cellId = cellIdentityWcdma.getCid();
                    cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                    rssi = cellSignalStrengthWcdma.getDbm();
                }
            }else {
                Log.e("1212","get CellInfo error");
                return null;
            }
            iv += 1;
            String tower = "#########################\n第" + iv + "条基站信息" + "\nMCC:" + String.valueOf(mcc) + "\nMNC:" + String.valueOf(mnc) + "\nLAC:" + String.valueOf(lac)
                    + "\nCellID:" + String.valueOf(cellId) + "\nRSSI:" + String.valueOf(rssi) + "\n";
            list.add(tower);
        }
        if (list.size() > 6){
            list = list.subList(0, 5);
        }else if (list.size() < 3){
            int need = 3 - list.size();
            for (int i = 0; i < need; i++) {
                list.add("");
            }
        }
        return list;

    }
}
