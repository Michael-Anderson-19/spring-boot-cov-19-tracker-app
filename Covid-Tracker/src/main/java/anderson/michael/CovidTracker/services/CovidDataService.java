package anderson.michael.CovidTracker.services;

import anderson.michael.CovidTracker.models.LocationStats;
import anderson.michael.CovidTracker.repositories.LocationStatsRepo;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


@Service
public class CovidDataService {
    //on load get the data from the covid cases github, recieved as a csv file

    @Autowired
    private LocationStatsRepo locationStatsRepo;

    private static String COVID_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    public List<LocationStats> getAllStats() {
        List<LocationStats> stats = this.locationStatsRepo.findAll();
        return stats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        //list ot hold the new stats
        List<LocationStats> newStats = new ArrayList<>();


        //create http client
        HttpClient client = HttpClient.newHttpClient();
        //create new request to uri built from string of url
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(COVID_DATA_URL)).build();
        //use the client to send the request and return it as a string, store in a response obejct
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        StringReader csvBodyReader = new StringReader(httpResponse.body()); //input reader from strings
        //the commons.csv library for parsing csv files
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            //create new instance of the locationstat class
            LocationStats locationStat = new LocationStats();
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int previousDaysCases = Integer.parseInt(record.get(record.size() - 2));
            locationStat.setLatestTotal(latestCases);
            locationStat.setDiffFromPrevDay(latestCases - previousDaysCases);
            newStats.add(locationStat);

        }


        //remove all previous results
        this.locationStatsRepo.deleteAll();
        //add new results to database
        this.locationStatsRepo.saveAll(newStats);
    }


}
