package com.hehe.hehexposedlocation.introduction;

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.hehe.hehexposedlocation.R;

public class ReferecnceActivity extends Activity {

    TextView display;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referecnce);
        display = (TextView) findViewById(R.id.ReferenceList);
        String HideMockLocation = "<a href=\"https://forum.xda-developers.com/member.php?u=7590815\" target=\"_blank\">HideMockLocation</a>,\n";
        String XposedDeveloper = "<a href=\"http://repo.xposed.info/users/rovo89\" target=\"_blank\">rovo89</a> " +
                " and " +
                "<a href=\"http://repo.xposed.info/users/tungstwenty\" target=\"_blank\">Tungstwenty</a>,\n ";
        String ProcessManager = "<a href=\"http://stackoverflow.com/users/1048340/jared-rummler\" target=\"_blank\">Jared Rummler</a>,\n";
        String IconDesign = "Icon design: \nde viajes by <a href=\"https://dribbble.com/laurareen\" target=\"_blank\">Laura Reen</a>";
        String ListToDisplay = HideMockLocation + "\n" + XposedDeveloper + "\n" + ProcessManager + "\n" + IconDesign;
        String msg = ListToDisplay;
        display.setText(Html.fromHtml(msg));
        display.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
