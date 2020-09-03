package datanapps.braintree;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private TextView tvPaymentResponse;
    private TextView tvRefundResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewStuff();
    }

    private void initViewStuff() {

        tvPaymentResponse = findViewById(R.id.tvPaymentRespnse);
        tvRefundResponse= findViewById(R.id.tvRefundRespnse);
        findViewById(R.id.btnPayNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        findViewById(R.id.btnRefund).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}