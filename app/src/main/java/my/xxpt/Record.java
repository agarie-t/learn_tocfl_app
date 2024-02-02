package my.xxpt;


public class Record {
    private int id;
    private int score;

    public Record(int id, int score) {
        this.id = id;
        this.score = score;
    }

    public Record() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", score=" + score +
                '}';
    }
}
