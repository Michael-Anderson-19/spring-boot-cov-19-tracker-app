package anderson.michael.CovidTracker.controllers;

import anderson.michael.CovidTracker.models.LocationStats;
import anderson.michael.CovidTracker.services.CovidDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    CovidDataService covidDataService;


    @GetMapping("/")
    public String home(Model model, @RequestParam(value = "search", required = false) String search) {
        List<LocationStats> allStats = this.covidDataService.getAllStats();
        boolean isSearch = false;
        boolean emptyResults = false;
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotal()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();

        if (search != null && search.trim().length() != 0) {
            allStats = allStats.stream()
                    .filter(stat -> stat.getCountry().toLowerCase().contains(search.toLowerCase())).collect(Collectors.toList());
            isSearch = true;
        }
        if (allStats.size() == 0) {
            emptyResults = true;
        }
        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);
        model.addAttribute("isSearch", isSearch);
        model.addAttribute("emptyResults", emptyResults);


        return "home";
    }


}
