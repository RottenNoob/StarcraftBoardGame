package gameEntities;

import java.util.concurrent.atomic.AtomicLong;

public class UnitIdGenerator implements java.io.Serializable {
	
	private static final long serialVersionUID = 9203137671645080349L;
	private AtomicLong currentValue = new AtomicLong(0L);
    
    public long getNextValue() {
        return currentValue.getAndIncrement();
    }
}
