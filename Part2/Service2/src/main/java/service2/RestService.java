package service2;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import service3.ResultFormer;
import service1.Schedules;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static service2.JsonGenerator.jsonGenerate;
import static service2.JsonGenerator.scheduleDeserialize;

@RestController
@RequestMapping("/")
public class RestService {

    @GetMapping(value = "/service2/formedjson")
    @ResponseStatus(HttpStatus.OK)
    public File getJson() {

        final String URL = "http://localhost:8090";
        RestTemplate restTemplate = new RestTemplate();
        Schedules schedules = restTemplate.getForObject(URL + "/service1", Schedules.class);
        jsonGenerate(schedules.schedules);
        //scheduleConsoleInput();

        return new File(JsonGenerator.fileName);
    }

    @GetMapping(value = "/service2/getschedule")
    public Schedules getScheduleFromJson(@RequestParam("filename") String filename) {

        if (Files.exists(Path.of(filename))) {
            Schedules schedules = new Schedules();
            schedules.schedules = scheduleDeserialize(filename);
            return schedules;

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such filename");
        }

    }

    @PostMapping(value = "/service2/formresult")
    @ResponseStatus(HttpStatus.CREATED)
    public void formJson(@RequestBody ResultFormer resultFormer) {
        jsonGenerate(resultFormer);
    }


}
