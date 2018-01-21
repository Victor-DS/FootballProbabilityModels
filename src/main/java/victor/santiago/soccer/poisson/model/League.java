package victor.santiago.soccer.poisson.model;

import java.util.List;

import lombok.Data;

@Data
public class League implements Comparable<League> {
    private String champion, name;
    private List<Match> matches;
    private int year;

    @Override
    public int compareTo(League o) {
        return Integer.compare(this.getYear(), o.getYear());
    }
}
