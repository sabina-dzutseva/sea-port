package service1;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RestService {

    @GetMapping(value = "/service1")
    @ResponseStatus(HttpStatus.OK)
    public Schedules getGeneratedSchedule() {
        return new Schedules(ScheduleGenerator.generateSchedule());
    }

}
