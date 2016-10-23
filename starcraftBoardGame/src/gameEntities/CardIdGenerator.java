package gameEntities;

import java.util.concurrent.atomic.AtomicInteger;

public class CardIdGenerator implements java.io.Serializable {

	private static final long serialVersionUID = -8109882456384790208L;
	private AtomicInteger currentValue = new AtomicInteger(0);
    
    public int getNextValue() {
        return currentValue.getAndIncrement();
    }
}
