package victor.santiago.soccer.poisson.model;

import java.util.Date;

import lombok.Data;

@Data
public class Match {
    private String home, away;
    private int homeGoals, awayGoals;
    private Date date;
}
