package simulacion;

import java.time.LocalDateTime;

import org.joda.time.DateTime;

public class Tps {
	private DateTime time;
	private Boolean prioridadAlta;
	public DateTime getTime() {
		return time;
	}
	public void setTime(DateTime time) {
		this.time = time;
	}
	public boolean getPrioridadAlta() {
		return prioridadAlta;
	}
	public void setPrioridadAlta(Boolean prioridadAlta) {
		this.prioridadAlta = prioridadAlta;
	}

}
