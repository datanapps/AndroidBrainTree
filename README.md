# AndroidBrainTree
https://datanapps.com/

<img src="https://github.com/datanapps/AndroidBrainTree/blob/master/screens/screen1.png" height="500" width="250"> <img src="https://github.com/datanapps/AndroidBrainTree/blob/master/screens/screen2.png" height="500" width="250"> <img src="https://github.com/datanapps/AndroidBrainTree/blob/master/screens/screen3.jpg" height="500" width="250">  


### clientToken will generate from server side.

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


### create client side payment and get nonce id.

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
    
    
   #### Get Nonce Id.
    
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

    
    
   #### Get Nonce id and make transaction onserver side Again.(ServerSide)
    
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
    
    
    
    
    <img src="https://datanapps.com/public/dnarestapi/naughty_smile.jpg" height="200" width="300">
 
 
 [![See](https://datanapps.com/public/dnarestapi/buy/buy_coffee2.png)](https://www.paypal.me/datanappspaynow)

  ### License

Copyright [2020] [datanapps]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   http://www.apache.org/licenses/LICENSE-2.0


    
