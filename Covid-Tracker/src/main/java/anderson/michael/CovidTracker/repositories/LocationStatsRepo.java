package anderson.michael.CovidTracker.repositories;

import anderson.michael.CovidTracker.models.LocationStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationStatsRepo extends JpaRepository<LocationStats, Long> {
}
