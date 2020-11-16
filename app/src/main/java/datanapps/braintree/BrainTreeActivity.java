package datanapps.braintree;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationError;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;

import com.braintreepayments.api.models.ThreeDSecureAdditionalInformation;
import com.braintreepayments.api.models.ThreeDSecurePostalAddress;
import com.braintreepayments.api.models.ThreeDSecureRequest;
import com.braintreegateway.Environment;

import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/*
 *
 *
 * */
public class BrainTreeActivity extends AppCompatActivity {


    private TextView tvPaymentResponse;


    private TextView tvRefundResponse;


    int REQUEST_CODE = 100;

    String amount = "100";
    private String clientToken;

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                refund();
            }
        });

        createClientToken();
        //String temp = encrypt("ahcs");

        //Log.d("asd", "ENCRYPT ---------- : "+temp);
    }


    private void createClientToken(){


        new Thread(){
            @Override
            public void run() {
                super.run();


                BraintreeGateway gateway = new BraintreeGateway(
                        Environment.SANDBOX,
                        "hv6ctxpsdbr73qtx",
                        "hvw7sd9536cbmzvf",
                        "46361868b654da46148f4e5993a406be"
                );



                CustomerRequest request = new CustomerRequest()
                        .firstName("Mark")
                        .lastName("Jones")
                        .email("mark.jones@example.com");

                Result<Customer> result = gateway.customer().create(request);

                result.isSuccess();
                // true

                String aCustomerId = result.getTarget().getId();

                ClientTokenRequest clientTokenRequest = new ClientTokenRequest()
                        .customerId(aCustomerId);
                        //.merchantAccountId("datanapps-CAD");
                clientToken = gateway.clientToken().generate(clientTokenRequest);
                System.out.println("******clientToken* :: "+clientToken);

                Log.d("asd", "client Token :"+clientToken);
            }
        }.start();

    }

    private void createDropInUI(){

        ThreeDSecurePostalAddress address = new ThreeDSecurePostalAddress()
                .givenName("Mark") // ASCII-printable characters required, else will throw a validation error
                .surname("Jones"); // ASCII-printable characters required, else will throw a validation error


        // For best results, provide as many additional elements as possible.
        ThreeDSecureAdditionalInformation additionalInformation = new ThreeDSecureAdditionalInformation()
                .shippingAddress(address);

        ThreeDSecureRequest threeDSecureRequest = new ThreeDSecureRequest()
                .amount(amount)
                .email("mark.jones@example.com")
                .billingAddress(address)
                .versionRequested(ThreeDSecureRequest.VERSION_2)
                .additionalInformation(additionalInformation);


        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(clientToken)
                .requestThreeDSecureVerification(true)
                .threeDSecureRequest(threeDSecureRequest)
                .collectDeviceData(true);

        startActivityForResult(dropInRequest.getIntent(BrainTreeActivity.this), REQUEST_CODE);

    }

    private void refund(){


        new Thread(){
            @Override
            public void run() {
                super.run();
                try {

        BraintreeGateway gateway = new BraintreeGateway(
                Environment.SANDBOX,
                "hv6ctxpsdbr73qtx",
                "hvw7sd9536cbmzvf",
                "46361868b654da46148f4e5993a406be" );

            Result<Transaction> result = gateway.transaction().refund("hnjsdd5y"); // e78v2a75

            if (result.isSuccess()) {
                tvRefundResponse.setText("Successful : " + result.getMessage());
            } else {
                for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
                    Log.d("asd", error.getMessage());
                }
            }
        }catch (Exception e){
            Log.d("asd Exception : ", e.getMessage());
        }

        // true
            }
        }.start();
    }



    @Override
    protected void
    onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

                tvPaymentResponse.setText("Nonce Id : " +result.getPaymentMethodNonce().getNonce());

                Log.d("asd", "Nonce Id : "+ result.getPaymentMethodNonce().getNonce());
                // use the result to update your UI and send the payment method nonce to your server
                completeTransactionNow(result.getDeviceData(),  result.getPaymentMethodNonce().getNonce());

            } else if (resultCode == RESULT_CANCELED) {
                tvPaymentResponse.setText("Cancelled.");
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);

                tvPaymentResponse.setText(error.getMessage());
            }
        }
    }


    private void completeTransactionNow(final String deviceDataFromTheClient, final String nonceFromTheClient) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {

                    BraintreeGateway gateway = new BraintreeGateway(
                            Environment.SANDBOX,
                            "hv6ctxpsdbr73qtx",
                            "hvw7sd9536cbmzvf",
                            "46361868b654da46148f4e5993a406be");

                    TransactionRequest request = new TransactionRequest()
                            .amount(new BigDecimal(amount))
                            //.merchantAccountId("datanapps-CAD")
                            .paymentMethodNonce(nonceFromTheClient)
                            .deviceData(deviceDataFromTheClient)
                            .options()
                            .submitForSettlement(true)
                            .done();

                    Result<Transaction> result = gateway.transaction().sale(request);
                    if (result.isSuccess()) {
                        // See result.getTarget() for details
                        Transaction transaction = result.getTarget();
                        Log.d("asd", "If part : "+transaction.getId());
                        tvPaymentResponse.setText(tvPaymentResponse .getText().toString()+" \n\n TransactionId: " +transaction.getId());

                    } else {
                        // Handle errors
                        Log.d("asd", "else part : "+result.getMessage());
                    }
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }

        }.start();
    }

}