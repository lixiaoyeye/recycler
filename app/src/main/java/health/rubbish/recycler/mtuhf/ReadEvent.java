package health.rubbish.recycler.mtuhf;

/**
 * Created by xiayanlei on 2016/11/25.
 */
public class ReadEvent {

    private ReadMode readMode;

    public ReadEvent(ReadMode readMode) {
        this.readMode = readMode;
    }

    public ReadMode getReadMode() {
        return readMode;
    }

    public void setReadMode(ReadMode readMode) {
        this.readMode = readMode;
    }
}
