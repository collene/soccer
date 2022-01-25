package ca.collene.soccer.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Tally {
    private String teamName;
    private long wins = 0;
    private long ties = 0;
    private long losses = 0;
    private long unscored = 0;

    public enum TallyType {        
        WIN(3),
        LOSS(1),
        TIE(2),
        UNSCORED(0);

        private final int value;
        private TallyType(int value) {
            this.value = value;
        }

        public int getTallyTotalValue() {
            return value;
        }
    }

    public Tally() {

    }
    public Tally(String teamName, long wins, long ties, long losses, long unscored) {
        this.teamName = teamName;
        this.wins = wins;
        this.ties = ties;
        this.losses = losses;
        this.unscored = unscored;
    }

    public Tally(String teamName, List<TallyType> tallyTypes) {
        this.teamName = teamName;
        this.wins = tallyTypes.stream().filter(t -> t == TallyType.WIN).count();
        this.losses = tallyTypes.stream().filter(t -> t == TallyType.LOSS).count();
        this.ties = tallyTypes.stream().filter(t -> t == TallyType.TIE).count();
        this.unscored = tallyTypes.stream().filter(t -> t == TallyType.UNSCORED).count();
    }

    public long getTotal() {
        return getWins() * TallyType.WIN.getTallyTotalValue()
                + getLosses() * TallyType.LOSS.getTallyTotalValue()
                + getTies() * TallyType.TIE.getTallyTotalValue()
                + getUnscored() * TallyType.UNSCORED.getTallyTotalValue();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if(!(o instanceof Tally)) {
            return false;
        }
        Tally other = (Tally) o;
        return Objects.equals(this.teamName, other.teamName) 
            && this.wins == other.wins
            && this.losses == other.losses
            && this.ties == other.ties
            && this.unscored == other.unscored;        
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamName, wins, losses, ties, unscored);
    }

    public static class With {
        private String teamName;
        private long wins = 0;
        private long ties = 0;
        private long losses = 0;
        private long unscored = 0;
        private List<TallyType> tallyTypes = new ArrayList<>();

        public With() {

        }
        public With teamName(String name) {
            this.teamName = name;
            return this;
        }

        public With wins(long wins) {
            this.wins = wins;
            return this;
        }

        public With ties(long ties) {
            this.ties = ties;
            return this;
        }
        public With losses(long losses) {
            this.losses = losses;
            return this;
        }
        public With unscored(long unscored) {
            this.unscored = unscored;
            return this;
        }
        public With tallyTypes(List<TallyType> tallyTypes) {
            this.tallyTypes = tallyTypes;
            return this;
        }

        public Tally build() {
            if(!tallyTypes.isEmpty()) {
                return new Tally(teamName, tallyTypes);
            }
            return new Tally(teamName, wins, ties, losses, unscored);
        }
    }
}
