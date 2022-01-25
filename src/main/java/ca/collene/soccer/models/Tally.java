package ca.collene.soccer.models;

import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tally {
    private String teamName;
    @Builder.Default private long wins = 0;
    @Builder.Default private long ties = 0;
    @Builder.Default private long losses = 0;
    @Builder.Default private long unscored = 0;

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
}
