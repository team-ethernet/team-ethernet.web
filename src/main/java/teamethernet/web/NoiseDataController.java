package teamethernet.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class NoiseDataController {

    @Autowired
    NoiseDataRepository noiseDataRepository;

    @GetMapping(path = "/visualization")
    public String getVisualization(Model model) {
        List<NoiseData> noiseData = getNoiseData();
        model.addAttribute("NoiseData", noiseData);

        return "visual";
    }


    @GetMapping(path = "/data")
    public @ResponseBody
    Iterable<NoiseData> getData(Model model) throws JsonProcessingException{
        return noiseDataRepository.findAll();
    }

    private List<NoiseData> getNoiseData() {
        List<NoiseData> noiseData = new ArrayList<>();
        noiseDataRepository.findAll().forEach(noiseData::add);

        return noiseData;
    }

}