package com.blackcatwalk.sharingpower.utility;


import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.content.Context.MODE_PRIVATE;

public class ControlFile {

    private static final String mFileUsername = "userName_sm.txt";
    private final String mStauslogin = "stausLogin_sm.txt";
    private final String mStausShared = "stausShared_sm.txt";
    private final String mStausRank = "stausRank_sm.txt";
    private final String mSetting = "setting_sm.txt";
    private final String mUserManual = "userManual_sm.txt";
    private final int mReadSize = 100; // Read 100byte

    public static String getUsername(Context _context) {
        try {
            FileInputStream _fIn = _context.openFileInput(mFileUsername);
            InputStreamReader _reader = new InputStreamReader(_fIn);

            char[] _buffer = new char[100];
            String _username = "";
            String _readString;
            int _charReadCount;
            while ((_charReadCount = _reader.read(_buffer)) > 0) {
                _readString = "";
                _readString = String.copyValueOf(_buffer, 0, _charReadCount);
                _username += _readString;
                _buffer = new char[100];
            }
            _reader.close();
            return _username;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return "not found";
    }

    public String getFile(Context _context, String _file) {

        String _tempFile = null;

        switch (_file){
            case "stausLogin":
                _tempFile = mStauslogin;
                break;
            case "stausShared":
                _tempFile = mStausShared;
                break;
            case "stausRank":
                _tempFile = mStausRank;
                break;
            case "setting":
                _tempFile = mSetting;
                break;
            case "userManual":
                _tempFile = mUserManual;
                break;
        }

        try {
            FileInputStream _fIn = _context.openFileInput(_tempFile);
            InputStreamReader _reader = new InputStreamReader(_fIn);

            char[] _buffer = new char[mReadSize];
            String _result = "";
            String _readString;
            int _charReadCount;
            while ((_charReadCount = _reader.read(_buffer)) > 0) {
                _readString = "";
                _readString = String.copyValueOf(_buffer, 0, _charReadCount);
                _result += _readString;
                _buffer = new char[mReadSize];
            }
            _reader.close();
            return _result;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return "not found";
    }

    //----------------------  Write File ----------------------//

    public void setFile(Context _context, String _value, String _file) {

        String _tempFile = null;

        switch (_file){
            case "userName":
                _tempFile = mFileUsername;
                break;
            case "stausLogin":
                _tempFile = mStauslogin;
                break;
            case "stausShared":
                _tempFile = mStausShared;
                break;
            case "stausRank":
                _tempFile = mStausRank;
                break;
            case "userManual":
                _tempFile = mUserManual;
                break;
        }

        try {
            FileOutputStream _fOut = _context.openFileOutput(_tempFile, MODE_PRIVATE);
            OutputStreamWriter _writer = new OutputStreamWriter(_fOut);
            _writer.write(_value);
            _writer.flush();
            _writer.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void setSetting(Context _context, String _stausTaffic, String _stausMapType, String _stausNearby) {
        try {
            FileOutputStream fOut = _context.openFileOutput(mSetting, MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fOut);

            writer.write(_stausTaffic + _stausMapType + _stausNearby);
            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
