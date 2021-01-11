package kr.co.alphacare.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class SoapClient
{
    private final String tag = "SoapClient";
    private final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
    //private String SOAP_ADDRESS = "https://service.alphado.co.kr/AlphaCare/";
    private String SOAP_ADDRESS = "http://121.152.44.12/AlphaCare/";
    //private String SOAP_ADDRESS = "http://121.152.44.12/Dev/Alphado/";

    private Context context;
    private String action;
    private String data = null;



    public SoapClient(Context context, String action)
    {
        this.context = context;
        this.action = action;
    }

    public void SendMessage(boolean ok, String data) {
        Intent intent = new Intent(action);

        if (ok) intent.putExtra("RESULT", true);
        else intent.putExtra("RESULT", false);

        intent.putExtra("DATA", data);


        context.sendBroadcast(intent);
        //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private PropertyInfo GetPropertyInfo(String name, String value, Class type) {
        PropertyInfo pi;

        pi = new PropertyInfo();
        pi.setType(type);
        pi.setName(name);

        if (type == PropertyInfo.INTEGER_CLASS) pi.setValue(Integer.parseInt(value));
        else pi.setValue(value);

        return pi;
    }

    public void AddPetUrineResult(String email, String data)
    {
        String SOAP_ACTION;
        String OPERATION_NAME;

        SOAP_ADDRESS += "PetUrineService.asmx";
        OPERATION_NAME = "AddPetUrineResult";
        SOAP_ACTION = WSDL_TARGET_NAMESPACE + OPERATION_NAME;

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty(GetPropertyInfo("sEmail", email, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sValues", data, PropertyInfo.STRING_CLASS));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        RequestData(envelope, SOAP_ACTION, OPERATION_NAME);
    }

    public void GetPetInfo(String email, String data)
    {
        String SOAP_ACTION;
        String OPERATION_NAME;

        SOAP_ADDRESS += "PetInfoService.asmx";
        OPERATION_NAME = "GetPetInfo";
        SOAP_ACTION = WSDL_TARGET_NAMESPACE + OPERATION_NAME;

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty(GetPropertyInfo("sEmail", email, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sValues", data, PropertyInfo.STRING_CLASS));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        RequestData(envelope, SOAP_ACTION, OPERATION_NAME);
    }

    public void DeletePetInfo(String email, String data)
    {
        String SOAP_ACTION;
        String OPERATION_NAME;

        SOAP_ADDRESS += "PetInfoService.asmx";
        OPERATION_NAME = "DelPetInfo";
        SOAP_ACTION = WSDL_TARGET_NAMESPACE + OPERATION_NAME;

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty(GetPropertyInfo("sEmail", email, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sValues", data, PropertyInfo.STRING_CLASS));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        RequestData(envelope, SOAP_ACTION, OPERATION_NAME);
    }



    public void AddPet(String email, String data)
    {
        String SOAP_ACTION;
        String OPERATION_NAME;

        SOAP_ADDRESS += "PetInfoService.asmx";
        OPERATION_NAME = "AddPetInfo";
        SOAP_ACTION = WSDL_TARGET_NAMESPACE + OPERATION_NAME;

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty(GetPropertyInfo("sEmail", email, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sValues", data, PropertyInfo.STRING_CLASS));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        RequestData(envelope, SOAP_ACTION, OPERATION_NAME);
    }

    public void UpdatePetInfo(String email, String data)
    {
        String SOAP_ACTION;
        String OPERATION_NAME;

        SOAP_ADDRESS += "PetInfoService.asmx";
        OPERATION_NAME = "ChgPetInfo";
        SOAP_ACTION = WSDL_TARGET_NAMESPACE + OPERATION_NAME;

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty(GetPropertyInfo("sEmail", email, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sValues", data, PropertyInfo.STRING_CLASS));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        RequestData(envelope, SOAP_ACTION, OPERATION_NAME);
    }

    public void GetPetUrineHistory(String email, String petIndex)
    {
        String SOAP_ACTION;
        String OPERATION_NAME;

        SOAP_ADDRESS += "PetUrineService.asmx";
        OPERATION_NAME = "GetPetUrineHistory";
        SOAP_ACTION = WSDL_TARGET_NAMESPACE + OPERATION_NAME;

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty(GetPropertyInfo("sEmail", email, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sValues", petIndex, PropertyInfo.STRING_CLASS));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        RequestData(envelope, SOAP_ACTION, OPERATION_NAME);
    }

    public void VerifyUser(String email, String pass)
    {
        String SOAP_ACTION;
        String OPERATION_NAME;

        SOAP_ADDRESS += "AccountService.asmx";
        OPERATION_NAME = "VerifyUser";
        SOAP_ACTION = WSDL_TARGET_NAMESPACE + OPERATION_NAME;

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty(GetPropertyInfo("sEmail", email, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sPasswd", pass, PropertyInfo.STRING_CLASS));

        //Log.e("AAAAAAA", "VerifyUser: " + pass + ", " + email);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        RequestData(envelope, SOAP_ACTION, OPERATION_NAME);
    }

    public void ChangePassword(String email, String currentPassword, String changePassword)
    {
        String SOAP_ACTION;
        String OPERATION_NAME;

        SOAP_ADDRESS += "AccountService.asmx";
        OPERATION_NAME = "ChangePassword";
        SOAP_ACTION = WSDL_TARGET_NAMESPACE + OPERATION_NAME;

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);

        request.addProperty(GetPropertyInfo("sEmail", email, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sCurrPasswd", currentPassword, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sChgPasswd", changePassword, PropertyInfo.STRING_CLASS));

        //Log.e("AAAAAAA", "ChangePassword: " + currentPassword + ", " + changePassword);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        RequestData(envelope, SOAP_ACTION, OPERATION_NAME);
    }
    public void SetToken(String email, String token)
    {
        String SOAP_ACTION;
        String OPERATION_NAME;
        SOAP_ADDRESS += "AccountService.asmx";
        OPERATION_NAME = "SetToken";
        SOAP_ACTION = WSDL_TARGET_NAMESPACE + OPERATION_NAME;

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);

        request.addProperty(GetPropertyInfo("sEmailAddr", email, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sToken", token, PropertyInfo.STRING_CLASS));




        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        RequestData(envelope, SOAP_ACTION, OPERATION_NAME);
    }
    public void ResetPassword(String email, String key, String pass, int step)
    {
        String SOAP_ACTION;
        String OPERATION_NAME;

        SOAP_ADDRESS += "AccountService.asmx";
        OPERATION_NAME = "ResetPassword";
        SOAP_ACTION = WSDL_TARGET_NAMESPACE + OPERATION_NAME;

        Log.e("AAAAAA", "email: " + email + ", key: " + key + ", pass: " + pass + ", step: " + step);

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty(GetPropertyInfo("sEmail", email, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sRcvdAuthKey", key, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sNewPasswd", pass, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("nStep", "" + step, PropertyInfo.INTEGER_CLASS));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        RequestData(envelope, SOAP_ACTION, OPERATION_NAME);
    }

    public void CreateAccount(String email, String pass, String firstName)
    {
        String SOAP_ACTION;
        String OPERATION_NAME;

        SOAP_ADDRESS += "AccountService.asmx";
        SOAP_ACTION = "http://tempuri.org/CreateAccount";
        OPERATION_NAME = "CreateAccount";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty(GetPropertyInfo("sEmail", email, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sPasswd", pass, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sFirstName", firstName, PropertyInfo.STRING_CLASS));
        request.addProperty(GetPropertyInfo("sLastName", "", PropertyInfo.STRING_CLASS));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        RequestData(envelope, SOAP_ACTION, OPERATION_NAME);
    }


    private void RequestData(final SoapSerializationEnvelope envelope, final String soapAction, final String operationName) {


        new Thread(new Runnable() {
            public void run() {
                boolean success = false;
                HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
                Object response = null;
                try {
                    httpTransport.call(soapAction, envelope);
                    //SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
                    //Log.e("AAAAAAA", envelope.bodyIn.toString());
                    SoapObject soapResponse = (SoapObject) envelope.bodyIn;
                    response = soapResponse.getProperty(operationName + "Result");

                    success = true;

                } catch (Exception exception) {
                    exception.printStackTrace();
                    response = exception;
                    success = false;
                    Log.e(tag, "response: " + response.toString());
                } finally {
                    String ret;
                    if (response == null) ret = null;
                    else ret = response.toString();

                    Log.e(tag, "response: " + ret);

                    SendMessage(success, ret);
                }

            }

        }).start();
    }
}

