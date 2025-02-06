package com.example.demo;

import org.json.JSONObject;

public class Outputs {
    public static String invalidJSON()
    {
        JSONObject nullFile = new JSONObject();
        nullFile.put("file", JSONObject.NULL);
        nullFile.put("error", "Invalid JSON input.");
        return nullFile.toString();
    }




    public static String errorStoringFile(String fileName)

    {
        JSONObject errorStoringFile = new JSONObject();
        errorStoringFile.put("file", fileName);
        errorStoringFile.put("error", "Error while storing the file to the storage.");
        return  errorStoringFile.toString();
    }

    public static String notCSV(String fileName)

    {
        JSONObject errorStoringFile = new JSONObject();
        errorStoringFile.put("file", fileName);
        errorStoringFile.put("error", "Input file not in CSV format.");
        return  errorStoringFile.toString();
    }
    public static String notFound(String fileName)

    {
        JSONObject errorStoringFile = new JSONObject();
        errorStoringFile.put("file", fileName);
        errorStoringFile.put("error", "File not found.");
        return  errorStoringFile.toString();
    }

    public static String success(String fileName)
    {
        JSONObject jsonOutput = new JSONObject();
        jsonOutput.put("file",fileName);
        jsonOutput.put("message","Success.");
        return jsonOutput.toString();
    }

}
