package datanapps.braintree;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.ThreeDSecureAdditionalInformation;
import com.braintreepayments.api.models.ThreeDSecurePostalAddress;
import com.braintreepayments.api.models.ThreeDSecureRequest;


/*
 *
 * LETSLITAPP@gmail.com PASSWORD: LETSLITapp12
 * */
public class BrainTreeFragmentActivity extends AppCompatActivity {


    private TextView tvPaymentResponse;


    private TextView tvRefundResponse;

    private String AUTHORIZATION = "sandbox_mfmx8jzt_hv6ctxpsdbr73qtx";

    int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvPaymentResponse = findViewById(R.id.tvPaymentRespnse);
        tvRefundResponse = findViewById(R.id.tvRefundRespnse);



        findViewById(R.id.btnPayNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDropInUI();
            }
        });

        findViewById(R.id. btnRefund).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void createDropInUI(){

        ThreeDSecurePostalAddress address = new ThreeDSecurePostalAddress()
                .givenName("Jill") // ASCII-printable characters required, else will throw a validation error
                .surname("Doe") // ASCII-printable characters required, else will throw a validation error
                .phoneNumber("5551234567")
                .streetAddress("555 Smith St")
                .extendedAddress("#2")
                .locality("Chicago")
                .region("IL")
                .postalCode("12345")
                .countryCodeAlpha2("US");

        // For best results, provide as many additional elements as possible.
        ThreeDSecureAdditionalInformation additionalInformation = new ThreeDSecureAdditionalInformation()
                .shippingAddress(address);

        ThreeDSecureRequest threeDSecureRequest = new ThreeDSecureRequest()
                .amount("10")
                .email("test@email.com")
                .billingAddress(address)
                .versionRequested(ThreeDSecureRequest.VERSION_2)
                .additionalInformation(additionalInformation);


        DropInRequest dropInRequest = new DropInRequest()
                .tokenizationKey(AUTHORIZATION)
                .requestThreeDSecureVerification(true)
                .threeDSecureRequest(threeDSecureRequest);

        startActivityForResult(dropInRequest.getIntent(BrainTreeFragmentActivity.this), REQUEST_CODE);

    }


    @Override
    protected void
    onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

                tvPaymentResponse.setText(result.getPaymentMethodNonce().getNonce());
                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == RESULT_CANCELED) {
                tvPaymentResponse.setText("Cancelled.");
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);

                tvPaymentResponse.setText(error.getMessage());
            }
        }
    }

}