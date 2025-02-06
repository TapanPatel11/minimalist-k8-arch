
package com.example.demo.Controller;

import com.example.demo.Outputs;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;


@RestController
@RequestMapping("/")
public class DataController {


    public String fileName="";
    public JSONObject jsonContents = new JSONObject();
    public static String directoryPath = "/Tapan_PV_dir/";

    public String data="";
    static String containerTwoName = "container2endpoint";
    static String containerTwoPort = "8001";

    @PostMapping("/store-file")

    public ResponseEntity storeFile(@RequestBody String jsonString) throws Exception {
        JSONObject obj = new JSONObject(jsonString);

        try {
            fileName = obj.getString("file");
            data = obj.getString("data");
        } catch (Exception e) {
            return new ResponseEntity<>(Outputs.invalidJSON(), HttpStatus.OK);
        }

        //If the file name is not provided, a null error message is returned
        if (fileName == null || fileName.isEmpty() || fileName.isBlank()) {
            //null file
            return new ResponseEntity<>(Outputs.invalidJSON(), HttpStatus.OK);
        }

        File storedFile = createFileFromData(data, directoryPath + fileName);

        if (storedFile == null || !storedFile.exists()) {
            return new ResponseEntity<>(Outputs.errorStoringFile(fileName), HttpStatus.OK);
        }

        return new ResponseEntity<>(Outputs.success(fileName), HttpStatus.OK);


    }


    public File createFileFromData(String data, String fileName) {
        try {
            File myObj = new File(fileName);
            if (!myObj.createNewFile()) {
                myObj.delete();
            }
            myObj.createNewFile();
            Files.write(Paths.get(fileName), data.getBytes(), StandardOpenOption.CREATE);

            return myObj;
        } catch (Exception e) {
            System.out.println("Unable to create or append to file");
            System.out.println(e.getMessage());
        }
        return null;

    }

        @PostMapping("/calculate")

    public ResponseEntity passContents(@RequestBody String jsonString) {



            String product="";
            //fetch request data
        JSONObject obj = new JSONObject(jsonString);
        try {
            fileName = obj.getString("file");
             product = obj.getString("product");
            //If the file name is not provided
            if (fileName == null || fileName.isEmpty() || fileName.isBlank()) {
                //null file
                return new ResponseEntity<>(Outputs.invalidJSON(), HttpStatus.OK);
            }
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(Outputs.invalidJSON(), HttpStatus.OK);

        }



        try {
            System.out.println("[#] Reading =====> "+directoryPath+fileName );
            File myObj = new File(directoryPath + fileName);

            //if file is not found
            if (!myObj.exists()) {
                return new ResponseEntity<>(Outputs.notFound(fileName), HttpStatus.OK);
            }

            Scanner myReader = new Scanner(myObj);
            int i = 0;
            while (myReader.hasNextLine()) {
                i++;
                String data = myReader.nextLine();
                if (i == 1) {
                    String[] header = data.split(",");
                    try {
                        if (!header[0].equalsIgnoreCase("product") && !header[1].equalsIgnoreCase("amount")) {
                            return new ResponseEntity<>(Outputs.notCSV(fileName), HttpStatus.OK);
                        }
                    } catch (Exception e) {
                        return new ResponseEntity<>(Outputs.notCSV(fileName), HttpStatus.OK);
                    }
                }


                if (data.contains("product") || data.contains("amount")) {

                } else {
                    try {
                        String[] actualData = data.split(",");

                        String pr = actualData[0].trim();
                        int value = Integer.parseInt(actualData[1].trim());

                        if (pr.equalsIgnoreCase(product)) {
                            pr = pr + i++;
                        }
                        jsonContents.put(pr, value);


                        if (actualData.length != 2) {
                            return new ResponseEntity<>(Outputs.notCSV(fileName), HttpStatus.OK);
                        }
                    } catch (Exception e) {

                        //not CSV
                        return new ResponseEntity<>(Outputs.notCSV(fileName), HttpStatus.OK);
                    }
                }
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            //File not found
            return new ResponseEntity<>(Outputs.notFound(fileName), HttpStatus.OK);
        }

        jsonContents.put("file",fileName);
        jsonContents.put("product",product);
//        send post request to another container
        final String uri = "http://" + containerTwoName + ":" +containerTwoPort+"/calculate";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            System.out.println("[*] Sending post request to : " + uri);
            HttpEntity<String> request =
                    new HttpEntity<String>(jsonContents.toString(), headers);
            String personResultAsJsonStr =
                    restTemplate.postForObject(uri, request, String.class);
            System.out.println(personResultAsJsonStr);
            return new ResponseEntity(personResultAsJsonStr, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("[#] Error sending request to second container");
            System.out.println("[#] " + e.getMessage());
            return new ResponseEntity("Error sending request : "+e.getMessage(), HttpStatus.OK);

        }

//            return new ResponseEntity(jsonContents.toString(), HttpStatus.OK);


    }

    @GetMapping("/call")
    public ResponseEntity call() {

        final String uri = "http://" + containerTwoName + ":"+containerTwoPort+"/test";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            System.out.println("[*] Sending get request to : " + uri);
            HttpEntity<String> request =
                    new HttpEntity<String>( headers);
            String personResultAsJsonStr =
                    restTemplate.getForObject(uri,  String.class);
            System.out.println(personResultAsJsonStr);
            return new ResponseEntity(personResultAsJsonStr, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("[#] Error sending request to second container");
            System.out.println("[#] " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.OK);

        }

    }

}
