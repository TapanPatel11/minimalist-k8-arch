package org.example;


import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@RestController
@RequestMapping("/")
public class CalculateController {





    @PostMapping("/calculate")

    public ResponseEntity login(@RequestBody String jsonString) throws Exception {

        JSONObject obj = new JSONObject(jsonString);

        System.out.println("==============CONTAINER TWO CALLED============");
        int total=0;

        String product = obj.getString("product");
        String fileName = obj.getString("file");

        for (String jsonKey: obj.keySet()) {
            if(jsonKey.contains(product))
            {
                total+= obj.getInt(jsonKey);
            }

        }

        String sum = String.valueOf(total);
            JSONObject output = new JSONObject();
            output.put("file",fileName);
            output.put("sum",sum);


        return new ResponseEntity<>(output.toString(),HttpStatus.OK);
    }
    @GetMapping("/test")
    public ResponseEntity call() {

            return new ResponseEntity("Hello from container 2 !", HttpStatus.OK);


    }
}
